package com.example.skillcinema.models

data class HomeContainerItem(
    val title: String,
    val films: List<ShortFilmData>,
    val type: String
)