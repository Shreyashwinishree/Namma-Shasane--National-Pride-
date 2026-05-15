package com.nammashasane.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HeritageDao {
    @Query("SELECT * FROM inscriptions ORDER BY title")
    fun observeInscriptions(): Flow<List<InscriptionEntity>>

    @Query("SELECT COUNT(*) FROM inscriptions")
    suspend fun inscriptionCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInscriptions(items: List<InscriptionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReport(report: DamageReportEntity)

    @Query("SELECT * FROM damage_reports ORDER BY createdAtMillis DESC")
    fun observeReports(): Flow<List<DamageReportEntity>>
}
