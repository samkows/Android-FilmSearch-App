package com.example.skillcinema.presentation.listpage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.ItemFilmBinding
import com.example.skillcinema.models.ShortFilmData

class ListPagePagingAdapter(
    private val onClick: (ShortFilmData) -> Unit
) : PagingDataAdapter<ShortFilmData, ListPagePagingViewHolder>(ListPageDiffUtilCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListPagePagingViewHolder {
        return ListPagePagingViewHolder(
            ItemFilmBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ListPagePagingViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { filmData ->
            holder.binding.apply {
                Glide
                    .with(root)
                    .load(filmData.posterURLPreview)
                    .into(imageViewFilmItem)

                filmTitleTextView.text =
                    filmData.nameRu ?: filmData.nameOriginal ?: filmData.nameEn ?: ""

                filmGenreTextView.text = filmData.genres?.firstOrNull()?.genre ?: ""

                filmData.ratingKinopoisk?.let { rating ->
                    filmRatingTextView.visibility = View.VISIBLE
                    filmRatingTextView.text = rating.toString()
                } ?: run { filmRatingTextView.visibility = View.GONE }

                if (filmData.isWatched) {
                    gradientImageViewFilmItem.visibility = View.VISIBLE
                    watchedIcon.visibility = View.VISIBLE
                } else {
                    gradientImageViewFilmItem.visibility = View.GONE
                    watchedIcon.visibility = View.GONE
                }

                root.setOnClickListener { onClick(filmData) }
            }
        }
    }
}

class ListPageDiffUtilCallback : DiffUtil.ItemCallback<ShortFilmData>() {
    override fun areItemsTheSame(oldItem: ShortFilmData, newItem: ShortFilmData): Boolean =
        oldItem.kinopoiskID == newItem.kinopoiskID

    override fun areContentsTheSame(oldItem: ShortFilmData, newItem: ShortFilmData): Boolean =
        oldItem == newItem
}

class ListPagePagingViewHolder(val binding: ItemFilmBinding) :
    RecyclerView.ViewHolder(binding.root)