package com.example.skillcinema.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PersonData (
    @SerializedName("personId")
    val personID: Long,
    val nameRu: String,
    val nameEn: String,
    val sex: String,
    @SerializedName("posterUrl")
    val posterURL: String,
    val profession: String,
    val films: List<Film>
) : Parcelable

@Parcelize
data class Film (
    @SerializedName("filmId")
    val filmID: Long,
    val nameRu: String,
    val nameEn: String,
    val rating: String,
    val general: Boolean,
    val description: String,
    val professionKey: String
) : Parcelable

