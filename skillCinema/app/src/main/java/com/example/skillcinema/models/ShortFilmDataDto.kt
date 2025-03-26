package com.example.skillcinema.models

import com.google.gson.annotations.SerializedName

data class ShortFilmDataDto(
    @SerializedName(value = "kinopoiskId", alternate = ["filmId"])
    override val kinopoiskID: Long,
    override val nameEn: String,
    override val nameRu: String,
    override val nameOriginal: String,
    override val countries: List<Country>,
    override var genres: List<Genre>,
    override var ratingKinopoisk: Double?,
    @SerializedName("posterUrl")
    override val posterURL: String,
    @SerializedName("posterUrlPreview")
    override val posterURLPreview: String,
    val premiereRu: String,
    override var isWatched: Boolean = false,
    override var isFavorite: Boolean = false,
    override var isWantToWatch: Boolean = false,
    override var isItWasInteresting: Boolean = false
) : ShortFilmData