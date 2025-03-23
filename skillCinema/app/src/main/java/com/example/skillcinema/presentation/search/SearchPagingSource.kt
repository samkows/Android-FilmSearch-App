package com.example.skillcinema.presentation.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.skillcinema.domain.SearchUseCase
import com.example.skillcinema.models.SearchParams
import com.example.skillcinema.models.ShortFilmData

//todo DONE
class SearchPagingSource(
    private val useCase: SearchUseCase,
    private val searchParams: SearchParams,
    private val keyword: String
) : PagingSource<Int, ShortFilmData>() {

    override fun getRefreshKey(state: PagingState<Int, ShortFilmData>): Int = FIRST_PAGE

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ShortFilmData> {
        val page = params.key ?: FIRST_PAGE

        return kotlin.runCatching {
            useCase.getSearchList(
                searchParams, keyword, page
            )
        }.fold(
            onSuccess = {
                LoadResult.Page(
                    data = it.items,
                    prevKey = null,
                    nextKey = if (it.items.isEmpty() || it.totalPages == 0L) null else page + 1
                )
            },
            onFailure = { LoadResult.Error(it) }
        )
    }

    private companion object {
        private const val FIRST_PAGE = 1
    }
}