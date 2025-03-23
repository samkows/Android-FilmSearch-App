package com.example.skillcinema

import androidx.room.TypeConverter
import com.example.skillcinema.models.Country
import com.example.skillcinema.models.Genre
import com.example.skillcinema.models.collections.UserCollection
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun listToJsonString(value: List<Country>?): String = Gson().toJson(value)

    @TypeConverter
    fun jsonStringToList(value: String) = Gson().fromJson(value, Array<Country>::class.java).toList()

    @TypeConverter
    fun genresListToJsonString(value: List<Genre>?): String = Gson().toJson(value)

    @TypeConverter
    fun genresJsonStringToList(value: String) = Gson().fromJson(value, Array<Genre>::class.java).toList()

    //todo del???
    @TypeConverter
    fun userCollectionsListToJsonString(value: List<UserCollection>?): String = Gson().toJson(value)

    @TypeConverter
    fun userCollectionsJsonStringToList(value: String) = Gson().fromJson(value, Array<UserCollection>::class.java).toList()
}