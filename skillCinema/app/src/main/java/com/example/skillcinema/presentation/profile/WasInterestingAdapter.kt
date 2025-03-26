package com.example.skillcinema.presentation.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.ItemClearHistoryBinding
import com.example.skillcinema.databinding.ItemFilmBinding
import com.example.skillcinema.models.ShortFilmData

private const val MAX_ITEM_COUNT = 20

class WasInterestingAdapter(
    private val onClick: (ShortFilmData) -> Unit,
    private val onClearHistoryClick: () -> Unit
) : RecyclerView.Adapter<FilmViewHolder>() {
    companion object {
        const val ITEM_TYPE1 = 0
        const val ITEM_TYPE2 = 1
    }

    private var data: List<ShortFilmData> = emptyList()

    fun setData(data: List<ShortFilmData>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        if (data.size < MAX_ITEM_COUNT) return data.size + 1
        return MAX_ITEM_COUNT + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) ITEM_TYPE2
        else ITEM_TYPE1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        return when (viewType) {
            ITEM_TYPE1 -> FilmViewHolder(
                ItemFilmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            else -> FilmViewHolder(
                ItemClearHistoryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_TYPE1 -> {
                data.getOrNull(position)?.let { item ->
                    (holder.binding as ItemFilmBinding).apply {
                        Glide
                            .with(root)
                            .load(item.posterURLPreview)
                            .into(imageViewFilmItem)

                        filmTitleTextView.text =
                            item.nameRu ?: item.nameOriginal ?: item.nameEn ?: ""

                        filmGenreTextView.text = item.genres?.firstOrNull()?.genre ?: ""

                        item.ratingKinopoisk?.let { rating ->
                            filmRatingTextView.visibility = View.VISIBLE
                            filmRatingTextView.text = rating.toString()
                        } ?: run {
                            filmRatingTextView.visibility = View.GONE
                        }

                        if (item.isWatched) {
                            gradientImageViewFilmItem.visibility = View.VISIBLE
                            watchedIcon.visibility = View.VISIBLE
                        } else {
                            gradientImageViewFilmItem.visibility = View.GONE
                            watchedIcon.visibility = View.GONE
                        }

                        root.setOnClickListener { onClick(item) }
                    }
                }
            }

            else -> {
                holder.binding.root.setOnClickListener {
                    onClearHistoryClick()
                }
            }
        }
    }
}

class FilmViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)