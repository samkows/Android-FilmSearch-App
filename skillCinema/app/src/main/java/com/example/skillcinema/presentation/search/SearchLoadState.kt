package com.example.skillcinema.presentation.search

//todo DONE
sealed class SearchLoadState {
    data object Loading : SearchLoadState()
    data class Error(val message: String?) : SearchLoadState()
    data object Success : SearchLoadState()
}