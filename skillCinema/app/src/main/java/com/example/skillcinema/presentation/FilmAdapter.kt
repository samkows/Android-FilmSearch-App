package com.example.skillcinema.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.ItemFilmBinding
import com.example.skillcinema.databinding.ItemShowAllBinding
import com.example.skillcinema.models.ShortFilmData
import com.example.skillcinema.models.ShortFilmDataListDto

private const val MAX_ITEM_COUNT = 20

//todo DONE
class FilmAdapter(
    private val onClick: (ShortFilmData) -> Unit,
    private val onShowAllClick: () -> Unit
) : RecyclerView.Adapter<FilmViewHolder>() {
    companion object {
        const val ITEM_TYPE1 = 0
        const val ITEM_TYPE2 = 1
    }

    private var data: List<ShortFilmData> = emptyList()
    fun setData(data: ShortFilmDataListDto) {
        this.data = data.items
        notifyDataSetChanged()
    }

    fun setData(data: List<ShortFilmData>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        if (data.size < MAX_ITEM_COUNT) return data.size
        return MAX_ITEM_COUNT + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 20) ITEM_TYPE2
        else ITEM_TYPE1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        return when (viewType) {
            ITEM_TYPE1 -> FilmViewHolder(
                ItemFilmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            else -> FilmViewHolder(
                ItemShowAllBinding.inflate(
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
                data.getOrNull(position)?.let { filmData ->
                    (holder.binding as ItemFilmBinding).apply {
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

            else -> {
                holder.binding.root.setOnClickListener {
                    onShowAllClick()
                }
            }
        }
    }
}

class FilmViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)