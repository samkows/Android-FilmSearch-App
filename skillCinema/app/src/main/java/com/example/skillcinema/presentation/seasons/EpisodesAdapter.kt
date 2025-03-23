package com.example.skillcinema.presentation.seasons

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.databinding.ItemEpisodeBinding
import com.example.skillcinema.models.Episode
import java.text.SimpleDateFormat

class EpisodesAdapter : RecyclerView.Adapter<EpisodesViewHolder>() {

    private var data: List<Episode> = emptyList()
    fun setData(data: List<Episode>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodesViewHolder {
        return EpisodesViewHolder(
            ItemEpisodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: EpisodesViewHolder, position: Int) {
        val item = data.getOrNull(position)
        item?.let {
            val nameRu = if (!item.nameRu.isNullOrEmpty()) item.nameRu else ""
            holder.binding.episodeTitle.text = "${item.episodeNumber} серия. $nameRu"

            if (!item.nameEn.isNullOrEmpty()) {
                holder.binding.episodeOriginTitle.apply {
                    visibility = View.VISIBLE
                    text = item.nameEn
                }
            }

            if (!item.synopsis.isNullOrEmpty()) {
                holder.binding.episodeSynopsis.apply {
                    visibility = View.VISIBLE
                    text = item.synopsis
                }
            }

            val formatFrom = SimpleDateFormat("yyyy-MM-dd")
            val formatTo = SimpleDateFormat("dd MMMM yyyy")

            if (!item.releaseDate.isNullOrEmpty()) {
                val dateFromApi = formatFrom.parse(item.releaseDate)
                val formattedDate = formatTo.format(dateFromApi)
                holder.binding.releaseDate.apply {
                    visibility = View.VISIBLE
                    text = formattedDate
                }
            }
        }
    }
}

class EpisodesViewHolder(val binding: ItemEpisodeBinding) : RecyclerView.ViewHolder(binding.root)