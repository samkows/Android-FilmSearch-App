package com.example.skillcinema.presentation.gallery

import android.R.attr.padding
import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class GallerySpacingItemDecoration(
   // val spanCount: Int,
    private val spacing: Int,
   // val includeEdge: Boolean,
  //  val context: Context

) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
      //  super.getItemOffsets(outRect, view, parent, state!!)

      //  val gridLayoutManager = parent.layoutManager as GridLayoutManager?
       // val spanCount = gridLayoutManager!!.spanCount

        val params = view.layoutParams as GridLayoutManager.LayoutParams

        val spanIndex = params.spanIndex
      //  val spanSize = params.spanSize

        outRect.bottom = spacing
        if (spanIndex != 0) outRect.left = spacing / 2

        // If it is in column 0 you apply the full offset on the start side, else only half
//        if (spanIndex == 0) {
//            //outRect.left = spacing
//            //outRect.right = spacing
//        } else {
//            outRect.left = spacing / 2
//        }
//
//        // If spanIndex + spanSize equals spanCount (it occupies the last column) you apply the full offset on the end, else only half.
//        if (spanIndex + spanSize == spanCount) {
//            //outRect.right = spacing
//        } else {
//           // outRect.right = spacing / 2
//        }
//
//        // just add some vertical padding as well
//      //  outRect.top = spacing /// 2
//        outRect.bottom = spacing /// 2

//        if (isLayoutRTL(parent)) {
//            val tmp = outRect.left
//            outRect.left = outRect.right
//            outRect.right = tmp
//        }
    }

//    override fun getItemOffsets(
//        outRect: Rect,
//        view: View,
//        parent: RecyclerView,
//        state: RecyclerView.State
//    ) {
//        val position = parent.getChildAdapterPosition(view) // item position
//        val column = position % spanCount // item column
//
//        Toast.makeText(context, "position $position", Toast.LENGTH_SHORT).show()
//        Toast.makeText(context, "column $column", Toast.LENGTH_SHORT).show()
//        if (includeEdge) {
//            outRect.left =
//                spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
//            outRect.right =
//                (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
//
//            if (position < spanCount) { // top edge
//                outRect.top = spacing;
//            }
//            outRect.bottom = spacing; // item bottom
//        } else {
//            outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
//            outRect.right =
//                spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
//            if (position >= spanCount) {
//                outRect.top = spacing; // item top
//            }
//        }
//    }
}