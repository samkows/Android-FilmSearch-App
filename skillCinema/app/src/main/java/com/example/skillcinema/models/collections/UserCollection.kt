package com.example.skillcinema.models.collections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_collections_table")
data class UserCollection(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("collection_id")
    val userCollectionId: Long = 0L,

    @ColumnInfo("collection_name")
    val userCollectionName: String?
)