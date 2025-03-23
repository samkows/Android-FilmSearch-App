package com.example.skillcinema.presentation.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.R
import com.example.skillcinema.databinding.ItemYearChangerBinding

//todo DONE
class YearChangerAdapter(
    private val onYearClick: (Int) -> Unit
) : RecyclerView.Adapter<YearChangerAdapter.YearViewHolder>() {

    private var years: List<Int> = emptyList()
    private var selectedYear: Int? = null

    fun setYears(newYears: List<Int>) {
        years = newYears
        notifyDataSetChanged()
    }

    fun setSelectedYear(year: Int?) {
        selectedYear = year
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YearViewHolder {
        return YearViewHolder(
            ItemYearChangerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: YearViewHolder, position: Int) {
        val year = years[position]
        holder.binding.root.apply {
            text = year.toString()
            if (year == selectedYear) {
                setBackgroundResource(R.drawable.item_year_changer_selected_backround)
                setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.white
                    )
                )
            } else {
                setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
                setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.title_text_color
                    )
                )
            }
            setOnClickListener { onYearClick(year) }
        }
    }

    override fun getItemCount(): Int = years.size

    class YearViewHolder(val binding: ItemYearChangerBinding) :
        RecyclerView.ViewHolder(binding.root)
}