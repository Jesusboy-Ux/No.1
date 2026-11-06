package com.example.domain

import com.example.data.model.DocumentChunk
import com.example.data.model.EvidenceLevel
import java.util.Calendar
import java.util.Locale

object DocumentChunker {

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
        targetWordsPerChunk: Int = 180
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
            val detectedTier = detectEvidenceTier(text)
            val keywords = extractKeywords(text, sourceTitle)

            DocumentChunk(
                id = "${sourceId}_CHK_${index + 1}",
                plantId = sourceId,
                plantScientificName = sourceTitle,
                sourceId = sourceId,
                sourceTitle = sourceTitle,
                authors = authorOrOrigin.ifBlank { "Personal Research Note" },
                year = currentYear,
                citation = "${authorOrOrigin.ifBlank { "Notebook Document" }} ($sourceTitle)",
                section = "Section ${index + 1}",
                text = text,
                evidenceTier = detectedTier,
                keywords = keywords
            )
        }
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
