package com.example.skillcinema.presentation.staffListPage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.ItemStaffListpageBinding
import com.example.skillcinema.models.StaffData

//todo DONE
class StaffListPageAdapter(
    private val onClick: (StaffData) -> Unit
) : RecyclerView.Adapter<StaffListPageViewHolder>() {

    private var data: Array<StaffData> = emptyArray()
    fun setData(data: Array<StaffData>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffListPageViewHolder {
        return StaffListPageViewHolder(
            ItemStaffListpageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: StaffListPageViewHolder, position: Int) {
        val item = data.getOrNull(position)
        item?.let {
            holder.binding.apply {
                Glide
                    .with(imageView.context)
                    .load(it.posterURL)
                    .into(imageView)

                nameTextView.text = it.nameRu
                descriptionTextView.text = it.description

                root.setOnClickListener {
                    onClick(item)
                }
            }
        }
    }
}

class StaffListPageViewHolder(val binding: ItemStaffListpageBinding) :
    RecyclerView.ViewHolder(binding.root)