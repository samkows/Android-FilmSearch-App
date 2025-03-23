package com.example.skillcinema.presentation.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.R
import com.example.skillcinema.databinding.ItemCollectionBinding
//todo DONE
class AppCollectionsAdapter(
    private val onCollectionClick: (Int) -> Unit
) : RecyclerView.Adapter<CollectionViewHolder>() {
    companion object {
        const val FAVORITE = 0
        const val WANT_TO_WATCH = 1
    }

    private var favoriteDataSize: String = ""
    private var wantToWatchDataSize: String = ""

    fun updateFavoriteItem(favoriteDataSize: String) {
        this.favoriteDataSize = favoriteDataSize
        notifyItemChanged(0)
    }

    fun updateWantToWatchItem(wantToWatchDataSize: String) {
        this.wantToWatchDataSize = wantToWatchDataSize
        notifyItemChanged(1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        return CollectionViewHolder(
            ItemCollectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = 2

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> FAVORITE
            else -> WANT_TO_WATCH
        }
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val context = holder.itemView.context
        holder.binding.apply {
            closeView.visibility = View.GONE

            when (getItemViewType(position)) {
                FAVORITE -> {
                    iconView.setImageResource(R.drawable.ic_favorite)
                    collectionName.text = context.getString(R.string.favorite)
                    filmsQuantity.text = favoriteDataSize

                    root.setOnClickListener { onCollectionClick(FAVORITE) }
                }

                WANT_TO_WATCH -> {
                    iconView.setImageResource(R.drawable.ic_bookmark)
                    collectionName.text = context.getString(R.string.want_to_watch)
                    filmsQuantity.text = wantToWatchDataSize

                    root.setOnClickListener { onCollectionClick(WANT_TO_WATCH) }
                }
            }
        }
    }
}