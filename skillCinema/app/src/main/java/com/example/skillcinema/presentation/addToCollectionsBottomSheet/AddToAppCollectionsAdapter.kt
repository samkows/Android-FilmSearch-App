package com.example.skillcinema.presentation.addToCollectionsBottomSheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.R
import com.example.skillcinema.databinding.ItemAddToCollectionBinding
import com.example.skillcinema.databinding.ItemAddToCollectionFooterBinding

class AddToAppCollectionsAdapter(
    private val checkedChange: (Int, Boolean) -> Unit,
    private val onCreateNewCollectionClick: () -> Unit
) : RecyclerView.Adapter<AddToCollectionsViewHolder>() {
    companion object {
        const val FAVORITE = 0
        const val WANT_TO_WATCH = 1
        const val FOOTER = 2
    }

    private var isFavorite = false
    private var isWantToWatch = false
    private var favoriteDataSize: Int? = null
    private var wantToWatchDataSize: Int? = null

    fun updateFavoriteItem(favoriteDataSize: Int, isFavorite: Boolean) {
        this.favoriteDataSize = favoriteDataSize
        this.isFavorite = isFavorite
        notifyItemChanged(0)
    }

    fun updateWantToWatchItem(wantToWatchDataSize: Int, isWantToWatch: Boolean) {
        this.wantToWatchDataSize = wantToWatchDataSize
        this.isWantToWatch = isWantToWatch
        notifyItemChanged(1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddToCollectionsViewHolder {
        return when (viewType) {
            FOOTER -> AddToCollectionsViewHolder(
                ItemAddToCollectionFooterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> AddToCollectionsViewHolder(
                ItemAddToCollectionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int = 3

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> FAVORITE
            1 -> WANT_TO_WATCH
            else -> FOOTER
        }
    }

    override fun onBindViewHolder(holder: AddToCollectionsViewHolder, position: Int) {
        val context = holder.itemView.context
        val itemType = getItemViewType(position)
        when (itemType) {
            FOOTER -> {
                holder.binding.root.setOnClickListener {
                    onCreateNewCollectionClick()
                }
            }

            FAVORITE -> {
                (holder.binding as ItemAddToCollectionBinding).apply {
                    collectionNameText.text = context.getString(R.string.favorite)
                    collectionSizeText.text = favoriteDataSize?.toString() ?: ""
                    collectionCheckbox.isChecked = isFavorite

                    collectionCheckbox.setOnClickListener {
                        checkedChange(FAVORITE, collectionCheckbox.isChecked)
                    }
                }
            }

            WANT_TO_WATCH -> {
                (holder.binding as ItemAddToCollectionBinding).apply {
                    collectionNameText.text = context.getString(R.string.want_to_watch)
                    collectionSizeText.text = wantToWatchDataSize?.toString() ?: ""
                    collectionCheckbox.isChecked = isWantToWatch

                    collectionCheckbox.setOnClickListener {
                        checkedChange(WANT_TO_WATCH, collectionCheckbox.isChecked)
                    }
                }
            }
        }
    }
}