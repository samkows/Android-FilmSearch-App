package com.example.skillcinema.presentation.filmpage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.ItemStaffBinding
import com.example.skillcinema.models.StaffData
import kotlin.math.min

class StaffAdapter(
    private val onItemClick: (StaffData) -> Unit,
    private val maxItems: Int
) : RecyclerView.Adapter<StaffViewHolder>() {

    private var data: List<StaffData> = emptyList()
    fun setData(data: List<StaffData>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = min(data.size, maxItems)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffViewHolder {
        return StaffViewHolder(
            ItemStaffBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: StaffViewHolder, position: Int) {
        val item = data[position]
        holder.binding.apply {
            Glide
                .with(imageView.context)
                .load(item.posterURL)
                .into(imageView)

            nameTextView.text = item.nameRu
            descriptionTextView.text = item.description

            root.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}

class StaffViewHolder(val binding: ItemStaffBinding) : RecyclerView.ViewHolder(binding.root)