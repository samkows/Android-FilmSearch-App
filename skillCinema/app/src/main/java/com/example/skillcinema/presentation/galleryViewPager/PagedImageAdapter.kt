package com.example.skillcinema.presentation.galleryViewPager

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.ViewCompat.canScrollHorizontally
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.ItemGalleryViewPagerBinding
import com.example.skillcinema.models.GalleryItem


// TouchImageView adapter from https://github.com/MikeOrtiz/TouchImageView/tree/master
class PagedImageAdapter :
    PagingDataAdapter<GalleryItem, PagedImageAdapter.ImageVH>(DiffUtilCallback()) {

    class ImageVH(val binding: ItemGalleryViewPagerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageVH {
        val binding = ItemGalleryViewPagerBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)


        binding.imageView.setOnTouchListener { view, event ->
            var result = true
            //can scroll horizontally checks if there's still a part of the image
            //that can be scrolled until you reach the edge
            if (event.pointerCount >= 2 || view.canScrollHorizontally(1) && canScrollHorizontally(
                    view,
                    -1
                )
            ) {
                //multi-touch event
                result = when (event.action) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                        // Disallow RecyclerView to intercept touch events.
                        parent.requestDisallowInterceptTouchEvent(true)
                        // Disable touch on view
                        false
                    }

                    MotionEvent.ACTION_UP -> {
                        // Allow RecyclerView to intercept touch events.
                        parent.requestDisallowInterceptTouchEvent(false)
                        true
                    }

                    else -> true
                }
            }
            result
        }

        return ImageVH(binding)
    }

    override fun onBindViewHolder(holder: ImageVH, position: Int) {
        val item = getItem(position)
        item?.let {
            Glide
                .with(holder.binding.root)
                .load(item.imageURL)
                .into(holder.binding.imageView)
        }
    }
}

class DiffUtilCallback : DiffUtil.ItemCallback<GalleryItem>() {
    override fun areItemsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean =
        oldItem.imageURL == newItem.imageURL

    override fun areContentsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean =
        oldItem == newItem
}