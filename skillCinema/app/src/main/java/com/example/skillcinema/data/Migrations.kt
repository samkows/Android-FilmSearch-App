package com.example.skillcinema.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2){
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `favorite` (`film_id` INTEGER NOT NULL, PRIMARY KEY(`film_id`))")
    }
}