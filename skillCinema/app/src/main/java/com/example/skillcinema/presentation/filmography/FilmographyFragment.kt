package com.example.skillcinema.presentation.filmography

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.skillcinema.App
import com.example.skillcinema.MainActivity
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentFilmographyBinding
import com.example.skillcinema.models.ProfessionKeys
import com.example.skillcinema.models.ShortFilmData
import com.example.skillcinema.presentation.OffsetItemDecoration
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FilmographyFragment : Fragment() {

    private val viewModel: FilmographyViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = (activity?.application as App).repository
                return FilmographyViewModel(repository) as T
            }
        }
    }
    private var _binding: FragmentFilmographyBinding? = null
    private val binding get() = _binding!!

    private val filmsAdapter = FilmographyFilmAdapter { filmDataDto -> onItemClick(filmDataDto) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilmographyBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val toolbar = binding.filmographyToolbar
        toolbar.setupWithNavController(findNavController())
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        toolbar.navigationIcon = resources.getDrawable(R.drawable.caret_left)
        toolbar.title = getString(R.string.filmography)
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.filmographyRecycler.apply {
            addItemDecoration(
                OffsetItemDecoration(
                    spacingInPxBottom = resources.getDimensionPixelSize(
                        R.dimen.offsets_8dp
                    )
                )
            )
            adapter = filmsAdapter
        }

//        val tab = binding.filmographyTabLayout.newTab()
//        tab.text = "from fragment"
//        val badge = tab.orCreateBadge
//        badge.number = 33
//        badge.horizontalOffset = -20
//        badge.verticalOffset = 35
//        binding.filmographyTabLayout.addTab(tab)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            viewModel.loadData(it.getLong("id"))
//            val data = it.getParcelable<PersonData>("data")
//            if (data != null) {
//                //episodesAdapter.setData(data.episodes)
//                binding.nameTextView.text = data.nameRu
//            }
        }
        viewModel.isLoading.onEach {
            when (it) {
                is FilmographyLoadState.Loading -> {
                    (activity as MainActivity).showProgressIndicator()
                    binding.root.visibility = View.INVISIBLE
                }

                is FilmographyLoadState.Error -> {
                    (activity as MainActivity).hideProgressIndicator()
                    (activity as MainActivity).showErrorBottomSheetFragment()

                    Toast.makeText(context, "${it.message}", Toast.LENGTH_LONG).show()
                }

                is FilmographyLoadState.Success -> {
                    (activity as MainActivity).hideProgressIndicator()
                    binding.root.visibility = View.VISIBLE

                    binding.nameTextView.text = it.personData.nameRu
                    setDataToTabsAndAdapter(it.filmography)
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setDataToTabsAndAdapter(filmography: ArrayList<ProfessionKeys>) {
        filmography.forEach {
            val tab = binding.filmographyTabLayout.newTab()
            tab.text = it.name
            binding.filmographyTabLayout.addTab(tab)
        }
        filmsAdapter.setData(filmography[binding.filmographyTabLayout.selectedTabPosition].films)

        binding.filmographyTabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    filmsAdapter.setData(filmography[tab.position].films)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun onItemClick(filmDataDto: ShortFilmData) {
        findNavController().navigate(
            R.id.action_filmographyFragment_to_filmpageFragment,
            bundleOf("id" to filmDataDto.kinopoiskID)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        ProfessionKeys.entries.forEach {
            it.films.removeAll(it.films)
        }
    }
}