package com.example.skillcinema.domain

import com.example.skillcinema.data.Repository
import com.example.skillcinema.models.Film
import com.example.skillcinema.models.PersonData
import com.example.skillcinema.models.ProfessionKeys
import com.example.skillcinema.models.ShortFilmDataDto
import com.example.skillcinema.models.ShortFilmDataListDto

class PersonUseCase(
    private val repository: Repository
) {

    suspend fun getPersonData(id: Long): PersonData {
        return repository.getPersonData(id)
    }

    suspend fun getTheBestFilmsData(theBestFilms: List<Film>): ShortFilmDataListDto {
        val uniqueFilms = theBestFilms
            .sortedByDescending { it.rating }
            .distinctBy { it.filmID }
            .take(10)

        val outputFilms = uniqueFilms.mapNotNull { film ->
            runCatching {
                repository.getFilmDataById(film.filmID)
            }.getOrNull()
        }

        return ShortFilmDataListDto(
            total = outputFilms.size.toLong(),
            totalPages = 1,
            items = outputFilms as List<ShortFilmDataDto>
        )
    }

    suspend fun getFilmographyData(films: List<Film>, sex: String): ArrayList<ProfessionKeys> {
        ProfessionKeys.entries.forEach { it.films.clear() }

        films
            .sortedByDescending { it.rating }
            .distinctBy { it.filmID }
            .forEach { film ->
                runCatching {
                    when (film.professionKey) {
                        "WRITER" -> ProfessionKeys.WRITER
                        "OPERATOR" -> ProfessionKeys.OPERATOR
                        "EDITOR" -> ProfessionKeys.EDITOR
                        "COMPOSER" -> ProfessionKeys.COMPOSER
                        "PRODUCER_USSR" -> ProfessionKeys.PRODUCER_USSR
                        "HIMSELF" -> ProfessionKeys.HIMSELF
                        "HERSELF" -> ProfessionKeys.HERSELF
                        "HRONO_TITR_MALE" -> ProfessionKeys.HRONO_TITR_MALE
                        "HRONO_TITR_FEMALE" -> ProfessionKeys.HRONO_TITR_FEMALE
                        "TRANSLATOR" -> ProfessionKeys.TRANSLATOR
                        "DIRECTOR" -> ProfessionKeys.DIRECTOR
                        "DESIGN" -> ProfessionKeys.DESIGN
                        "PRODUCER" -> ProfessionKeys.PRODUCER
                        "ACTOR" -> {
                            if (sex == "MALE") ProfessionKeys.ACTOR else ProfessionKeys.ACTRESS
                        }

                        "VOICE_DIRECTOR" -> ProfessionKeys.VOICE_DIRECTOR
                        else -> ProfessionKeys.UNKNOWN

                    }.films.add(repository.getFilmDataById(film.filmID))
                }
            }

        return ProfessionKeys.entries
            .filter { it.films.isNotEmpty() }
            .toCollection(ArrayList())
    }
}