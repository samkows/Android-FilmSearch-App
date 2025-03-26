package com.example.skillcinema.presentation.settings

sealed class SettingsLoadState {
    data object Loading : SettingsLoadState()
    data class Error(val throwable: Throwable?) : SettingsLoadState()
    data object Success : SettingsLoadState()
}