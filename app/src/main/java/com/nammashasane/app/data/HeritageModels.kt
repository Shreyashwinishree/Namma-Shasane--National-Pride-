package com.nammashasane.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inscriptions")
data class InscriptionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val district: String,
    val dynasty: String,
    val era: String,
    val script: String,
    val inscriptionType: String,
    val latitude: Double,
    val longitude: Double,
    val summary: String,
    val translation: String,
    val condition: String,
    val isEndangered: Boolean
)

@Entity(tableName = "damage_reports")
data class DamageReportEntity(
    @PrimaryKey val id: String,
    val inscriptionId: String,
    val inscriptionTitle: String,
    val severity: String,
    val note: String,
    val createdAtMillis: Long,
    val synced: Boolean = false
)

data class DecodeResult(
    val detectedScript: String,
    val confidence: String,
    val modernKannada: String,
    val historicalContext: String
)
