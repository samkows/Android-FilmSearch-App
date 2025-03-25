package com.example.skillcinema.presentation.listpage

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.skillcinema.domain.HomeAndListPageUseCase
import com.example.skillcinema.models.ShortFilmData

//todo DONE
class ListPagePagingSource(
    private val useCase: HomeAndListPageUseCase,
    private val type: String,
    private val countryId: Long = 0L,
    private val genreId: Long = 0L,
    private val filmId: Long = 0L
) : PagingSource<Int, ShortFilmData>() {

    override fun getRefreshKey(state: PagingState<Int, ShortFilmData>): Int = FIRST_PAGE

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ShortFilmData> {
        val page = params.key ?: FIRST_PAGE
        return kotlin.runCatching {
            when (type) {
                ListPageFragment.PREMIERES -> useCase.getPremieres()
                ListPageFragment.POPULAR -> useCase.getPopularData(page)
                ListPageFragment.TOP_250 -> useCase.getTop250(page)
                ListPageFragment.SERIALS -> useCase.getSerials(page)
                ListPageFragment.SIMILAR -> useCase.getSimilarFilmsData(filmId)
                else -> {
                    useCase.getListByCountryAndGenre(countryId, genreId, page)
                }
            }
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