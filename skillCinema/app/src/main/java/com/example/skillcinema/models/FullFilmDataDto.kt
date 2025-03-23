package com.example.skillcinema.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "full_film_data_table")
data class FullFilmDataDto(
    @PrimaryKey
    @ColumnInfo("kinopoiskId")
    @SerializedName("kinopoiskId")
    override val kinopoiskID: Long,
    @ColumnInfo("nameRu")
    override val nameRu: String?,
    @ColumnInfo("nameEn")
    override val nameEn: String?,
    @ColumnInfo("nameOriginal")
    override val nameOriginal: String?,
    @ColumnInfo("posterUrl")
    @SerializedName("posterUrl")
    override val posterURL: String?,
    @ColumnInfo("posterUrlPreview")
    @SerializedName("posterUrlPreview")
    override val posterURLPreview: String?,
    @ColumnInfo("coverUrl")
    @SerializedName("coverUrl")
    val coverURL: String?,
    @ColumnInfo("logoUrl")
    @SerializedName("logoUrl")
    val logoURL: String?,
    @ColumnInfo("ratingKinopoisk")
    override val ratingKinopoisk: Double?,
    @ColumnInfo("webUrl")
    @SerializedName("webUrl")
    val webURL: String?,
    @ColumnInfo("year")
    val year: Long?,
    @ColumnInfo("filmLength")
    val filmLength: Long?,
    @ColumnInfo("slogan")
    val slogan: String?,
    @ColumnInfo("description")
    val description: String?,
    @ColumnInfo("shortDescription")
    val shortDescription: String?,
    @ColumnInfo("type")
    val type: String?,
    @ColumnInfo("ratingAgeLimits")
    val ratingAgeLimits: String?,
    @ColumnInfo("countries")
    override val countries: List<Country>,
    @ColumnInfo("genres")
    override val genres: List<Genre>,
    @ColumnInfo("startYear")
    val startYear: Long?,
    @ColumnInfo("endYear")
    val endYear: Long?,
    @ColumnInfo("serial")
    val serial: Boolean?,
    @ColumnInfo("shortFilm")
    val shortFilm: Boolean?,
    @ColumnInfo("completed")
    val completed: Boolean?,

    //    @Embedded
//    override val appCollections: AppCollections

    @ColumnInfo("is_watched")
    override var isWatched: Boolean = false,
    @ColumnInfo("is_favorite")
    override var isFavorite: Boolean = false,
    @ColumnInfo("is_want_to_watch")
    override var isWantToWatch: Boolean = false,
    @ColumnInfo("is_it_was_interesting")
    override var isItWasInteresting: Boolean = false

) : ShortFilmData
