package com.example.skillcinema.presentation.filmography

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.ItemFilmographyBinding
import com.example.skillcinema.models.FullFilmDataDto
import com.example.skillcinema.models.ShortFilmData

class FilmographyFilmAdapter(
    private val onClick: (ShortFilmData) -> Unit
) : RecyclerView.Adapter<FilmographyFilmViewHolder>() {

    private var data: ArrayList<FullFilmDataDto> = ArrayList()
    fun setData(data: ArrayList<FullFilmDataDto>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmographyFilmViewHolder {
        return FilmographyFilmViewHolder(
            ItemFilmographyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: FilmographyFilmViewHolder, position: Int) {
        val item = data.getOrNull(position)
        item?.let {
            with(holder.binding) {
                Glide
                    .with(holder.binding.root)
                    .load(it.posterURLPreview)
                    .into(holder.binding.imageViewFilmographyItem)

                filmTitleTextView.text = it.nameRu ?: it.nameOriginal ?: it.nameEn ?: ""

                filmGenreTextView.text = item.genres.firstOrNull()?.genre ?: ""

                item.ratingKinopoisk?.let { rating ->
                    filmRatingTextView.visibility = View.VISIBLE
                    filmRatingTextView.text = rating.toString()
                } ?: run {
                    filmRatingTextView.visibility = View.GONE
                }

                root.setOnClickListener { onClick(item) }
            }
        }
    }
}

class FilmographyFilmViewHolder(val binding: ItemFilmographyBinding) :
    RecyclerView.ViewHolder(binding.root)