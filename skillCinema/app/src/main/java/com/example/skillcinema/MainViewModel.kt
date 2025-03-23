package com.example.skillcinema

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.Repository
import com.example.skillcinema.models.CountryWithId
import com.example.skillcinema.models.GenreWithId
import com.example.skillcinema.models.SearchParams
import com.example.skillcinema.models.SettingsUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

//todo DONE but maybe this is searchSettingsViewModel???
class MainViewModel(
    private val settingsDataStore: DataStore<Preferences>,
    private val repository: Repository
) : ViewModel() {
    companion object {
        val COUNTRY_ID_KEY = longPreferencesKey("countryId")
        val GENRE_ID_KEY = longPreferencesKey("genreId")
        val ORDER_KEY = stringPreferencesKey("order")
        val TYPE_KEY = stringPreferencesKey("type")
        val RATING_FROM_KEY = intPreferencesKey("ratingFrom")
        val RATING_TO_KEY = intPreferencesKey("ratingTo")
        val YEAR_FROM_KEY = intPreferencesKey("yearFrom")
        val YEAR_TO_KEY = intPreferencesKey("yearTo")
        val IS_WATCHED_KEY = booleanPreferencesKey("isWatched")

        const val DEFAULT_COUNTRY_ID = 1L
        const val DEFAULT_GENRE_ID = 1L
        const val DEFAULT_ORDER = "RATING"
        const val DEFAULT_TYPE = "ALL"
        const val DEFAULT_RATING_FROM = 0
        const val DEFAULT_RATING_TO = 10
        const val DEFAULT_YEAR_FROM = 1991
        const val DEFAULT_YEAR_TO = 2030
        const val DEFAULT_IS_WATCHED = false
    }

    private var countries = MutableLiveData<List<CountryWithId>>()
    private var genres = MutableLiveData<List<GenreWithId>>()

    val searchFilterFlow: StateFlow<SearchParams> = settingsDataStore.data
        .map { preferences: Preferences ->
            SearchParams(
                countryId = preferences[COUNTRY_ID_KEY] ?: DEFAULT_COUNTRY_ID,
                genreId = preferences[GENRE_ID_KEY] ?: DEFAULT_GENRE_ID,
                order = preferences[ORDER_KEY] ?: DEFAULT_ORDER,
                type = preferences[TYPE_KEY] ?: DEFAULT_TYPE,
                ratingFrom = preferences[RATING_FROM_KEY] ?: DEFAULT_RATING_FROM,
                ratingTo = preferences[RATING_TO_KEY] ?: DEFAULT_RATING_TO,
                yearFrom = preferences[YEAR_FROM_KEY] ?: DEFAULT_YEAR_FROM,
                yearTo = preferences[YEAR_TO_KEY] ?: DEFAULT_YEAR_TO,
                isWatched = preferences[IS_WATCHED_KEY] ?: DEFAULT_IS_WATCHED
            )
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, SearchParams())

    val settingsUiState: LiveData<SettingsUiState> = MediatorLiveData<SettingsUiState>().apply {
        addSource(searchFilterFlow.asLiveData()) { filters ->
            value = SettingsUiState(
                filters,
                countries.value ?: emptyList(),
                genres.value ?: emptyList()
            )
        }
        addSource(countries) { countries ->
            value = SettingsUiState(
                searchFilterFlow.value,
                countries,
                genres.value ?: emptyList()
            )
        }
        addSource(genres) { genres ->
            value = SettingsUiState(
                searchFilterFlow.value,
                countries.value ?: emptyList(),
                genres
            )
        }
    }.distinctUntilChanged()


    fun prepareCountriesAndGenres() {
        if (countries.value == null || genres.value == null) {
            viewModelScope.launch {
                val genresAndCountriesData = repository.getGenresAndCountries()
                countries.value = genresAndCountriesData.countries
                genres.value = genresAndCountriesData.genres
            }
        }
    }

    fun updateCountryId(id: Long) {
        viewModelScope.launch {
            settingsDataStore.edit { preferences ->
                preferences[COUNTRY_ID_KEY] = id
            }
        }
    }

    fun updateGenreId(id: Long) {
        viewModelScope.launch {
            settingsDataStore.edit { preferences ->
                preferences[GENRE_ID_KEY] = id
            }
        }
    }

    fun updateOrder(order: String) {
        viewModelScope.launch {
            settingsDataStore.edit { preferences ->
                preferences[ORDER_KEY] = order
            }
        }
    }

    fun updateType(type: String) {
        viewModelScope.launch {
            settingsDataStore.edit { preferences ->
                preferences[TYPE_KEY] = type
            }
        }
    }

    fun updateRatingFrom(ratingFrom: Int) {
        viewModelScope.launch {
            settingsDataStore.edit { preferences ->
                preferences[RATING_FROM_KEY] = ratingFrom
            }
        }
    }

    fun updateRatingTo(ratingTo: Int) {
        viewModelScope.launch {
            settingsDataStore.edit { preferences ->
                preferences[RATING_TO_KEY] = ratingTo
            }
        }
    }

    fun updateYearFrom(yearFrom: Int) {
        viewModelScope.launch {
            settingsDataStore.edit { preferences ->
                preferences[YEAR_FROM_KEY] = yearFrom
            }
        }
    }

    fun updateYearTo(yearTo: Int) {
        viewModelScope.launch {
            settingsDataStore.edit { preferences ->
                preferences[YEAR_TO_KEY] = yearTo
            }
        }
    }

    fun updateIsWatched(isWatched: Boolean) {
        viewModelScope.launch {
            settingsDataStore.edit { preferences ->
                preferences[IS_WATCHED_KEY] = isWatched
            }
        }
    }

    fun resetSettingsToDefault() {
        viewModelScope.launch {
            settingsDataStore.edit { preferences ->
                preferences.clear()
            }
        }
    }
}