package com.example.skillcinema.presentation.seasons

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.skillcinema.models.SeasonsData

//todo DONE
class SeasonsViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private lateinit var data: SeasonsData

    fun setData(data: SeasonsData) {
        this.data = data
    }

    override fun getItemCount(): Int = data.seasons.size

    override fun createFragment(position: Int): Fragment {
        val fragment = EpisodesFragment()
        fragment.arguments = Bundle().apply {
            putParcelable(EpisodesFragment.SEASON_DATA, data.seasons[position])
        }
        return fragment
    }
}