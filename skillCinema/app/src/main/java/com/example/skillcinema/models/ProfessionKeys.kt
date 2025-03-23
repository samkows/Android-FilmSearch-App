package com.example.skillcinema.models

enum class ProfessionKeys(var films: ArrayList<FullFilmDataDto>) {
    ACTOR(ArrayList()),
    WRITER(ArrayList()),
    OPERATOR(ArrayList()),
    EDITOR(ArrayList()),
    COMPOSER(ArrayList()),
    PRODUCER_USSR(ArrayList()),
    HIMSELF(ArrayList()),
    HERSELF(ArrayList()),
    HRONO_TITR_MALE(ArrayList()),
    HRONO_TITR_FEMALE(ArrayList()),
    TRANSLATOR(ArrayList()),
    DIRECTOR(ArrayList()),
    DESIGN(ArrayList()),
    PRODUCER(ArrayList()),
    VOICE_DIRECTOR(ArrayList()),
    UNKNOWN(ArrayList())
}