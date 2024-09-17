package com.example.printingapp.ui.screens.main

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    var uiState: UiState by mutableStateOf(UiState.PdfChoosing)
        private set

    fun onUriPicked(uriStr: Uri?) {
        if (uriStr != null) {
            uiState = UiState.PdfChosen(uri = uriStr)
        }
    }
}

@Immutable
sealed interface UiState {
    data object PdfChoosing: UiState
    @JvmInline
    value class PdfChosen(val uri: Uri): UiState
}