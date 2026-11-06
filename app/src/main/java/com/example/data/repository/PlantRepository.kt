package com.example.data.repository

import com.example.data.CuratedKnowledgeBase
import com.example.data.api.GeminiService
import com.example.data.api.GroundedAiResult
import com.example.data.local.AppDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSender
import com.example.data.model.DocumentChunk
import com.example.data.model.EvidenceLevel
import com.example.data.model.MedicinalPlant
import com.example.data.model.NotebookSource
import com.example.data.model.ResearchMode
import com.example.data.model.SavedBookmark
import com.example.domain.RetrievalEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class PlantRepository(
    private val database: AppDatabase,
    private val geminiService: GeminiService = GeminiService(),
    private val retrievalEngine: RetrievalEngine = RetrievalEngine()
) {

    fun getAllPlants(): Flow<List<MedicinalPlant>> {
        return database.plantDao().getAllPlants().map { dbPlants ->
            if (dbPlants.isEmpty()) {
                CuratedKnowledgeBase.curatedPlants
            } else {
                val customOnly = dbPlants.filter { it.isUserCreated }
                CuratedKnowledgeBase.curatedPlants + customOnly
            }
        }
    }

    suspend fun getPlantById(id: String): MedicinalPlant? {
        val dbPlant = database.plantDao().getPlantById(id)
        if (dbPlant != null) return dbPlant
        return CuratedKnowledgeBase.curatedPlants.firstOrNull { it.id == id }
    }

    suspend fun getAllChunks(): List<DocumentChunk> {
        val curated = CuratedKnowledgeBase.curatedDocumentChunks
        return try {
            val dbChunks = database.chunkDao().searchChunks("")
            if (dbChunks.isNotEmpty()) {
                val customOnly = dbChunks.filter { dbChunk -> curated.none { it.id == dbChunk.id } }
                curated + customOnly
            } else {
                curated
            }
        } catch (e: Exception) {
            curated
        }
    }

    suspend fun insertCustomPlant(
        plant: MedicinalPlant,
        chunks: List<DocumentChunk>
    ) {
        database.plantDao().insertPlant(plant)
        if (chunks.isNotEmpty()) {
            database.chunkDao().insertChunks(chunks)
        }
    }

    // Notebook Sources (NotebookLM style materials)
    fun getAllNotebookSources(): Flow<List<NotebookSource>> {
        return database.notebookSourceDao().getAllSources()
    }

    suspend fun insertNotebookSource(
        source: NotebookSource,
        chunks: List<DocumentChunk>
    ) {
        database.notebookSourceDao().insertSource(source)
        if (chunks.isNotEmpty()) {
            database.chunkDao().insertChunks(chunks)
        }
    }

    suspend fun setNotebookSourceActive(id: String, isActive: Boolean) {
        database.notebookSourceDao().setSourceActive(id, isActive)
    }

    suspend fun setAllNotebookSourcesActive(isActive: Boolean) {
        database.notebookSourceDao().setAllSourcesActive(isActive)
    }

    suspend fun deleteNotebookSource(id: String) {
        database.notebookSourceDao().deleteSourceById(id)
        database.chunkDao().deleteChunksBySourceId(id)
    }

    fun getAllBookmarks(): Flow<List<SavedBookmark>> {
        return database.bookmarkDao().getAllBookmarks()
    }

    suspend fun toggleBookmark(bookmark: SavedBookmark): Boolean {
        val count = database.bookmarkDao().isBookmarked(bookmark.targetId)
        return if (count > 0) {
            database.bookmarkDao().deleteBookmarkByTargetId(bookmark.targetId)
            false
        } else {
            database.bookmarkDao().insertBookmark(bookmark)
            true
        }
    }

    suspend fun isBookmarked(targetId: String): Boolean {
        return database.bookmarkDao().isBookmarked(targetId) > 0
    }

    suspend fun deleteBookmark(id: String) {
        database.bookmarkDao().deleteBookmarkById(id)
    }

    suspend fun answerResearchQuestion(
        question: String,
        mode: ResearchMode = ResearchMode.LOCAL_NOTEBOOK_RAG,
        activeSourceIds: Set<String>? = null
    ): ChatMessage {
        if (mode == ResearchMode.DIRECT_INTERNET_AI) {
            val liveResult = geminiService.generateDirectInternetAnswer(question)
            return ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = ChatSender.ASSISTANT,
                text = liveResult.answerText,
                timestamp = System.currentTimeMillis(),
                retrievedChunks = emptyList(),
                evidenceStatus = null,
                safetyWarning = null,
                isEvidenceGrounded = false,
                externalNotice = liveResult.externalNotice ?: "Direct Internet AI Query",
                researchMode = ResearchMode.DIRECT_INTERNET_AI
            )
        }

        // Mode: LOCAL_NOTEBOOK_RAG
        val allChunks = getAllChunks()
        val candidateChunks = if (activeSourceIds != null && activeSourceIds.isNotEmpty()) {
            allChunks.filter { chunk ->
                // Check if chunk belongs to an active source or if curated materials are considered active
                activeSourceIds.contains(chunk.sourceId) ||
                        (activeSourceIds.contains("CURATED_CORPUS") && chunk.plantId.startsWith("PLANT-"))
            }
        } else {
            allChunks
        }

        val retrieved = retrievalEngine.retrievePassages(question, candidateChunks)
        val evidenceStatus = retrievalEngine.determineEvidenceStatus(retrieved)
        val safetyWarning = retrievalEngine.extractSafetyWarning(retrieved)

        val aiResult: GroundedAiResult = geminiService.generateGroundedAnswer(question, retrieved)

        return ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = ChatSender.ASSISTANT,
            text = aiResult.answerText,
            timestamp = System.currentTimeMillis(),
            retrievedChunks = retrieved,
            evidenceStatus = evidenceStatus,
            safetyWarning = safetyWarning,
            isEvidenceGrounded = true,
            externalNotice = aiResult.externalNotice,
            researchMode = ResearchMode.LOCAL_NOTEBOOK_RAG
        )
    }

    suspend fun initializeDatabase() {
        database.plantDao().insertPlants(CuratedKnowledgeBase.curatedPlants)
        database.chunkDao().insertChunks(CuratedKnowledgeBase.curatedDocumentChunks)
    }
}
