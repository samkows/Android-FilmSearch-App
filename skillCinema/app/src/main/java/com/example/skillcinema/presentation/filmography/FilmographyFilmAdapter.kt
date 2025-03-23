package com.example.skillcinema.presentation.filmography

import android.view.LayoutInflater
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

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: FilmographyFilmViewHolder, position: Int) {
        val item = data.getOrNull(position)
        item?.let {
            with(holder.binding) {
                filmTitleTextView.text = it.nameRu ?: it.nameOriginal

                if (it.genres.isNotEmpty()) {
                    filmGenreTextView.text = it.genres.first().genre ?: ""
                }
                filmRatingTextView.text = it.ratingKinopoisk.toString() ?: "0.0"
            }
            Glide
                .with(holder.binding.root)
                .load(it.posterURLPreview)
                .into(holder.binding.imageViewFilmographyItem)
        }
        holder.binding.root.setOnClickListener {
            item?.let {
                onClick(it)
            }
        }
    }
}

class FilmographyFilmViewHolder(val binding: ItemFilmographyBinding) :
    RecyclerView.ViewHolder(binding.root)
