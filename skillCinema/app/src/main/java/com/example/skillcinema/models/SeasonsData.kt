package com.example.skillcinema.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class SeasonsData(
    val total: Long,
    @SerializedName("items")
    val seasons: List<Season>
)

@Parcelize
data class Season(
    val number: Long,
    val episodes: List<Episode>
) : Parcelable

@Parcelize
data class Episode(
    val seasonNumber: Long,
    val episodeNumber: Long,
    val nameRu: String?,
    val nameEn: String?,
    val synopsis: String?,
    val releaseDate: String?
) : Parcelable