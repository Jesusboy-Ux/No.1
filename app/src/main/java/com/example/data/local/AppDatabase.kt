package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.CuratedKnowledgeBase
import com.example.data.model.DocumentChunk
import com.example.data.model.MedicinalPlant
import com.example.data.model.NotebookSource
import com.example.data.model.SavedBookmark
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [MedicinalPlant::class, DocumentChunk::class, SavedBookmark::class, NotebookSource::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
    abstract fun chunkDao(): ChunkDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun notebookSourceDao(): NotebookSourceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "green_ai_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getInstance(context)
                                database.plantDao().insertPlants(CuratedKnowledgeBase.curatedPlants)
                                database.chunkDao().insertChunks(CuratedKnowledgeBase.curatedDocumentChunks)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
