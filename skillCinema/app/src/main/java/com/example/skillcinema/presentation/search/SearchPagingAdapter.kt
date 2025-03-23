package com.example.skillcinema.presentation.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.ItemFilmographyBinding
import com.example.skillcinema.models.ShortFilmData

class SearchPagingAdapter(
    private val onClick: (ShortFilmData) -> Unit
) : PagingDataAdapter<ShortFilmData, SearchPagingViewHolder>(SearchDiffUtilCallback()) {

    override fun onBindViewHolder(holder: SearchPagingViewHolder, position: Int) {
        getItem(position)?.let { item ->
            holder.binding.apply {
                Glide
                    .with(root)
                    .load(item.posterURLPreview)
                    .into(imageViewFilmographyItem)

                filmTitleTextView.text = item.nameRu ?: item.nameOriginal ?: item.nameEn ?: ""

                filmGenreTextView.text = item.genres?.firstOrNull()?.genre ?: ""

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchPagingViewHolder {
        return SearchPagingViewHolder(
            ItemFilmographyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }
}

class SearchDiffUtilCallback : DiffUtil.ItemCallback<ShortFilmData>() {
    override fun areItemsTheSame(oldItem: ShortFilmData, newItem: ShortFilmData): Boolean =
        oldItem.kinopoiskID == newItem.kinopoiskID

    override fun areContentsTheSame(oldItem: ShortFilmData, newItem: ShortFilmData): Boolean =
        oldItem == newItem //todo other is DONE
}

class SearchPagingViewHolder(val binding: ItemFilmographyBinding) :
    RecyclerView.ViewHolder(binding.root)