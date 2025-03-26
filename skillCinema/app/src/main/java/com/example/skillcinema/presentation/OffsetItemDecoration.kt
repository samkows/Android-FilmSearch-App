package com.example.skillcinema.presentation

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class OffsetItemDecoration(
    private val spacingInPxLeft: Int = 0,
    private val spacingInPxTop: Int = 0,
    private val spacingInPxRight: Int = 0,
    private val spacingInPxBottom: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = spacingInPxLeft
        outRect.top = spacingInPxTop
        outRect.right = spacingInPxRight
        outRect.bottom = spacingInPxBottom
    }
}