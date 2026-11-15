package com.example.domain

import android.content.Context
import android.net.Uri
import android.os.StatFs
import android.provider.OpenableColumns
import com.example.data.model.DocumentChunk
import com.example.data.model.NotebookSource
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.io.MemoryUsageSetting
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FilterInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.util.Locale
import kotlin.coroutines.coroutineContext

enum class DocumentFormat(val label: String, val extensions: Set<String>, val mimeTypes: Set<String>) {
    PDF("PDF", setOf("pdf"), setOf("application/pdf")),
    TXT("TXT", setOf("txt", "log", "text"), setOf("text/plain")),
    MD("MD", setOf("md", "markdown"), setOf("text/markdown", "text/x-markdown")),
    CSV("CSV", setOf("csv", "tsv"), setOf("text/csv", "text/comma-separated-values", "application/csv", "text/tab-separated-values")),
    JSON("JSON", setOf("json", "jsonl"), setOf("application/json", "text/json"))
}

data class DocumentIngestionProgress(
    val fileName: String,
    val fileSizeBytes: Long,
    val progressFraction: Float?, // 0f..1f or null if indeterminate
    val statusMessage: String
)

class DocumentTooLargeException(message: String) : IllegalArgumentException(message)
class UnsupportedDocumentFormatException(message: String) : IllegalArgumentException(message)
class EmptyDocumentTextException(message: String) : IllegalStateException(message)

object DocumentIngestionEngine {

    const val MAX_UPLOAD_BYTES: Long = 100L * 1024L * 1024L // 100 MB
    private const val MIN_REQUIRED_FREE_BYTES: Long = 32L * 1024L * 1024L // 32 MB safety cushion
    private const val PDF_PAGE_WINDOW = 10
    private const val TEXT_READ_BUFFER_CHARS = 16 * 1024 // 16K chars per streaming read

    @Volatile
    private var pdfBoxInitialized = false

    fun formatBytes(bytes: Long): String {
        if (bytes <= 0L) return "Unknown size"
        val kb = 1024.0
        val mb = kb * 1024.0
        return when {
            bytes >= mb -> String.format(Locale.US, "%.1f MB", bytes / mb)
            bytes >= kb -> String.format(Locale.US, "%.0f KB", bytes / kb)
            else -> "$bytes B"
        }
    }

    fun detectFormat(fileName: String, mimeType: String?): DocumentFormat {
        val ext = fileName.substringAfterLast('.', "").lowercase(Locale.ROOT)
        val cleanMime = mimeType?.lowercase(Locale.ROOT)?.substringBefore(';')?.trim().orEmpty()

        DocumentFormat.entries.firstOrNull { ext in it.extensions }?.let { return it }
        DocumentFormat.entries.firstOrNull { cleanMime in it.mimeTypes }?.let { return it }

        if (cleanMime.startsWith("text/")) return DocumentFormat.TXT

        throw UnsupportedDocumentFormatException(
            "Unsupported file format for '$fileName'. Supported formats: TXT, MD, CSV, JSON, and PDF."
        )
    }

    fun validateSizeAndStorage(fileSizeBytes: Long, availableDiskBytes: Long) {
        if (fileSizeBytes > MAX_UPLOAD_BYTES) {
            throw DocumentTooLargeException(
                "File is ${formatBytes(fileSizeBytes)}, exceeding the 100 MB upload limit."
            )
        }
        val requiredBytes = if (fileSizeBytes > 0L) {
            maxOf(fileSizeBytes * 2L, MIN_REQUIRED_FREE_BYTES)
        } else {
            MIN_REQUIRED_FREE_BYTES
        }
        if (availableDiskBytes < requiredBytes) {
            throw IllegalStateException(
                "Insufficient device storage. Need ~${formatBytes(requiredBytes)} free, but only ${formatBytes(availableDiskBytes)} available."
            )
        }
    }

    private fun ensurePdfBoxInitialized(context: Context) {
        if (!pdfBoxInitialized) {
            synchronized(this) {
                if (!pdfBoxInitialized) {
                    PDFBoxResourceLoader.init(context.applicationContext)
                    pdfBoxInitialized = true
                }
            }
        }
    }

    suspend fun ingestUri(
        context: Context,
        uri: Uri,
        sourceId: String,
        onBatchFlush: suspend (List<DocumentChunk>) -> Unit,
        onProgress: (DocumentIngestionProgress) -> Unit
    ): NotebookSource = withContext(Dispatchers.IO) {
        val contentResolver = context.contentResolver
        var fileName = "Imported Document"
        var reportedSize = -1L

        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIdx = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (cursor.moveToFirst()) {
                if (nameIdx != -1) {
                    fileName = cursor.getString(nameIdx) ?: fileName
                }
                if (sizeIdx != -1 && !cursor.isNull(sizeIdx)) {
                    reportedSize = cursor.getLong(sizeIdx)
                }
            }
        }

        val mimeType = contentResolver.getType(uri)
        val format = detectFormat(fileName, mimeType)

        val statFs = StatFs(context.filesDir.absolutePath)
        validateSizeAndStorage(reportedSize, statFs.availableBytes)

        onProgress(
            DocumentIngestionProgress(
                fileName = fileName,
                fileSizeBytes = reportedSize.coerceAtLeast(0L),
                progressFraction = if (reportedSize > 0) 0.02f else null,
                statusMessage = "Preparing ${format.label} stream (${formatBytes(reportedSize)})..."
            )
        )

        val accumulator = DocumentChunker.StreamingChunkAccumulator(
            sourceId = sourceId,
            sourceTitle = fileName,
            authorOrOrigin = "Document Upload (${format.label})",
            onBatchReady = onBatchFlush
        )

        var totalBytesRead = 0L

        if (format == DocumentFormat.PDF) {
            totalBytesRead = ingestPdfUri(
                context = context,
                uri = uri,
                fileName = fileName,
                reportedSize = reportedSize,
                accumulator = accumulator,
                onProgress = onProgress
            )
        } else {
            totalBytesRead = ingestTextUri(
                context = context,
                uri = uri,
                fileName = fileName,
                format = format,
                reportedSize = reportedSize,
                accumulator = accumulator,
                onProgress = onProgress
            )
        }

        coroutineContext.ensureActive()
        accumulator.finish()

        if (accumulator.totalWords == 0 || accumulator.totalChunks == 0) {
            throw EmptyDocumentTextException(
                if (format == DocumentFormat.PDF) {
                    "No extractable text found in '$fileName'. Scanned image-only PDFs are not supported without OCR."
                } else {
                    "No readable text content found in '$fileName'."
                }
            )
        }

        val finalSizeBytes = if (reportedSize > 0L) reportedSize else totalBytesRead

        NotebookSource(
            id = sourceId,
            title = fileName,
            authorOrOrigin = "${format.label}: $fileName",
            sourceType = "FILE_UPLOAD",
            rawContent = accumulator.getPreviewExcerpt(),
            chunkCount = accumulator.totalChunks,
            wordCount = accumulator.totalWords,
            fileSizeBytes = finalSizeBytes,
            formatLabel = format.label,
            isActive = true
        )
    }

    private suspend fun ingestTextUri(
        context: Context,
        uri: Uri,
        fileName: String,
        format: DocumentFormat,
        reportedSize: Long,
        accumulator: DocumentChunker.StreamingChunkAccumulator,
        onProgress: (DocumentIngestionProgress) -> Unit
    ): Long {
        val rawStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Unable to open '$fileName'.")
        val countingStream = LimitAwareCountingInputStream(rawStream, MAX_UPLOAD_BYTES)

        countingStream.use { stream ->
            BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).use { reader ->
                val buffer = CharArray(TEXT_READ_BUFFER_CHARS)
                var lastProgressBytes = 0L

                while (true) {
                    coroutineContext.ensureActive()
                    val charsRead = reader.read(buffer)
                    if (charsRead == -1) break

                    accumulator.appendText(String(buffer, 0, charsRead))

                    val currentBytes = stream.bytesRead
                    if (currentBytes - lastProgressBytes >= 256 * 1024L) {
                        lastProgressBytes = currentBytes
                        val fraction = if (reportedSize > 0L) {
                            (currentBytes.toFloat() / reportedSize.toFloat()).coerceIn(0.05f, 0.95f)
                        } else null
                        onProgress(
                            DocumentIngestionProgress(
                                fileName = fileName,
                                fileSizeBytes = reportedSize.coerceAtLeast(currentBytes),
                                progressFraction = fraction,
                                statusMessage = "Streaming ${format.label} (${formatBytes(currentBytes)} • ${accumulator.totalChunks} chunks)..."
                            )
                        )
                    }
                }
            }
            return stream.bytesRead
        }
    }

    private suspend fun ingestPdfUri(
        context: Context,
        uri: Uri,
        fileName: String,
        reportedSize: Long,
        accumulator: DocumentChunker.StreamingChunkAccumulator,
        onProgress: (DocumentIngestionProgress) -> Unit
    ): Long {
        ensurePdfBoxInitialized(context)

        val tempPdfFile = File.createTempFile("greenai_ingest_", ".pdf", context.cacheDir)
        var bytesCopied = 0L

        try {
            val rawStream = context.contentResolver.openInputStream(uri)
                ?: throw IllegalStateException("Unable to open PDF '$fileName'.")
            val countingStream = LimitAwareCountingInputStream(rawStream, MAX_UPLOAD_BYTES)

            countingStream.use { input ->
                FileOutputStream(tempPdfFile).use { output ->
                    val copyBuf = ByteArray(64 * 1024)
                    var lastNotified = 0L
                    while (true) {
                        coroutineContext.ensureActive()
                        val read = input.read(copyBuf)
                        if (read == -1) break
                        output.write(copyBuf, 0, read)
                        bytesCopied = input.bytesRead

                        if (bytesCopied - lastNotified >= 1024 * 1024L) {
                            lastNotified = bytesCopied
                            val copyFraction = if (reportedSize > 0L) {
                                (bytesCopied.toFloat() / reportedSize.toFloat() * 0.25f).coerceIn(0.02f, 0.25f)
                            } else null
                            onProgress(
                                DocumentIngestionProgress(
                                    fileName = fileName,
                                    fileSizeBytes = reportedSize.coerceAtLeast(bytesCopied),
                                    progressFraction = copyFraction,
                                    statusMessage = "Buffering PDF (${formatBytes(bytesCopied)})..."
                                )
                            )
                        }
                    }
                }
            }

            coroutineContext.ensureActive()
            onProgress(
                DocumentIngestionProgress(
                    fileName = fileName,
                    fileSizeBytes = reportedSize.coerceAtLeast(bytesCopied),
                    progressFraction = 0.28f,
                    statusMessage = "Opening PDF structure (${formatBytes(bytesCopied)})..."
                )
            )

            val memSetting = MemoryUsageSetting.setupTempFileOnly().setTempDir(context.cacheDir)
            PDDocument.load(tempPdfFile, memSetting).use { pdDocument ->
                if (pdDocument.isEncrypted) {
                    throw IllegalStateException("'$fileName' is password-protected or encrypted and cannot be indexed.")
                }

                val totalPages = pdDocument.numberOfPages
                if (totalPages <= 0) {
                    throw EmptyDocumentTextException("PDF '$fileName' contains no pages.")
                }

                val stripper = PDFTextStripper().apply {
                    sortByPosition = true
                }

                var startPage = 1
                while (startPage <= totalPages) {
                    coroutineContext.ensureActive()
                    val endPage = minOf(startPage + PDF_PAGE_WINDOW - 1, totalPages)
                    stripper.startPage = startPage
                    stripper.endPage = endPage

                    val pageWindowText = stripper.getText(pdDocument)
                    if (!pageWindowText.isNullOrBlank()) {
                        accumulator.appendText(pageWindowText)
                    }

                    val progress = 0.28f + (endPage.toFloat() / totalPages.toFloat()) * 0.68f
                    onProgress(
                        DocumentIngestionProgress(
                            fileName = fileName,
                            fileSizeBytes = reportedSize.coerceAtLeast(bytesCopied),
                            progressFraction = progress.coerceIn(0.28f, 0.96f),
                            statusMessage = "Extracting PDF pages $startPage–$endPage of $totalPages (${accumulator.totalChunks} chunks)..."
                        )
                    )

                    startPage = endPage + 1
                }
            }

            return bytesCopied
        } catch (ce: CancellationException) {
            throw ce
        } finally {
            runCatching {
                if (tempPdfFile.exists()) {
                    tempPdfFile.delete()
                }
            }
        }
    }

    private class LimitAwareCountingInputStream(
        input: InputStream,
        private val maxBytes: Long
    ) : FilterInputStream(input) {
        var bytesRead: Long = 0L
            private set

        override fun read(): Int {
            val b = super.read()
            if (b != -1) {
                incrementAndCheck(1)
            }
            return b
        }

        override fun read(b: ByteArray, off: Int, len: Int): Int {
            val count = super.read(b, off, len)
            if (count > 0) {
                incrementAndCheck(count.toLong())
            }
            return count
        }

        private fun incrementAndCheck(delta: Long) {
            bytesRead += delta
            if (bytesRead > maxBytes) {
                throw DocumentTooLargeException(
                    "Document stream exceeded the 100 MB maximum file size limit."
                )
            }
        }
    }
}
