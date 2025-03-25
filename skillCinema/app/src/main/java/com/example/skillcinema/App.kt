package com.example.skillcinema

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.skillcinema.data.AppDatabase
import com.example.skillcinema.data.Repository
import com.example.skillcinema.data.SkillCinemaApi
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics

//todo DONE
class App : Application() {
    private lateinit var appDatabase: AppDatabase
    private lateinit var networkingApi: SkillCinemaApi.NetworkingApi
    val repository by lazy { Repository(appDatabase.appDatabaseDao(), networkingApi) }
    val dataStore: DataStore<Preferences> by preferencesDataStore(name = "search_settings")

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true;

        appDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "db"
        )
            .build()

        networkingApi = SkillCinemaApi.RetrofitInstance.networkingApi
    }
}