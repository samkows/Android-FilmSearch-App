package com.example.skillcinema.presentation.filmography

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
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
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

//todo DONE
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

    private val filmsAdapter = FilmographyFilmAdapter(::onItemClick)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilmographyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        arguments?.let {
            viewModel.loadData(it.getLong("id"))
        }
        handleLoadingState()
    }

    private fun setupToolbar() {
        binding.filmographyToolbar.apply {
            setupWithNavController(findNavController())
            (activity as? AppCompatActivity)?.setSupportActionBar(this)
            title = getString(R.string.filmography)
            navigationIcon =
                ResourcesCompat.getDrawable(resources, R.drawable.caret_left, null)
            setNavigationOnClickListener { findNavController().navigateUp() }
        }
    }

    private fun setupRecyclerView() {
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
    }

    private fun handleLoadingState() {
        viewModel.isLoading.onEach {
            when (it) {
                is FilmographyLoadState.Loading -> {
                    (activity as MainActivity).showProgressIndicator()
                    binding.root.visibility = View.INVISIBLE
                }

                is FilmographyLoadState.Error -> {
                    (activity as MainActivity).hideProgressIndicator()
                    (activity as MainActivity).showErrorBottomSheetFragment()

                    it.throwable?.let { e ->
                        Firebase.crashlytics.log("${this.javaClass.simpleName} : ${e.message}")
                        Firebase.crashlytics.recordException(e)

                        //todo delete toast
                        Toast.makeText(context, "${e.message}", Toast.LENGTH_LONG).show()
                    }
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
            val tab = binding.filmographyTabLayout.newTab().apply {
                setCustomView(R.layout.custom_tab)
                customView?.findViewById<TextView>(R.id.tabText)?.text =
                    it.getLocalizedName(requireContext())
                customView?.findViewById<TextView>(R.id.tabNumber)?.text = it.films.size.toString()
            }
            binding.filmographyTabLayout.addTab(tab)
        }
        filmsAdapter.setData(filmography[binding.filmographyTabLayout.selectedTabPosition].films)

        binding.filmographyTabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    filmsAdapter.setData(filmography[tab.position].films)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
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
        binding.filmographyTabLayout.clearOnTabSelectedListeners()
        _binding = null
    }
}