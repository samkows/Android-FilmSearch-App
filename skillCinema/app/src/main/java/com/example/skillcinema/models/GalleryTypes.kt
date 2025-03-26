package com.example.skillcinema.models

import android.content.Context
import androidx.annotation.StringRes
import com.example.skillcinema.R

enum class GalleryTypes(@StringRes val titleResId: Int, var quantity: Long = 0) {
    STILL(R.string.gallery_still),
    SHOOTING(R.string.gallery_shooting),
    POSTER(R.string.gallery_poster),
    FAN_ART(R.string.gallery_fan_art),
    PROMO(R.string.gallery_promo),
    CONCEPT(R.string.gallery_concept),
    WALLPAPER(R.string.gallery_wallpaper),
    COVER(R.string.gallery_cover),
    SCREENSHOT(R.string.gallery_screenshot);

    fun getLocalizedName(context: Context): String {
        return context.getString(titleResId)
    }
}