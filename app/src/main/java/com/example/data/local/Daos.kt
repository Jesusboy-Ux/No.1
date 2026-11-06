package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.DocumentChunk
import com.example.data.model.MedicinalPlant
import com.example.data.model.NotebookSource
import com.example.data.model.SavedBookmark
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM medicinal_plants ORDER BY isUserCreated ASC, scientificName ASC")
    fun getAllPlants(): Flow<List<MedicinalPlant>>

    @Query("SELECT * FROM medicinal_plants WHERE id = :id LIMIT 1")
    suspend fun getPlantById(id: String): MedicinalPlant?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: MedicinalPlant)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlants(plants: List<MedicinalPlant>)

    @Query("DELETE FROM medicinal_plants WHERE id = :id")
    suspend fun deletePlantById(id: String)
}

@Dao
interface ChunkDao {
    @Query("SELECT * FROM document_chunks")
    fun getAllChunks(): Flow<List<DocumentChunk>>

    @Query("SELECT * FROM document_chunks WHERE plantId = :plantId")
    suspend fun getChunksForPlant(plantId: String): List<DocumentChunk>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChunk(chunk: DocumentChunk)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChunks(chunks: List<DocumentChunk>)

    @Query("SELECT * FROM document_chunks WHERE text LIKE '%' || :query || '%' OR keywords LIKE '%' || :query || '%' OR plantScientificName LIKE '%' || :query || '%'")
    suspend fun searchChunks(query: String): List<DocumentChunk>

    @Query("DELETE FROM document_chunks WHERE sourceId = :sourceId")
    suspend fun deleteChunksBySourceId(sourceId: String)
}

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM saved_bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<SavedBookmark>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: SavedBookmark)

    @Query("DELETE FROM saved_bookmarks WHERE targetId = :targetId")
    suspend fun deleteBookmarkByTargetId(targetId: String)

    @Query("DELETE FROM saved_bookmarks WHERE id = :id")
    suspend fun deleteBookmarkById(id: String)

    @Query("SELECT COUNT(*) FROM saved_bookmarks WHERE targetId = :targetId")
    suspend fun isBookmarked(targetId: String): Int
}

@Dao
interface NotebookSourceDao {
    @Query("SELECT * FROM notebook_sources ORDER BY timestamp DESC")
    fun getAllSources(): Flow<List<NotebookSource>>

    @Query("SELECT * FROM notebook_sources WHERE id = :id LIMIT 1")
    suspend fun getSourceById(id: String): NotebookSource?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSource(source: NotebookSource)

    @Query("UPDATE notebook_sources SET isActive = :isActive WHERE id = :id")
    suspend fun setSourceActive(id: String, isActive: Boolean)

    @Query("UPDATE notebook_sources SET isActive = :isActive")
    suspend fun setAllSourcesActive(isActive: Boolean)

    @Query("DELETE FROM notebook_sources WHERE id = :id")
    suspend fun deleteSourceById(id: String)
}
