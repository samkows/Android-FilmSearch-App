package com.example.skillcinema.presentation.seasons

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.Repository
import com.example.skillcinema.domain.SeasonsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SeasonsViewModel(repository: Repository) : ViewModel() {

    private val useCase = SeasonsUseCase(repository)

    private val _isLoading = MutableStateFlow<SeasonsLoadState>(SeasonsLoadState.Loading)
    val isLoading = _isLoading.asStateFlow()

    private fun loadSeasonsData(id: Long) {
        viewModelScope.launch {
            _isLoading.value = SeasonsLoadState.Loading
            try {
                val seasonsData = useCase.getSeasonsData(id)

                _isLoading.value =
                    SeasonsLoadState.Success(seasonsData)

            } catch (e: Exception) {
                _isLoading.value = SeasonsLoadState.Error("error ${e.message}")
                e.message?.let { Log.e("filmViewModel", it) }
            }
        }
    }

    fun loadData(id: Long) {
        loadSeasonsData(id)
    }
}