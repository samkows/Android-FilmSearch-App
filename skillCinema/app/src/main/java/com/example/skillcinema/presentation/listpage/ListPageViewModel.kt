package com.example.skillcinema.presentation.listpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.skillcinema.data.Repository
import com.example.skillcinema.domain.CollectionsUseCase
import com.example.skillcinema.domain.HomeAndListPageUseCase
import com.example.skillcinema.models.ShortFilmData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//todo Done
class ListPageViewModel(
    repository: Repository
) : ViewModel() {
    private val useCase = HomeAndListPageUseCase(repository)
    private val collectionsUseCase = CollectionsUseCase(repository)

    private val _isLoading = MutableStateFlow<ListPageLoadState>(ListPageLoadState.Loading)
    val isLoading = _isLoading.asStateFlow()

    private fun getPagingFlow(
        type: String,
        countryId: Long,
        genreId: Long,
        filmId: Long
    ): Flow<PagingData<ShortFilmData>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                ListPagePagingSource(
                    useCase,
                    type,
                    countryId,
                    genreId,
                    filmId
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

    fun getData(type: String, countryId: Long = 0L, genreId: Long = 0L, filmId: Long = 0L) {
        viewModelScope.launch {
            _isLoading.value = ListPageLoadState.Loading
            kotlin.runCatching {
                getPagingFlow(type, countryId, genreId, filmId)
            }.onSuccess {
                _isLoading.value = ListPageLoadState.Success(it)
            }.onFailure {
                _isLoading.value = ListPageLoadState.Error(it)
            }
        }
    }

    fun getCollectionData(collectionType: String, collectionId: Long = 0L) {
        viewModelScope.launch {
            _isLoading.value = ListPageLoadState.Loading
            kotlin.runCatching {
                getCollectionPagingFlow(type = collectionType, collectionId = collectionId)
            }.onSuccess {
                _isLoading.value = ListPageLoadState.Success(it)
            }.onFailure {
                _isLoading.value = ListPageLoadState.Error(it)
            }
        }
    }

    private fun getCollectionPagingFlow(
        type: String,
        collectionId: Long
    ): Flow<PagingData<ShortFilmData>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                CollectionsPagingSource(
                    useCase = collectionsUseCase,
                    collectionType = type,
                    collectionId = collectionId
                )
            }
        ).flow.cachedIn(viewModelScope)
    }
}