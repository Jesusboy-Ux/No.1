package com.example.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.data.CuratedKnowledgeBase
import com.example.data.api.GeminiService
import com.example.data.api.GroundedAiResult
import com.example.data.local.AppDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSender
import com.example.data.model.DocumentChunk
import com.example.data.model.MedicinalPlant
import com.example.data.model.NotebookSource
import com.example.data.model.ResearchMode
import com.example.data.model.SavedBookmark
import com.example.domain.RetrievalEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Locale
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

    suspend fun getAllChunks(): List<DocumentChunk> = withContext(Dispatchers.IO) {
        val curated = CuratedKnowledgeBase.curatedDocumentChunks
        return@withContext try {
            val dbChunks = database.chunkDao().searchChunks("")
            if (dbChunks.isNotEmpty()) {
                val customOnly = dbChunks.filter { dbChunk -> curated.none { it.id == dbChunk.id } }
                curated + customOnly
            } else {
                curated
            }
        } catch (_: Exception) {
            curated
        }
    }

    /**
     * SQL-level candidate pre-filtering to prevent OutOfMemoryError when indexing
     * large research documents (up to 100 MB / 80,000+ chunks). Caps SQLite reads
     * to <= 200 candidate rows instead of loading the entire document_chunks table.
     */
    suspend fun getCandidateChunksForQuery(
        question: String,
        activeSourceIds: Set<String>?
    ): List<DocumentChunk> = withContext(Dispatchers.IO) {
        val curated = CuratedKnowledgeBase.curatedDocumentChunks
        val includeCurated = activeSourceIds == null || activeSourceIds.contains("CURATED_CORPUS")
        val curatedSet = if (includeCurated) curated else emptyList()

        val tokens = question.lowercase(Locale.ROOT)
            .split(Regex("[^a-zA-Z0-9]+"))
            .filter { it.length > 2 }
            .distinct()
            .take(8)

        val userSourceIds = activeSourceIds?.filter { it != "CURATED_CORPUS" } ?: emptyList()

        // If user explicitly filtered ONLY CURATED_CORPUS and no uploaded sources
        if (activeSourceIds != null && userSourceIds.isEmpty() && includeCurated) {
            return@withContext curatedSet
        }
        if (activeSourceIds != null && activeSourceIds.isEmpty()) {
            return@withContext emptyList()
        }

        val dbCandidates = try {
            val args = mutableListOf<Any>()
            val whereClauses = mutableListOf<String>()

            if (userSourceIds.isNotEmpty()) {
                val placeholders = userSourceIds.joinToString(",") { "?" }
                args.addAll(userSourceIds)
                if (includeCurated) {
                    whereClauses.add("(sourceId IN ($placeholders) OR plantId LIKE 'PLANT-%')")
                } else {
                    whereClauses.add("sourceId IN ($placeholders)")
                }
            } else {
                // activeSourceIds == null: query across non-curated custom chunks
                whereClauses.add("plantId NOT LIKE 'PLANT-%' OR plantId LIKE 'PLANT-USER-%'")
            }

            if (tokens.isNotEmpty()) {
                val tokenConditions = tokens.joinToString(" OR ") {
                    args.add("%$it%")
                    args.add("%$it%")
                    args.add("%$it%")
                    "(text LIKE ? OR keywords LIKE ? OR sourceTitle LIKE ?)"
                }
                whereClauses.add("($tokenConditions)")
            }

            val sql = buildString {
                append("SELECT * FROM document_chunks")
                if (whereClauses.isNotEmpty()) {
                    append(" WHERE ")
                    append(whereClauses.joinToString(" AND "))
                }
                append(" LIMIT 200")
            }

            database.chunkDao().getCandidateChunks(SimpleSQLiteQuery(sql, args.toTypedArray()))
        } catch (_: Exception) {
            emptyList()
        }

        val curatedIds = curatedSet.map { it.id }.toHashSet()
        curatedSet + dbCandidates.filter { !curatedIds.contains(it.id) }
    }

    suspend fun insertCustomPlant(
        plant: MedicinalPlant,
        chunks: List<DocumentChunk>
    ) = withContext(Dispatchers.IO) {
        database.plantDao().insertPlant(plant)
        if (chunks.isNotEmpty()) {
            chunks.chunked(250).forEach { batch ->
                database.chunkDao().insertChunks(batch)
            }
        }
    }

    // Notebook Sources (NotebookLM style materials)
    fun getAllNotebookSources(): Flow<List<NotebookSource>> {
        return database.notebookSourceDao().getAllSources()
    }

    suspend fun insertNotebookSource(
        source: NotebookSource,
        chunks: List<DocumentChunk>
    ) = withContext(Dispatchers.IO) {
        database.notebookSourceDao().insertSource(source)
        if (chunks.isNotEmpty()) {
            chunks.chunked(250).forEach { batch ->
                database.chunkDao().insertChunks(batch)
            }
        }
    }

    suspend fun insertChunkBatch(chunks: List<DocumentChunk>) = withContext(Dispatchers.IO) {
        if (chunks.isNotEmpty()) {
            database.chunkDao().insertChunks(chunks)
        }
    }

    suspend fun insertNotebookSourceMetadata(source: NotebookSource) = withContext(Dispatchers.IO) {
        database.notebookSourceDao().insertSource(source)
    }

    suspend fun setNotebookSourceActive(id: String, isActive: Boolean) = withContext(Dispatchers.IO) {
        database.notebookSourceDao().setSourceActive(id, isActive)
    }

    suspend fun setAllNotebookSourcesActive(isActive: Boolean) = withContext(Dispatchers.IO) {
        database.notebookSourceDao().setAllSourcesActive(isActive)
    }

    suspend fun deleteNotebookSource(id: String) = withContext(Dispatchers.IO) {
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

        // Mode: LOCAL_NOTEBOOK_RAG (SQL-level pre-filtered to keep heap < 2 MB even with 100 MB files)
        val candidateChunks = getCandidateChunksForQuery(question, activeSourceIds)

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
