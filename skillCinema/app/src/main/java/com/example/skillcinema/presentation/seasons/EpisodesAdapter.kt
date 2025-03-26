package com.example.skillcinema.presentation.seasons

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.databinding.ItemEpisodeBinding
import com.example.skillcinema.models.Episode
import java.text.SimpleDateFormat
import java.util.Locale

class EpisodesAdapter : RecyclerView.Adapter<EpisodesViewHolder>() {

    private val dateFormatFrom = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dateFormatTo = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

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

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: EpisodesViewHolder, position: Int) {
        data.getOrNull(position)?.let { bindEpisode(holder.binding, it) }
    }

    private fun bindEpisode(binding: ItemEpisodeBinding, episode: Episode) {
        with(binding) {
            episodeTitle.text = formatTitle(episode)
            episodeOriginTitle.setVisibleWithText(episode.nameEn)
            episodeSynopsis.setVisibleWithText(episode.synopsis)
            releaseDate.setVisibleWithText(formatDate(episode.releaseDate))
        }
    }

    private fun formatDate(dateString: String?): String? {
        return try {
            dateString?.let { dateFormatFrom.parse(it)?.let { it1 -> dateFormatTo.format(it1) } }
        } catch (e: Exception) {
            null
        }
    }

    private fun formatTitle(episode: Episode) =
        "${episode.episodeNumber} серия. ${episode.nameRu ?: ""}"

    private fun TextView.setVisibleWithText(text: String?) {
        visibility = if (!text.isNullOrEmpty()) {
            this.text = text
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}

class EpisodesViewHolder(val binding: ItemEpisodeBinding) : RecyclerView.ViewHolder(binding.root)