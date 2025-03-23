package com.example.skillcinema.presentation.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.skillcinema.R
import com.example.skillcinema.databinding.ItemCountryGenreChangerBinding
import com.example.skillcinema.models.CountryWithId
import com.example.skillcinema.models.GenreWithId

//todo DONE
class CountryGenreChangerAdapter(
    private val onClick: (Long) -> Unit
) : RecyclerView.Adapter<ChangerViewHolder>() {

    private var type = ""
    private var countries: List<CountryWithId> = emptyList()
    private var genres: List<GenreWithId> = emptyList()
    private var checkedId = 0L

    fun setCountriesData(countries: List<CountryWithId>, checkedId: Long) {
        type = "countries"
        this.countries = countries
        this.checkedId = checkedId
        notifyDataSetChanged()
    }

    fun setGenresData(genres: List<GenreWithId>, checkedId: Long) {
        type = "genres"
        this.genres = genres
        this.checkedId = checkedId
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChangerViewHolder {
        return ChangerViewHolder(
            ItemCountryGenreChangerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = if (type == "countries") countries.size else genres.size

    override fun onBindViewHolder(holder: ChangerViewHolder, position: Int) {
        holder.binding.root.apply {
            setBackgroundColor(
                ContextCompat.getColor(context, android.R.color.transparent)
            )
            when (type) {
                "countries" -> bindItem(
                    text = countries[position].country,
                    id = countries[position].id,
                    onClick = { onClick(countries[position].id) }
                )

                "genres" -> bindItem(
                    text = genres[position].genre.replaceFirstChar { it.uppercase() },
                    id = genres[position].id,
                    onClick = { onClick(genres[position].id) }
                )
            }
        }
    }

    private fun TextView.bindItem(text: String, id: Long, onClick: () -> Unit) {
        this.text = text
        if (checkedId == id) {
            setBackgroundColor(context.getColor(R.color.item_changer_background_color))
        }
        setOnClickListener { onClick() }
    }
}

class ChangerViewHolder(val binding: ItemCountryGenreChangerBinding) : ViewHolder(binding.root)