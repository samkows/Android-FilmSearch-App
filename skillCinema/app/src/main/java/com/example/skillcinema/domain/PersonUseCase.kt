package com.example.skillcinema.domain

import com.example.skillcinema.data.Repository
import com.example.skillcinema.models.Film
import com.example.skillcinema.models.PersonData
import com.example.skillcinema.models.ProfessionKeys
import com.example.skillcinema.models.ShortFilmData
import com.example.skillcinema.models.ShortFilmDataDto
import com.example.skillcinema.models.ShortFilmDataListDto

class PersonUseCase(
    private val repository: Repository
) {

    suspend fun getPersonData(id: Long): PersonData {
        return repository.getPersonData(id)
    }

    suspend fun getTheBestFilmsData(theBestFilms: List<Film>): ShortFilmDataListDto {
        val outputFilms: MutableList<ShortFilmData> = emptyList<ShortFilmData>().toMutableList()
        val sortedList = theBestFilms.sortedBy { it.rating }.reversed()
        val numberOfOutputItems = if (sortedList.size < 10) sortedList.size else 10

        var i = 0
        while (i != numberOfOutputItems) {
            outputFilms.add(repository.getFilmDataById(sortedList[i].filmID))
            i++
        }

        return ShortFilmDataListDto(
            total = outputFilms.size.toLong(),
            totalPages = 1,
            items = outputFilms.toList() as List<ShortFilmDataDto>
        )
    }

    suspend fun getFilmographyData(films: List<Film>): ArrayList<ProfessionKeys> {
        val array = ProfessionKeys.entries.toTypedArray()
        val outputArray = ArrayList<ProfessionKeys>()

        films.forEach {
            when (it.professionKey) {
                "WRITER" -> ProfessionKeys.WRITER.films
                    .add(repository.getFilmDataById(it.filmID))

                "OPERATOR" -> ProfessionKeys.OPERATOR.films
                    .add(repository.getFilmDataById(it.filmID))

                "EDITOR" -> ProfessionKeys.EDITOR.films
                    .add(repository.getFilmDataById(it.filmID))

                "COMPOSER" -> ProfessionKeys.COMPOSER.films
                    .add(repository.getFilmDataById(it.filmID))

                "PRODUCER_USSR" -> ProfessionKeys.PRODUCER_USSR.films
                    .add(repository.getFilmDataById(it.filmID))

                "HIMSELF" -> ProfessionKeys.HIMSELF.films
                    .add(repository.getFilmDataById(it.filmID))

                "HERSELF" -> ProfessionKeys.HERSELF.films
                    .add(repository.getFilmDataById(it.filmID))

                "HRONO_TITR_MALE" -> ProfessionKeys.HRONO_TITR_MALE.films
                    .add(repository.getFilmDataById(it.filmID))

                "HRONO_TITR_FEMALE" -> ProfessionKeys.HRONO_TITR_FEMALE.films
                    .add(repository.getFilmDataById(it.filmID))

                "TRANSLATOR" -> ProfessionKeys.TRANSLATOR.films
                    .add(repository.getFilmDataById(it.filmID))

                "DIRECTOR" -> ProfessionKeys.DIRECTOR.films
                    .add(repository.getFilmDataById(it.filmID))

                "DESIGN" -> ProfessionKeys.DESIGN.films
                    .add(repository.getFilmDataById(it.filmID))

                "PRODUCER" -> ProfessionKeys.PRODUCER.films
                    .add(repository.getFilmDataById(it.filmID))

                "ACTOR" -> ProfessionKeys.ACTOR.films
                    .add(repository.getFilmDataById(it.filmID))

                "VOICE_DIRECTOR" -> ProfessionKeys.VOICE_DIRECTOR.films
                    .add(repository.getFilmDataById(it.filmID))

                "UNKNOWN" -> ProfessionKeys.UNKNOWN.films
                    .add(repository.getFilmDataById(it.filmID))
            }
        }

        array.forEach {
            if (it.films.isNotEmpty()) {
                outputArray.add(it)
            }
        }

        return outputArray
    }
}