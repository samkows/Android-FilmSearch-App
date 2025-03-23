package com.example.skillcinema.models

import com.example.skillcinema.presentation.home.HomeAdapter

data class HomeContainerItem(
    val title: String,
    val films: List<ShortFilmData>,
    val type: String,
   // val viewType: Int = HomeAdapter.TYPE_CONTAINER
)