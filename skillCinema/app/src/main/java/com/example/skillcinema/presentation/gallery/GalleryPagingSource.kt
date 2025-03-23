package com.example.skillcinema.presentation.gallery

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.skillcinema.domain.GalleryUseCase
import com.example.skillcinema.models.GalleryItem

class GalleryPagingSource(
    private val useCase: GalleryUseCase,
    private val filmId: Long,
    private val galleryType: String
) : PagingSource<Int, GalleryItem>() {

    override fun getRefreshKey(state: PagingState<Int, GalleryItem>): Int? = FIRST_PAGE

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryItem> {
        val page = params.key ?: FIRST_PAGE
        return kotlin.runCatching {
            useCase.getGalleryDataByType(filmId, galleryType, page)
        }.fold(
            onSuccess = {
                LoadResult.Page(
                    data = it.items,
                    prevKey = null,
                    nextKey = if (it.items.isEmpty()) null else page + 1
                )
            },
            onFailure = { LoadResult.Error(it) }
        )
    }

    private companion object {
        private val FIRST_PAGE = 1
    }
}