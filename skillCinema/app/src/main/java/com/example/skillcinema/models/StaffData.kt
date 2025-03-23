package com.example.skillcinema.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class StaffData(
    @SerializedName("staffId")
    val staffID: Long,
    val nameRu: String,
    val nameEn: String,
    val description: String,
    @SerializedName("posterUrl")
    val posterURL: String,
    val professionText: String,
    val professionKey: String
) : Parcelable