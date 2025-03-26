package com.example.skillcinema.presentation.filmpage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.ItemGalleryBinding
import com.example.skillcinema.models.GalleryData
import com.example.skillcinema.models.GalleryItem

private const val ITEM_COUNT = 20

class GalleryAdapter(
    private val onClick: (Int, List<GalleryItem>) -> Unit,
) : RecyclerView.Adapter<GalleryViewHolder>() {

    private var data: List<GalleryItem> = emptyList()
    fun setData(data: GalleryData) {
        this.data = data.items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        return GalleryViewHolder(
            ItemGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        if (data.size < ITEM_COUNT) {
            return data.size
        }
        return ITEM_COUNT
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val item = data.getOrNull(position)
        item?.let {
            Glide
                .with(holder.binding.root)
                .load(item.previewURL)
                .into(holder.binding.root)

            holder.binding.root.setOnClickListener {
                onClick(position, data)
            }
        }
    }
}

class GalleryViewHolder(val binding: ItemGalleryBinding) : RecyclerView.ViewHolder(binding.root)