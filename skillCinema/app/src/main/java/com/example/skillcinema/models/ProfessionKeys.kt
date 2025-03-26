package com.example.skillcinema.models

import android.content.Context
import androidx.annotation.StringRes
import com.example.skillcinema.R

enum class ProfessionKeys(
    val films: ArrayList<FullFilmDataDto>,
    @StringRes val nameResId: Int
) {
    ACTOR(ArrayList(), R.string.profession_actor),
    ACTRESS(ArrayList(), R.string.profession_actress),
    WRITER(ArrayList(), R.string.profession_writer),
    OPERATOR(ArrayList(), R.string.profession_operator),
    EDITOR(ArrayList(), R.string.profession_editor),
    COMPOSER(ArrayList(), R.string.profession_composer),
    PRODUCER_USSR(ArrayList(), R.string.profession_producer_ussr),
    HIMSELF(ArrayList(), R.string.profession_himself),
    HERSELF(ArrayList(), R.string.profession_herself),
    HRONO_TITR_MALE(ArrayList(), R.string.profession_hrono_titr_male),
    HRONO_TITR_FEMALE(ArrayList(), R.string.profession_hrono_titr_female),
    TRANSLATOR(ArrayList(), R.string.profession_translator),
    DIRECTOR(ArrayList(), R.string.profession_director),
    DESIGN(ArrayList(), R.string.profession_design),
    PRODUCER(ArrayList(), R.string.profession_producer),
    VOICE_DIRECTOR(ArrayList(), R.string.profession_voice_director),
    UNKNOWN(ArrayList(), R.string.profession_unknown);

    fun getLocalizedName(context: Context): String {
        return context.getString(nameResId)
    }
}