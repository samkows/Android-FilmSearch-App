package com.example.skillcinema.models.collections

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.skillcinema.models.FullFilmDataDto

data class UserCollectionWithFilms(
    @Embedded
    val userCollection: UserCollection,

    @Relation(
        parentColumn = "collection_id",
        entityColumn = "kinopoiskId",
        associateBy = Junction(UserCollectionsFilmsCrossRef::class)
    )
    val films: List<FullFilmDataDto>
)
