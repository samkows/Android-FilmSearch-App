package com.example.skillcinema.models.collections

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.skillcinema.models.FullFilmDataDto

//todo can delete this???
data class FilmWithUserCollections(
    @Embedded
    val film: FullFilmDataDto,

    @Relation(
        parentColumn = "kinopoiskId",
        entityColumn = "collection_id",
        associateBy = Junction(UserCollectionsFilmsCrossRef::class)
    )
    val userCollections: List<UserCollection>
)
