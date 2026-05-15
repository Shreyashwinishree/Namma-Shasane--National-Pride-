package com.nammashasane.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [InscriptionEntity::class, DamageReportEntity::class],
    version = 1,
    exportSchema = false
)
abstract class HeritageDatabase : RoomDatabase() {
    abstract fun inscriptionDao(): HeritageDao

    companion object {
        fun create(context: Context): HeritageDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                HeritageDatabase::class.java,
                "namma-shasane.db"
            ).build()
        }
    }
}
