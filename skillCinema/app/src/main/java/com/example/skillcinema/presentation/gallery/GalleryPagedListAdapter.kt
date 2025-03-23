package com.example.skillcinema.presentation.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.ItemGalleryBigViewBinding
import com.example.skillcinema.databinding.ItemGallerySmallViewBinding
import com.example.skillcinema.models.GalleryItem

class GalleryPagedListAdapter(
    private val onClick: (Int) -> Unit
) : PagingDataAdapter<GalleryItem, GalleryPagedViewHolder>(DiffUtilCallback()) {

    val ITEM_TYPE1 = 0
    val ITEM_TYPE2 = 1

    override fun getItemViewType(position: Int): Int {
        return if ((position + 1) % 3 == 0) ITEM_TYPE2
        else ITEM_TYPE1
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GalleryPagedViewHolder {
        return when (viewType) {
            ITEM_TYPE1 -> GalleryPagedViewHolder(
                ItemGallerySmallViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> GalleryPagedViewHolder(
                ItemGalleryBigViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: GalleryPagedViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            Glide
                .with(holder.binding.root)
                .load(item.previewURL)
                .into(holder.binding.root as ImageView)

            holder.binding.root.setOnClickListener {
                onClick(position)
            }
        }
    }
}

class DiffUtilCallback : DiffUtil.ItemCallback<GalleryItem>() {
    override fun areItemsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean =
        oldItem.imageURL == newItem.imageURL

    override fun areContentsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean =
        oldItem == newItem
}

class GalleryPagedViewHolder(val binding: ViewBinding) :
    RecyclerView.ViewHolder(binding.root)
