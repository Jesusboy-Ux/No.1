package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EvidenceLevel(val label: String, val description: String) {
    TRADITIONAL_USE_REPORTED(
        "Traditional Use Reported",
        "Documented community knowledge, ethnobotanical field surveys, and historical medicinal preparations."
    ),
    LABORATORY_EVIDENCE(
        "Laboratory Evidence",
        "In vitro cellular assays, microbial inhibition, and phytochemical isolation studies."
    ),
    ANIMAL_RESEARCH(
        "Animal Research",
        "In vivo pharmacological models demonstrating physiological activity or toxicological parameters."
    ),
    HUMAN_RESEARCH_AVAILABLE(
        "Human Research Available",
        "Randomized controlled trials, human crossover studies, or systematic clinical reviews."
    ),
    INSUFFICIENT_OR_CONFLICTING(
        "Insufficient or Conflicting Evidence",
        "Available literature is inconclusive, conflicting, or lacks rigorous peer-reviewed documentation."
    );

    companion object {
        fun fromString(value: String): EvidenceLevel {
            return entries.firstOrNull {
                it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true)
            } ?: INSUFFICIENT_OR_CONFLICTING
        }
    }
}

data class SourceReference(
    val id: String,
    val title: String,
    val authors: String,
    val year: Int,
    val journal: String,
    val doiOrUrl: String,
    val evidenceType: String
)

@Entity(tableName = "document_chunks")
data class DocumentChunk(
    @PrimaryKey val id: String,
    val plantId: String,
    val plantScientificName: String,
    val sourceId: String,
    val sourceTitle: String,
    val authors: String,
    val year: Int,
    val citation: String,
    val section: String,
    val text: String,
    val evidenceTier: EvidenceLevel,
    val keywords: String
)

@Entity(tableName = "medicinal_plants")
data class MedicinalPlant(
    @PrimaryKey val id: String,
    val scientificName: String,
    val commonNamesList: String,
    val family: String,
    val localNamesJson: String,
    val plantPartsList: String,
    val geographicalContext: String,
    val traditionalUsesList: String,
    val preparationMethod: String,
    val phytochemicalsList: String,
    val laboratoryEvidence: String,
    val clinicalEvidence: String,
    val safetyInformation: String,
    val limitations: String,
    val referencesJson: String,
    val reviewStatus: EvidenceLevel,
    val isUserCreated: Boolean = false,
    val imageDrawableName: String = "botanical_hero"
) {
    val commonNames: List<String>
        get() = commonNamesList.split(",").map { it.trim() }.filter { it.isNotEmpty() }

    val plantParts: List<String>
        get() = plantPartsList.split(",").map { it.trim() }.filter { it.isNotEmpty() }

    val traditionalUses: List<String>
        get() = traditionalUsesList.split("|").map { it.trim() }.filter { it.isNotEmpty() }

    val phytochemicals: List<String>
        get() = phytochemicalsList.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}

@Entity(tableName = "saved_bookmarks")
data class SavedBookmark(
    @PrimaryKey val id: String,
    val targetId: String,
    val itemType: String, // "PLANT" or "RESEARCH_ANSWER"
    val title: String,
    val subtitle: String,
    val snippet: String,
    val timestamp: Long = System.currentTimeMillis(),
    val evidenceLevel: EvidenceLevel? = null
)

enum class ChatSender {
    USER,
    ASSISTANT
}

enum class ResearchMode(val title: String, val subtitle: String) {
    LOCAL_NOTEBOOK_RAG(
        "Local Notebook RAG",
        "Grounded strictly in your uploaded materials & curated botanical records"
    ),
    DIRECT_INTERNET_AI(
        "Direct Internet AI",
        "Live web knowledge & open exploration without local corpus constraints"
    );

    val displayName: String get() = if (this == DIRECT_INTERNET_AI) "Direct Internet & AI" else title
}

@Entity(tableName = "notebook_sources")
data class NotebookSource(
    @PrimaryKey val id: String,
    val title: String,
    val authorOrOrigin: String = "Personal Research",
    val sourceType: String = "PASTED_NOTE", // "FILE_UPLOAD" or "PASTED_NOTE"
    val rawContent: String,
    val chunkCount: Int,
    val wordCount: Int,
    val isActive: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

data class ChatMessage(
    val id: String,
    val sender: ChatSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val retrievedChunks: List<DocumentChunk> = emptyList(),
    val evidenceStatus: EvidenceLevel? = null,
    val safetyWarning: String? = null,
    val isEvidenceGrounded: Boolean = true,
    val externalNotice: String? = null,
    val isBookmarked: Boolean = false,
    val researchMode: ResearchMode = ResearchMode.LOCAL_NOTEBOOK_RAG
)

enum class PlantOrgan(val label: String, val apiValue: String) {
    LEAF("Leaf", "leaf"),
    FLOWER("Flower", "flower"),
    FRUIT("Fruit", "fruit"),
    BARK("Bark", "bark"),
    HABIT("Whole Plant / Habit", "habit")
}

enum class IdentificationLabel(val title: String, val description: String) {
    IDENTIFICATION_SUGGESTED(
        "Identification suggested",
        "An online botanical identification service has proposed a plausible species identity."
    ),
    CROSS_REFERENCE_FOUND(
        "Cross-reference found",
        "The scientific name matches an external authoritative botanical database record."
    ),
    UNCERTAIN_IDENTIFICATION(
        "Uncertain identification",
        "More information or expert botanical assessment is needed."
    )
}

data class TaxonomicCrossReference(
    val matchedScientificName: String,
    val canonicalName: String,
    val taxonomicStatus: String, // e.g. "ACCEPTED", "SYNONYM", "DOUBTFUL"
    val rank: String, // e.g. "SPECIES"
    val family: String,
    val genus: String,
    val sourceName: String = "GBIF Taxonomic Backbone & Kew POWO",
    val powoUrl: String,
    val worldFloraOnlineUrl: String,
    val gbifUrl: String,
    val matchType: String = "EXACT",
    val confidence: Int = 100
)

data class AlternativeSpeciesCandidate(
    val scientificName: String,
    val commonNames: List<String> = emptyList(),
    val family: String = "",
    val scorePercentage: Double? = null,
    val scoreMeaning: String? = null,
    val reasoning: String = ""
)

data class PlantScanResult(
    val id: String,
    val primaryScientificName: String,
    val authorship: String? = null,
    val family: String,
    val genus: String,
    val commonNames: List<String> = emptyList(),
    val observedOrgans: List<PlantOrgan> = emptyList(),
    val identificationService: String, // "Pl@ntNet Botanical API" or "Gemini 3.5 Flash Botanical Vision"
    val confidenceScore: Double? = null, // only if provided by service
    val confidenceDocumentation: String? = null,
    val primaryLabel: IdentificationLabel,
    val morphologicalObservations: String,
    val taxonomicReference: TaxonomicCrossReference? = null,
    val alternatives: List<AlternativeSpeciesCandidate> = emptyList(),
    val safetyDisclaimer: String = "BOTANICAL IDENTIFICATION CAUTION: Visual plant identification from photographs may be inaccurate. Visual likeness does NOT establish chemical composition or medicinal safety. Do NOT ingest, prepare, or use any plant medicinally based on an AI suggestion. Consult a qualified botanist, pharmacognosist, or medical professional.",
    val curatedMonographMatch: MedicinalPlant? = null,
    val timestamp: Long = System.currentTimeMillis()
)

