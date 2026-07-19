package com.quangthe.nhatky.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.quangthe.nhatky.data.dao.*
import com.quangthe.nhatky.data.entities.*

@Database(
    entities = [
        DiaryEntity::class,
        PhotoUriEntity::class,
        SimpleNoteEntity::class,
        NoteFolderEntity::class,
        TodoTaskEntity::class,
        TodoItemEntity::class,
        ActionLogEntity::class,
        DDayEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun diaryDao(): DiaryDao
    abstract fun noteDao(): NoteDao
    abstract fun taskDao(): TaskDao
    abstract fun actionLogDao(): ActionLogDao
    abstract fun dDayDao(): DDayDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "easydiary.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
