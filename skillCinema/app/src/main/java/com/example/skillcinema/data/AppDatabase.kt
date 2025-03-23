package com.example.skillcinema.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.skillcinema.Converters
import com.example.skillcinema.models.FullFilmDataDto
import com.example.skillcinema.models.collections.UserCollection
import com.example.skillcinema.models.collections.UserCollectionsFilmsCrossRef

@Database(
    entities = [
        FullFilmDataDto::class,
        UserCollection::class,
        UserCollectionsFilmsCrossRef::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDatabaseDao(): AppDatabaseDao
}