package com.example.skillcinema.presentation.listpage

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.skillcinema.domain.CollectionsUseCase
import com.example.skillcinema.models.ShortFilmData

class CollectionsPagingSource(
    private val useCase: CollectionsUseCase,
    private val collectionType: String,
    private val collectionId: Long
) : PagingSource<Int, ShortFilmData>() {

    override fun getRefreshKey(state: PagingState<Int, ShortFilmData>): Int? = FIRST_PAGE

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ShortFilmData> {
        val page = params.key ?: FIRST_PAGE
        lateinit var result: LoadResult<Int, ShortFilmData>
        try {
            when (collectionType) {
//                ListPageFragment.APP_COLLECTION -> {
//                    val collection = useCase.getAppCollection(collectionId)
//                    collection?.let {
//                        result = LoadResult.Page(
//                            data = it.films as List<ShortFilmData> as List<ShortFilmDataDto>,
//                            prevKey = null,
//                            nextKey = null //if (collection.films.isEmpty()) null else page + 1
//                        )
//                    }
//                }
                ListPageFragment.WATCHED -> {
                    val collection = useCase.getAllIsWatched()
                    collection.let {
                        result = LoadResult.Page(
                            data = it as List<ShortFilmData>,
                            prevKey = null,
                            nextKey = null //if (collection.films.isEmpty()) null else page + 1
                        )
                    }
                }

                ListPageFragment.FAVORITE -> {
                    val collection = useCase.getAllIsFavorite()
                    collection.let {
                        result = LoadResult.Page(
                            data = it as List<ShortFilmData>,
                            prevKey = null,
                            nextKey = null //if (collection.films.isEmpty()) null else page + 1
                        )
                    }
                }

                ListPageFragment.WANT_TO_WATCH -> {
                    val collection = useCase.getAllIsWantToWatch()
                    collection.let {
                        result = LoadResult.Page(
                            data = it as List<ShortFilmData>,
                            prevKey = null,
                            nextKey = null //if (collection.films.isEmpty()) null else page + 1
                        )
                    }
                }

                ListPageFragment.WAS_INTERESTING -> {
                    val collection = useCase.getAllIsItWasInteresting()
                    collection.let {
                        result = LoadResult.Page(
                            data = it as List<ShortFilmData>,
                            prevKey = null,
                            nextKey = null //if (collection.films.isEmpty()) null else page + 1
                        )
                    }
                }

                else -> {
                    val collection = useCase.getUserCollection(collectionId)
                    collection?.let {
                        result = LoadResult.Page(
                            data = it.films as List<ShortFilmData> as List<ShortFilmData>,
                            prevKey = null,
                            nextKey = null //if (collection.films.isEmpty()) null else page + 1
                        )
                    }
                }
            }
        } catch (_: Exception) {
        }
        return result
    }

    private companion object {
        private const val FIRST_PAGE = 1
    }
}