package com.example.skillcinema.domain

import com.example.skillcinema.data.Repository
import com.example.skillcinema.models.SeasonsData

class SeasonsUseCase(private val repository: Repository) {

    suspend fun getSeasonsData(serialId: Long): SeasonsData {
        return repository.getSeasonsData(serialId)
    }
}