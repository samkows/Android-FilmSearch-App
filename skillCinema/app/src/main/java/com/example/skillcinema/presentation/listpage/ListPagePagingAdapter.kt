package com.example.skillcinema.presentation.listpage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.ItemFilmBinding
import com.example.skillcinema.models.ShortFilmData

class ListPagePagingAdapter(
    private val onClick: (ShortFilmData) -> Unit
) : PagingDataAdapter<ShortFilmData, ListPagePagingViewHolder>(ListPageDiffUtilCallback()) {
//
//    val ITEM_TYPE1 = 0
//    val ITEM_TYPE2 = 1
//
//    override fun getItemViewType(position: Int): Int {
//        return if ((position + 1)%3 == 0) ITEM_TYPE2
//        else ITEM_TYPE1
//    }

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
        item?.let {
            (holder.binding as ItemFilmBinding).apply {
                filmTitleTextView.text = if (it.nameRu != null) {
                    it.nameRu
                } else {
                    it.nameOriginal
                }
                filmGenreTextView.text = it.genres?.first()?.genre ?: ""
                if (it.ratingKinopoisk != null) {
                    filmRatingTextView.visibility = View.VISIBLE
                    filmRatingTextView.text = it.ratingKinopoisk.toString()
                } else {
                    filmRatingTextView.visibility = View.GONE
                }

                Glide
                    .with(root)
                    .load(it.posterURLPreview)
                    .into(imageViewFilmItem)

//                if (it.appCollections.isWatched == true) {
                if (it.isWatched == true) {
                    gradientImageViewFilmItem.visibility = View.VISIBLE
                    watchedIcon.visibility = View.VISIBLE
                } else {
                    gradientImageViewFilmItem.visibility = View.GONE
                    watchedIcon.visibility = View.GONE
                }
            }
        }
        holder.binding.root.setOnClickListener {
            item?.let {
                onClick(it)
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

class ListPagePagingViewHolder(val binding: ViewBinding) :
    RecyclerView.ViewHolder(binding.root)