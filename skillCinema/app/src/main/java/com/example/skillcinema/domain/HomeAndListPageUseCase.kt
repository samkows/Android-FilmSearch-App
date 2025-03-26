package com.example.skillcinema.domain

import com.example.skillcinema.data.Repository
import com.example.skillcinema.models.CountryWithId
import com.example.skillcinema.models.DynamicListIds
import com.example.skillcinema.models.GenreWithId
import com.example.skillcinema.models.GenresAndCountriesData
import com.example.skillcinema.models.ShortFilmDataDto
import com.example.skillcinema.models.ShortFilmDataListDto
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

class HomeAndListPageUseCase(
    private val repository: Repository
) {

    lateinit var firstDynamicIds: DynamicListIds
    lateinit var secondDynamicIds: DynamicListIds

    suspend fun getPopularData(page: Int = 1): ShortFilmDataListDto {
        return checkIsWatched(repository.getPopularFilms(page))
    }

    suspend fun getTop250(page: Int = 1): ShortFilmDataListDto {
        return checkIsWatched(repository.getTopMovies(page))
    }

    suspend fun getSerials(page: Int = 1): ShortFilmDataListDto {
        return checkIsWatched(repository.getSerials(page))
    }

    private suspend fun checkIsWatched(data: ShortFilmDataListDto): ShortFilmDataListDto {
        data.items.forEach {
            it.isWatched = repository.getFilmDataById(it.kinopoiskID).isWatched
        }
        return data
    }

    suspend fun getPremieres(): ShortFilmDataListDto {
        val currentCalendar = Calendar.getInstance()
        val twoWeeksLaterCalendar = Calendar.getInstance()
        val twoWeeksInMillis: Long = 1209600000
        twoWeeksLaterCalendar.timeInMillis = currentCalendar.timeInMillis + twoWeeksInMillis

        val premieres = getPremieresFromRepository(currentCalendar, twoWeeksLaterCalendar)
        val checkedPremieres = checkPremieresDate(premieres, currentCalendar, twoWeeksLaterCalendar)

        return checkRatingAndIsWatched(
            ShortFilmDataListDto(
                total = checkedPremieres.size.toLong(),
                totalPages = 0L,
                items = checkedPremieres
            )
        )
    }

    private suspend fun getPremieresFromRepository(
        currentCalendar: Calendar,
        twoWeeksLaterCalendar: Calendar
    ): List<ShortFilmDataDto> {
        val outputItems: MutableList<ShortFilmDataDto> =
            emptyList<ShortFilmDataDto>().toMutableList()

        val currentMonthString =
            currentCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US)?.uppercase()
        val twoWeeksLaterMonthString =
            twoWeeksLaterCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US)
                ?.uppercase()

        val currentYear = currentCalendar.get(Calendar.YEAR)
        val twoWeeksLaterYear = twoWeeksLaterCalendar.get(Calendar.YEAR)


        if (currentMonthString == twoWeeksLaterMonthString) {
            if (currentMonthString != null) {
                outputItems.addAll(repository.getPremieres(currentYear, currentMonthString).items)
            }
        } else {
            if (currentMonthString != null && twoWeeksLaterMonthString != null) {
                outputItems.addAll(
                    repository.getPremieres(currentYear, currentMonthString).items
                            + repository.getPremieres(
                        twoWeeksLaterYear,
                        twoWeeksLaterMonthString
                    ).items
                )
            }
        }
        return outputItems
    }

    private fun checkPremieresDate(
        premieres: List<ShortFilmDataDto>,
        currentCalendar: Calendar,
        twoWeeksLaterCalendar: Calendar
    ): List<ShortFilmDataDto> {
        val outputItems: MutableList<ShortFilmDataDto> =
            emptyList<ShortFilmDataDto>().toMutableList()

        premieres.forEach {
            val premiereDate = SimpleDateFormat("yyyy-MM-dd").parse(it.premiereRu)
            if (premiereDate != null) {
                if (premiereDate.time in currentCalendar.timeInMillis..twoWeeksLaterCalendar.timeInMillis) {
                    outputItems.add(it)
                }
            }
        }
        return outputItems
    }

    private suspend fun checkRatingAndIsWatched(data: ShortFilmDataListDto): ShortFilmDataListDto {
        val outputItemsSize = if (data.items.size < 20) data.items.size else 20

        var i = 0
        while (i != outputItemsSize) {
            val fullData = repository.getFilmDataById(data.items[i].kinopoiskID)
            data.items[i].ratingKinopoisk = fullData.ratingKinopoisk
            data.items[i].isWatched = fullData.isWatched
            i++
        }
        return data
    }

    suspend fun getDynamicLists(): List<ShortFilmDataListDto> {
        val data = repository.getGenresAndCountries()

        var firstDynamicList = getDynamicList(data, 1)
        while (firstDynamicList.items.isEmpty()) {
            firstDynamicList = getDynamicList(data, 1)
        }

        var secondDynamicList = getDynamicList(data, 2)
        while (secondDynamicList.items.isEmpty() || secondDynamicList == firstDynamicList) {
            secondDynamicList = getDynamicList(data, 2)
        }

        return listOf(
            checkIsWatched(firstDynamicList),
            checkIsWatched(secondDynamicList)
        )
    }

    private suspend fun getDynamicList(
        data: GenresAndCountriesData,
        listNumber: Int
    ): ShortFilmDataListDto {
        val randomCountry = getRandomCountryId(data.countries)
        val randomGenre = getRandomGenreId(data.genres)
        when (listNumber) {
            1 -> firstDynamicIds = DynamicListIds(randomCountry, randomGenre)
            2 -> secondDynamicIds = DynamicListIds(randomCountry, randomGenre)
        }
        return repository.getDynamicList(randomCountry.id, randomGenre.id)
    }

    private fun getRandomCountryId(countries: List<CountryWithId>) =
        countries[Random.nextInt(0, 19)]

    private fun getRandomGenreId(genres: List<GenreWithId>) =
        genres[Random.nextInt(0, 9)]

    suspend fun getListByCountryAndGenre(
        countryId: Long,
        genreId: Long,
        page: Int
    ): ShortFilmDataListDto {
        return checkIsWatched(repository.getDynamicList(countryId, genreId, page))
    }

    suspend fun getSimilarFilmsData(id: Long): ShortFilmDataListDto {
        val similarFilms = repository.getSimilarFilms(id)
        similarFilms.items.forEach {
            val filmData = repository.getFilmDataById(it.kinopoiskID)
            it.ratingKinopoisk = filmData.ratingKinopoisk
            it.genres = filmData.genres
            it.isWatched = filmData.isWatched
        }
        return similarFilms
    }
}