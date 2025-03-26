package com.example.skillcinema.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GalleryData(
    val total: Long,
    val totalPages: Long,
    val items: List<GalleryItem>
)

@Parcelize
data class GalleryItem(
    @SerializedName("imageUrl")
    val imageURL: String,

    @SerializedName("previewUrl")
    val previewURL: String
) : Parcelable