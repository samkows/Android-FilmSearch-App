package com.example.skillcinema.models

import android.os.Parcelable
import androidx.lifecycle.LiveData
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class FullFilmListLiveData(
    val total: Long,
    val totalPages: Long,
    val items: @RawValue LiveData<List<FullFilmDataDto?>>
) : Parcelable