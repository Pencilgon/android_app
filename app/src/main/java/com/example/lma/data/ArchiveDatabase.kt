package com.example.lma.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.lma.model.MediaCase
import com.example.lma.model.RecoveryEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(
    entities = [
        MediaCase::class,
        RecoveryEvent::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ArchiveDatabase : RoomDatabase() {

    abstract fun archiveDao(): ArchiveDao

    companion object {
        @Volatile
        private var INSTANCE: ArchiveDatabase? = null

        fun getDatabase(context: Context): ArchiveDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also {
                    INSTANCE = it
                }
            }
        }

        private fun buildDatabase(context: Context): ArchiveDatabase {
            lateinit var database: ArchiveDatabase

            database = Room.databaseBuilder(
                context.applicationContext,
                ArchiveDatabase::class.java,
                "lost_media_archive_database"
            )
                .addCallback(object : RoomDatabase.Callback() {

                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)

                        CoroutineScope(
                            SupervisorJob() + Dispatchers.IO
                        ).launch {
                            database.archiveDao().insertCases(
                                SampleCases.cases
                            )
                        }
                    }
                })
                .build()

            return database
        }
    }
}