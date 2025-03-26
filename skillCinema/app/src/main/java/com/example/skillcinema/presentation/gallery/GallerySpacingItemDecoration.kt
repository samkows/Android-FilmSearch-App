package com.example.skillcinema.presentation.gallery

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GallerySpacingItemDecoration(
    private val spacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val params = view.layoutParams as GridLayoutManager.LayoutParams
        val spanIndex = params.spanIndex

        outRect.bottom = spacing
        if (spanIndex != 0) outRect.left = spacing / 2
    }
}