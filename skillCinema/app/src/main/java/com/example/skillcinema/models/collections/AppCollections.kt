package com.example.skillcinema.models.collections

import androidx.room.ColumnInfo

data class AppCollections(
    @ColumnInfo("is_favorite")
    var isFavorite: Boolean? = false,

    @ColumnInfo("is_want_to_watch")
    var isWantToWatch: Boolean? = false,

    @ColumnInfo("is_watched")
    var isWatched: Boolean? = false,

    @ColumnInfo("is_it_was_interesting")
    var isItWasInteresting: Boolean? = false
)
