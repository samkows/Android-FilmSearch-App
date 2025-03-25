package com.example.skillcinema.presentation.listpage

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.skillcinema.domain.CollectionsUseCase
import com.example.skillcinema.models.ShortFilmData

//TODO DONE
class CollectionsPagingSource(
    private val useCase: CollectionsUseCase,
    private val collectionType: String,
    private val collectionId: Long
) : PagingSource<Int, ShortFilmData>() {

    override fun getRefreshKey(state: PagingState<Int, ShortFilmData>): Int = FIRST_PAGE

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ShortFilmData> {
        return try {
            val data = when (collectionType) {
                ListPageFragment.WATCHED -> useCase.getAllIsWatched()
                ListPageFragment.FAVORITE -> useCase.getAllIsFavorite()
                ListPageFragment.WANT_TO_WATCH -> useCase.getAllIsWantToWatch()
                ListPageFragment.WAS_INTERESTING -> useCase.getAllIsItWasInteresting()
                else -> useCase.getUserCollection(collectionId)?.films ?: emptyList()
            }

            LoadResult.Page(
                data = data as List<ShortFilmData>,
                prevKey = null,
                nextKey = null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private companion object {
        private const val FIRST_PAGE = 1
    }
}