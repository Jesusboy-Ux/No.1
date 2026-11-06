package com.example.domain

import com.example.data.model.DocumentChunk
import com.example.data.model.EvidenceLevel
import java.util.Locale

class RetrievalEngine {

    private val stopWords = setOf(
        "a", "an", "the", "is", "are", "was", "were", "in", "on", "at", "to", "for",
        "of", "with", "by", "about", "what", "which", "how", "can", "could", "should",
        "do", "does", "did", "and", "or", "it", "this", "that", "there", "be", "been",
        "tell", "me", "any", "some", "all", "have", "has", "had", "use", "used"
    )

    fun retrievePassages(
        query: String,
        allChunks: List<DocumentChunk>,
        maxResults: Int = 4
    ): List<DocumentChunk> {
        val cleanQuery = query.lowercase(Locale.ROOT)
        val queryTokens = cleanQuery
            .split(Regex("[^a-zA-Z0-9]+"))
            .filter { it.length > 2 && !stopWords.contains(it) }

        if (queryTokens.isEmpty()) return emptyList()

        val scoredChunks = allChunks.map { chunk ->
            val score = calculateRelevanceScore(queryTokens, cleanQuery, chunk)
            Pair(chunk, score)
        }

        // Only keep chunks that exceed relevance threshold
        val filtered = scoredChunks
            .filter { it.second >= 4.0 }
            .sortedByDescending { it.second }
            .take(maxResults)
            .map { it.first }

        return filtered
    }

    private fun calculateRelevanceScore(
        queryTokens: List<String>,
        fullQuery: String,
        chunk: DocumentChunk
    ): Double {
        var score = 0.0

        val chunkTextLower = chunk.text.lowercase(Locale.ROOT)
        val keywordsLower = chunk.keywords.lowercase(Locale.ROOT)
        val plantNameLower = chunk.plantScientificName.lowercase(Locale.ROOT)
        val sectionLower = chunk.section.lowercase(Locale.ROOT)
        val titleLower = chunk.sourceTitle.lowercase(Locale.ROOT)

        // 1. Plant name matching (high weight)
        val plantTokens = plantNameLower.split(" ").filter { it.length > 3 }
        for (pt in plantTokens) {
            if (fullQuery.contains(pt)) {
                score += 10.0
            }
        }

        // Specific common plant name check
        if (fullQuery.contains("wormwood") || fullQuery.contains("umhlonyane") || fullQuery.contains("lengana")) {
            if (chunk.plantId == "PLANT-001") score += 12.0
        }
        if (fullQuery.contains("moringa") || fullQuery.contains("zogale") || fullQuery.contains("drumstick")) {
            if (chunk.plantId == "PLANT-002") score += 12.0
        }
        if (fullQuery.contains("bitter leaf") || fullQuery.contains("ewuro") || fullQuery.contains("onugbu") || fullQuery.contains("shuwaka") || fullQuery.contains("vernonia")) {
            if (chunk.plantId == "PLANT-003") score += 12.0
        }
        if (fullQuery.contains("devil's claw") || fullQuery.contains("devils claw") || fullQuery.contains("sengaparile") || fullQuery.contains("duiwelsklou") || fullQuery.contains("harpagophytum")) {
            if (chunk.plantId == "PLANT-004") score += 12.0
        }
        if (fullQuery.contains("periwinkle") || fullQuery.contains("catharanthus") || fullQuery.contains("vinca") || fullQuery.contains("vinblastine") || fullQuery.contains("vincristine")) {
            if (chunk.plantId == "PLANT-005") score += 12.0
        }

        // 2. Intent & Section boosting
        val isSafetyQuery = fullQuery.contains("safe") || fullQuery.contains("danger") ||
                fullQuery.contains("toxic") || fullQuery.contains("pregnant") || fullQuery.contains("pregnancy") ||
                fullQuery.contains("child") || fullQuery.contains("adverse") || fullQuery.contains("contraindicat") ||
                fullQuery.contains("poison") || fullQuery.contains("abortion")

        if (isSafetyQuery && (sectionLower.contains("safe") || sectionLower.contains("toxici") || chunkTextLower.contains("contraindicated") || chunkTextLower.contains("warning"))) {
            score += 15.0
        }

        val isPhytochemQuery = fullQuery.contains("phytochem") || fullQuery.contains("compound") ||
                fullQuery.contains("chemical") || fullQuery.contains("alkaloid") || fullQuery.contains("flavonoid") ||
                fullQuery.contains("active ingredient") || fullQuery.contains("isolate")

        if (isPhytochemQuery && sectionLower.contains("phytochem")) {
            score += 10.0
        }

        val isTraditionalQuery = fullQuery.contains("tradition") || fullQuery.contains("folk") ||
                fullQuery.contains("prepare") || fullQuery.contains("steam") || fullQuery.contains("boil") ||
                fullQuery.contains("tea") || fullQuery.contains("soup") || fullQuery.contains("indigenous")

        if (isTraditionalQuery && sectionLower.contains("tradition")) {
            score += 8.0
        }

        val isClinicalQuery = fullQuery.contains("clinical") || fullQuery.contains("trial") ||
                fullQuery.contains("human") || fullQuery.contains("cochrane") || fullQuery.contains("evidence") ||
                fullQuery.contains("proven") || fullQuery.contains("osteoarthritis") || fullQuery.contains("malaria")

        if (isClinicalQuery && (sectionLower.contains("clinical") || sectionLower.contains("pharmacology"))) {
            score += 8.0
        }

        // 3. Keyword and token frequency
        for (token in queryTokens) {
            if (keywordsLower.contains(token)) score += 3.0
            if (titleLower.contains(token)) score += 2.0
            if (chunkTextLower.contains(token)) score += 1.5
        }

        return score
    }

    fun determineEvidenceStatus(retrievedChunks: List<DocumentChunk>): EvidenceLevel {
        if (retrievedChunks.isEmpty()) return EvidenceLevel.INSUFFICIENT_OR_CONFLICTING

        val hasHuman = retrievedChunks.any { it.evidenceTier == EvidenceLevel.HUMAN_RESEARCH_AVAILABLE }
        val hasAnimal = retrievedChunks.any { it.evidenceTier == EvidenceLevel.ANIMAL_RESEARCH }
        val hasLab = retrievedChunks.any { it.evidenceTier == EvidenceLevel.LABORATORY_EVIDENCE }
        val hasTraditional = retrievedChunks.any { it.evidenceTier == EvidenceLevel.TRADITIONAL_USE_REPORTED }

        return when {
            hasHuman -> EvidenceLevel.HUMAN_RESEARCH_AVAILABLE
            hasAnimal -> EvidenceLevel.ANIMAL_RESEARCH
            hasLab -> EvidenceLevel.LABORATORY_EVIDENCE
            hasTraditional -> EvidenceLevel.TRADITIONAL_USE_REPORTED
            else -> EvidenceLevel.INSUFFICIENT_OR_CONFLICTING
        }
    }

    fun extractSafetyWarning(retrievedChunks: List<DocumentChunk>): String? {
        val safetyChunk = retrievedChunks.firstOrNull {
            it.section.contains("Safety", ignoreCase = true) ||
                    it.section.contains("Toxicity", ignoreCase = true) ||
                    it.text.contains("WARNING", ignoreCase = true) ||
                    it.text.contains("CONTRAINDICATED", ignoreCase = true)
        } ?: return null

        val text = safetyChunk.text
        return when {
            text.contains("CONTRAINDICATED IN PREGNANCY", ignoreCase = true) ->
                "CRITICAL WARNING: Strictly contraindicated during pregnancy. Documented oxytocic or abortifacient actions."
            text.contains("MUST NEVER BE CONSUMED", ignoreCase = true) ->
                "LETHAL TOXICITY WARNING: Severe cytotoxin. Ingestion of raw plant material causes bone marrow aplasia and neurotoxicity."
            text.contains("thujone", ignoreCase = true) ->
                "CAUTION: Contains neurotoxic thujone; contraindicated in pregnancy and seizure disorders."
            text.contains("stomach ulcer", ignoreCase = true) || text.contains("peptic ulcer", ignoreCase = true) ->
                "CONTRAINDICATION: Bitter iridoids stimulate gastric acid; avoid in active peptic ulcer disease."
            else -> "SAFETY ALERT: Review detailed contraindications and consult qualified healthcare professionals."
        }
    }
}
