package com.example.skillcinema.presentation.profile

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CollectionsSpacingItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val params = view.layoutParams as GridLayoutManager.LayoutParams
        val spanIndex = params.spanIndex

        outRect.top = spacing
        if (spanIndex != 0) outRect.left = spacing / 2
        if (spanIndex == 0) outRect.right = spacing / 2
    }
}