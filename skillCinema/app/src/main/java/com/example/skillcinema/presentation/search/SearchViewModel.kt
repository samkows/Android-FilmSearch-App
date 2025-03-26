package com.example.skillcinema.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.skillcinema.domain.SearchUseCase
import com.example.skillcinema.models.SearchParams
import com.example.skillcinema.models.ShortFilmData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchUseCase: SearchUseCase
) : ViewModel() {

    private val _searchParams = MutableStateFlow(SearchParams())

    private val _isLoading = MutableStateFlow<SearchLoadState>(SearchLoadState.Success)
    val isLoading = _isLoading.asStateFlow()

    private val _keyword = MutableStateFlow("")
    val keyword = _keyword.asStateFlow()

    private val _pagingDataFlow = MutableStateFlow<Flow<PagingData<ShortFilmData>>?>(null)
    val pagingDataFlow = _pagingDataFlow.asStateFlow()


    init {
        viewModelScope.launch {
            combine(_searchParams, _keyword) { params, keyword ->
                Pair(params, keyword)
            }.collect { (params, keyword) ->
                _isLoading.value = SearchLoadState.Loading

                if (keyword.isNotEmpty()) {
                    kotlin.runCatching {
                        getPagingFlow(params, keyword)
                    }.onSuccess { flow ->
                        _pagingDataFlow.value = flow
                        _isLoading.value = SearchLoadState.Success
                    }.onFailure { error ->
                        _isLoading.value = SearchLoadState.Error(error)
                    }
                } else {
                    _pagingDataFlow.value = flow { emit(PagingData.empty()) }
                    _isLoading.value = SearchLoadState.Success
                }
            }
        }
    }

    private fun getPagingFlow(
        params: SearchParams, keyword: String
    ): Flow<PagingData<ShortFilmData>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                SearchPagingSource(searchUseCase, params, keyword)
            }
        ).flow.cachedIn(viewModelScope)
    }

    fun updateSearchParams(params: SearchParams) {
        _searchParams.value = params
    }

    fun updateKeyword(keyword: String) {
        _keyword.value = keyword
    }
}