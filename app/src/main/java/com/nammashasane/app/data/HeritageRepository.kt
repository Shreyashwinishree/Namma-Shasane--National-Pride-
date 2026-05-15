package com.nammashasane.app.data

import kotlinx.coroutines.flow.Flow
import java.util.UUID

class HeritageRepository(private val dao: HeritageDao) {
    val inscriptions: Flow<List<InscriptionEntity>> = dao.observeInscriptions()
    val reports: Flow<List<DamageReportEntity>> = dao.observeReports()

    suspend fun seedIfNeeded() {
        if (dao.inscriptionCount() > 0) return
        dao.insertInscriptions(seedInscriptions)
    }

    suspend fun queueReport(inscription: InscriptionEntity, severity: String, note: String) {
        dao.saveReport(
            DamageReportEntity(
                id = UUID.randomUUID().toString(),
                inscriptionId = inscription.id,
                inscriptionTitle = inscription.title,
                severity = severity,
                note = note.ifBlank { "No extra note provided." },
                createdAtMillis = System.currentTimeMillis()
            )
        )
    }

    fun decodeInscriptionImage(imageLabel: String?): DecodeResult {
        return DecodeResult(
            detectedScript = "Old Kannada / Halegannada",
            confidence = if (imageLabel == null) "Demo mode" else "High confidence demo",
            modernKannada = "Ee shasana sthaniya dana, kere nirmanakke sahaya, mattu devalaya rakshaneya bagge heluttade.",
            historicalContext = "The wording and honorific style match medieval Karnataka grants. In production this service should send OCR output to Gemini with dynasty, location, and paleography prompts."
        )
    }
}

private val seedInscriptions = listOf(
    InscriptionEntity(
        id = "begur-hero-stone",
        title = "Begur Hero Stone",
        district = "Bengaluru Urban",
        dynasty = "Western Ganga",
        era = "9th century CE",
        script = "Old Kannada",
        inscriptionType = "Hero stone",
        latitude = 12.8788,
        longitude = 77.6377,
        summary = "One of the earliest records mentioning Bengaluru, commemorating warriors and local conflict.",
        translation = "This memorial praises brave men who stood in battle near Bengavaluru and earned lasting honor.",
        condition = "Protected but needs clearer public interpretation.",
        isEndangered = false
    ),
    InscriptionEntity(
        id = "halmidi",
        title = "Halmidi Inscription",
        district = "Hassan",
        dynasty = "Kadamba",
        era = "5th-6th century CE",
        script = "Early Kannada",
        inscriptionType = "Royal grant",
        latitude = 13.0068,
        longitude = 75.9932,
        summary = "A landmark early Kannada record connected with royal patronage and land grants.",
        translation = "The king grants land and honors loyal service, preserving rights for the village community.",
        condition = "Museum preserved.",
        isEndangered = false
    ),
    InscriptionEntity(
        id = "shravanabelagola",
        title = "Shravanabelagola Tyagada Brahmadeva Pillar",
        district = "Hassan",
        dynasty = "Western Ganga",
        era = "10th century CE",
        script = "Kannada and Sanskrit",
        inscriptionType = "Jain record",
        latitude = 12.8574,
        longitude = 76.4880,
        summary = "A Jain heritage record linked to patronage, pilgrimage, and monastic memory.",
        translation = "The inscription records devotion, gifts, and the merit of preserving sacred spaces.",
        condition = "Protected heritage zone.",
        isEndangered = false
    ),
    InscriptionEntity(
        id = "aithihasa-lakkundi",
        title = "Lakkundi Temple Record",
        district = "Gadag",
        dynasty = "Kalyani Chalukya",
        era = "11th century CE",
        script = "Kannada",
        inscriptionType = "Temple grant",
        latitude = 15.3870,
        longitude = 75.7197,
        summary = "Documents temple endowments and the cultural strength of Lakkundi's medieval craft networks.",
        translation = "Oil, lamps, and land revenue are dedicated so worship and learning continue every day.",
        condition = "Weathered surface, partial reading difficult.",
        isEndangered = true
    ),
    InscriptionEntity(
        id = "talakad",
        title = "Talakad Riverbank Slab",
        district = "Mysuru",
        dynasty = "Chola / Hoysala period",
        era = "11th-12th century CE",
        script = "Kannada",
        inscriptionType = "Administrative order",
        latitude = 12.1886,
        longitude = 77.0306,
        summary = "A riverbank record describing local administration, tax boundaries, and temple maintenance.",
        translation = "Village officers confirm boundaries and assign a share of produce for public works.",
        condition = "At risk from erosion and casual handling.",
        isEndangered = true
    )
)
