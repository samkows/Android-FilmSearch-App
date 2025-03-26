package com.example.skillcinema.presentation.seasons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentEpisodesBinding
import com.example.skillcinema.models.Season

class EpisodesFragment : Fragment() {

    companion object {
        const val SEASON_DATA = "data"
    }

    private var _binding: FragmentEpisodesBinding? = null
    private val binding get() = _binding!!

    private val episodesAdapter = EpisodesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEpisodesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.episodesRecycler.adapter = episodesAdapter
        arguments?.let {
            val data = it.getParcelable<Season>(SEASON_DATA)
            if (data != null) {
                episodesAdapter.setData(data.episodes)
                binding.seasonInfo.text =
                    "${data.number} сезон, ${data.episodes.size} ${
                        resources.getQuantityString(
                            R.plurals.episodes_plurals, data.episodes.size
                        )
                    }"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}