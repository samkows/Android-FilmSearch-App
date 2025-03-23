package com.example.skillcinema.domain

import com.example.skillcinema.data.Repository
import com.example.skillcinema.models.GalleryData

class GalleryUseCase(
    private val repository: Repository
) {
    suspend fun getGalleryDataByType(id: Long, type: String, page: Int): GalleryData {
        return repository.getGalleryDataByType(id, type, page)
    }
}