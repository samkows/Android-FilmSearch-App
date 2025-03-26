package com.example.skillcinema.models.collections

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    primaryKeys = ["collection_id", "kinopoiskId"],
    tableName = "user_collections_films_cross_ref"
)
data class UserCollectionsFilmsCrossRef(
    @ColumnInfo("collection_id")
    val collectionId: Long,

    @ColumnInfo("kinopoiskId", index = true)
    val filmId: Long
)