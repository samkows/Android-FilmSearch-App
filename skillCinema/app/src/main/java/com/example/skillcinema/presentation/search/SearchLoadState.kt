package com.example.skillcinema.presentation.search

sealed class SearchLoadState {
    data object Loading : SearchLoadState()
    data class Error(val throwable: Throwable?) : SearchLoadState()
    data object Success : SearchLoadState()
}