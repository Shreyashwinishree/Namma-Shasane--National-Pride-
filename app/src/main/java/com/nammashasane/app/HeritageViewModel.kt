package com.nammashasane.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nammashasane.app.data.DecodeResult
import com.nammashasane.app.data.DamageReportEntity
import com.nammashasane.app.data.HeritageRepository
import com.nammashasane.app.data.InscriptionEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HeritageUiState(
    val inscriptions: List<InscriptionEntity> = emptyList(),
    val reports: List<DamageReportEntity> = emptyList()
)

class HeritageViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: HeritageRepository =
        (application as NammaShasaneApplication).repository

    val uiState: StateFlow<HeritageUiState> =
        repository.inscriptions.map { HeritageUiState(inscriptions = it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HeritageUiState())

    val reports: StateFlow<List<DamageReportEntity>> =
        repository.reports.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch { repository.seedIfNeeded() }
    }

    fun queueReport(inscription: InscriptionEntity, severity: String, note: String) {
        viewModelScope.launch { repository.queueReport(inscription, severity, note) }
    }

    fun decodeImage(imageLabel: String?): DecodeResult = repository.decodeInscriptionImage(imageLabel)
}
