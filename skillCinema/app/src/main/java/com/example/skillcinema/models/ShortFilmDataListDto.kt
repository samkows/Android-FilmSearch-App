package com.example.skillcinema.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ShortFilmDataListDto(
    val total: Long,
    val totalPages: Long,
    val items: @RawValue List<ShortFilmDataDto>
) : Parcelable