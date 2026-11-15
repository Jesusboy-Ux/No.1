package com.example.domain

import com.example.data.model.DocumentChunk
import com.example.data.model.EvidenceLevel
import java.util.Calendar
import java.util.Locale

object DocumentChunker {

    const val DEFAULT_WORDS_PER_CHUNK = 180
    const val DEFAULT_BATCH_FLUSH_SIZE = 250
    const val MAX_PREVIEW_CHARS = 1500

    private val stopWords = setOf(
        "a", "an", "the", "is", "are", "was", "were", "in", "on", "at", "to", "for",
        "of", "with", "by", "about", "what", "which", "how", "can", "could", "should",
        "do", "does", "did", "and", "or", "it", "this", "that", "there", "be", "been",
        "tell", "me", "any", "some", "all", "have", "has", "had", "use", "used", "from"
    )

    fun chunkText(
        sourceId: String,
        sourceTitle: String,
        authorOrOrigin: String,
        rawText: String,
        targetWordsPerChunk: Int = DEFAULT_WORDS_PER_CHUNK
    ): List<DocumentChunk> {
        val cleanText = rawText.trim()
        if (cleanText.isBlank()) return emptyList()

        val paragraphs = cleanText.split(Regex("(\r?\n){2,}"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val chunkTexts = mutableListOf<String>()
        val currentChunkWords = mutableListOf<String>()

        for (paragraph in paragraphs) {
            val wordsInPara = paragraph.split(Regex("\\s+")).filter { it.isNotEmpty() }
            if (currentChunkWords.size + wordsInPara.size <= targetWordsPerChunk * 1.4) {
                currentChunkWords.addAll(wordsInPara)
            } else {
                if (currentChunkWords.isNotEmpty()) {
                    chunkTexts.add(currentChunkWords.joinToString(" "))
                    currentChunkWords.clear()
                }
                if (wordsInPara.size > targetWordsPerChunk * 1.5) {
                    // Split very long paragraphs into windowed chunks
                    var start = 0
                    while (start < wordsInPara.size) {
                        val end = (start + targetWordsPerChunk).coerceAtMost(wordsInPara.size)
                        chunkTexts.add(wordsInPara.subList(start, end).joinToString(" "))
                        start += (targetWordsPerChunk * 0.85).toInt()
                    }
                } else {
                    currentChunkWords.addAll(wordsInPara)
                }
            }
        }

        if (currentChunkWords.isNotEmpty()) {
            chunkTexts.add(currentChunkWords.joinToString(" "))
        }

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        return chunkTexts.mapIndexed { index, text ->
            buildChunk(
                sourceId = sourceId,
                sourceTitle = sourceTitle,
                authorOrOrigin = authorOrOrigin,
                chunkIndex = index + 1,
                text = text,
                year = currentYear
            )
        }
    }

    /**
     * Streaming incremental chunker designed for large documents (up to 100 MB).
     * Never retains the full document or full chunk list in memory: flushes completed
     * batches directly to the caller (e.g., Room SQLite) every [batchFlushSize] chunks.
     */
    class StreamingChunkAccumulator(
        private val sourceId: String,
        private val sourceTitle: String,
        private val authorOrOrigin: String,
        private val targetWordsPerChunk: Int = DEFAULT_WORDS_PER_CHUNK,
        private val batchFlushSize: Int = DEFAULT_BATCH_FLUSH_SIZE,
        private val onBatchReady: suspend (List<DocumentChunk>) -> Unit
    ) {
        private val currentWords = ArrayList<String>(targetWordsPerChunk + 32)
        private val pendingBatch = ArrayList<DocumentChunk>(batchFlushSize)
        private val previewBuilder = StringBuilder(MAX_PREVIEW_CHARS)
        private val wordTokenBuilder = StringBuilder(64)
        private val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        private val overlapWordsCount = (targetWordsPerChunk * 0.15).toInt().coerceAtLeast(15)

        var totalWords: Int = 0
            private set

        var totalChunks: Int = 0
            private set

        fun getPreviewExcerpt(): String = previewBuilder.toString().trim()

        suspend fun appendText(text: String) {
            if (text.isEmpty()) return

            // Capture compact preview snippet (< 1.5 KB) for SQLite metadata row
            if (previewBuilder.length < MAX_PREVIEW_CHARS) {
                val remaining = MAX_PREVIEW_CHARS - previewBuilder.length
                val slice = if (text.length <= remaining) text else text.substring(0, remaining)
                previewBuilder.append(slice)
                if (previewBuilder.length < MAX_PREVIEW_CHARS && !text.endsWith('\n')) {
                    previewBuilder.append('\n')
                }
            }

            // Stream characters directly into word tokens so huge single lines (e.g. JSON/CSV) never OOM Regex
            for (i in text.indices) {
                val ch = text[i]
                if (ch.isWhitespace()) {
                    if (wordTokenBuilder.isNotEmpty()) {
                        pushWord(wordTokenBuilder.toString())
                        wordTokenBuilder.setLength(0)
                    }
                } else {
                    if (wordTokenBuilder.length < 120) {
                        wordTokenBuilder.append(ch)
                    }
                }
            }
        }

        suspend fun finish() {
            if (wordTokenBuilder.isNotEmpty()) {
                pushWord(wordTokenBuilder.toString())
                wordTokenBuilder.setLength(0)
            }

            if (currentWords.isNotEmpty()) {
                emitCurrentWindow(keepOverlap = false)
            }

            if (pendingBatch.isNotEmpty()) {
                val toFlush = pendingBatch.toList()
                pendingBatch.clear()
                onBatchReady(toFlush)
            }
        }

        private suspend fun pushWord(word: String) {
            totalWords++
            currentWords.add(word)
            if (currentWords.size >= targetWordsPerChunk) {
                emitCurrentWindow(keepOverlap = true)
            }
        }

        private suspend fun emitCurrentWindow(keepOverlap: Boolean) {
            val chunkText = currentWords.joinToString(" ")
            totalChunks++
            pendingBatch.add(
                buildChunk(
                    sourceId = sourceId,
                    sourceTitle = sourceTitle,
                    authorOrOrigin = authorOrOrigin,
                    chunkIndex = totalChunks,
                    text = chunkText,
                    year = currentYear
                )
            )

            if (keepOverlap && currentWords.size > overlapWordsCount) {
                val tail = currentWords.subList(currentWords.size - overlapWordsCount, currentWords.size).toList()
                currentWords.clear()
                currentWords.addAll(tail)
            } else {
                currentWords.clear()
            }

            if (pendingBatch.size >= batchFlushSize) {
                val toFlush = pendingBatch.toList()
                pendingBatch.clear()
                onBatchReady(toFlush)
            }
        }
    }

    internal fun buildChunk(
        sourceId: String,
        sourceTitle: String,
        authorOrOrigin: String,
        chunkIndex: Int,
        text: String,
        year: Int
    ): DocumentChunk {
        val detectedTier = detectEvidenceTier(text)
        val keywords = extractKeywords(text, sourceTitle)

        return DocumentChunk(
            id = "${sourceId}_CHK_$chunkIndex",
            plantId = sourceId,
            plantScientificName = sourceTitle,
            sourceId = sourceId,
            sourceTitle = sourceTitle,
            authors = authorOrOrigin.ifBlank { "Personal Research Note" },
            year = year,
            citation = "${authorOrOrigin.ifBlank { "Notebook Document" }} ($sourceTitle)",
            section = "Section $chunkIndex",
            text = text,
            evidenceTier = detectedTier,
            keywords = keywords
        )
    }

    private fun detectEvidenceTier(text: String): EvidenceLevel {
        val lower = text.lowercase(Locale.ROOT)
        return when {
            lower.contains("clinical trial") || lower.contains("randomized") || lower.contains("human subjects") || lower.contains("cochrane") ->
                EvidenceLevel.HUMAN_RESEARCH_AVAILABLE
            lower.contains("murine") || lower.contains("in vivo") || lower.contains("rats") || lower.contains("mice") || lower.contains("rodent") ->
                EvidenceLevel.ANIMAL_RESEARCH
            lower.contains("in vitro") || lower.contains("phytochemical") || lower.contains("assay") || lower.contains("inhibition") || lower.contains("ic50") ->
                EvidenceLevel.LABORATORY_EVIDENCE
            else -> EvidenceLevel.TRADITIONAL_USE_REPORTED
        }
    }

    private fun extractKeywords(text: String, title: String): String {
        val tokens = (title + " " + text).lowercase(Locale.ROOT)
            .split(Regex("[^a-zA-Z0-9]+"))
            .filter { it.length > 3 && !stopWords.contains(it) }

        val frequencyMap = mutableMapOf<String, Int>()
        for (token in tokens) {
            frequencyMap[token] = (frequencyMap[token] ?: 0) + 1
        }

        return frequencyMap.entries
            .sortedByDescending { it.value }
            .take(8)
            .joinToString(", ") { it.key }
    }
}
