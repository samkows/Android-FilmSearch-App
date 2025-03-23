package com.example.skillcinema.presentation.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.skillcinema.App
import com.example.skillcinema.MainViewModel
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentCountryGenreChangerBinding

//todo DONE
class CountryGenreChangerFragment : Fragment() {
    companion object {
        const val TYPE = "type"
        const val COUNTRY_CHANGER = "country"
        const val GENRE_CHANGER = "genre"
    }

    private val activityViewModel: MainViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val dataStore = (activity?.application as App).dataStore
                val repository = (activity?.application as App).repository
                return MainViewModel(dataStore, repository) as T
            }
        }
    }

    private var _binding: FragmentCountryGenreChangerBinding? = null
    private val binding get() = _binding!!

    private var changerType = ""
    private var currentSearchQuery = ""
    private val changerAdapter = CountryGenreChangerAdapter(::onItemClick)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCountryGenreChangerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()

        initializeChangerType()
        setupSearch()
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            setupWithNavController(findNavController())
            (activity as? AppCompatActivity)?.setSupportActionBar(this)
            (activity as? AppCompatActivity)?.supportActionBar?.setDisplayShowTitleEnabled(false)
            navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.caret_left, null)
            setNavigationOnClickListener { findNavController().navigateUp() }
        }
    }

    private fun setupRecyclerView() {
        binding.changerRecyclerView.apply {
            adapter = changerAdapter
            addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            )
        }
    }

    private fun initializeChangerType() {
        arguments?.getString(TYPE)?.let { type ->
            changerType = type
            binding.apply {
                toolbar.title = getText(
                    if (type == COUNTRY_CHANGER) R.string.country else R.string.genre
                )
                searchEditText.hint = getText(
                    if (type == COUNTRY_CHANGER) R.string.enter_country else R.string.enter_genre
                )
            }
            observeData()
        }
    }

    private fun setupSearch() {
        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            currentSearchQuery = text.toString().trim()
            observeData()
        }
    }

    private fun observeData() {
        activityViewModel.settingsUiState.observe(viewLifecycleOwner) { uiState ->
            if (changerType == COUNTRY_CHANGER) {
                val filteredItems = if (currentSearchQuery.isEmpty()) {
                    uiState.countries
                } else {
                    uiState.countries.filter {
                        it.country.contains(
                            currentSearchQuery,
                            ignoreCase = true
                        )
                    }
                }
                changerAdapter.setCountriesData(filteredItems, uiState.filters.countryId)
            } else {
                val filteredItems = if (currentSearchQuery.isEmpty()) {
                    uiState.genres
                } else {
                    uiState.genres.filter {
                        it.genre.contains(
                            currentSearchQuery,
                            ignoreCase = true
                        )
                    }
                }
                changerAdapter.setGenresData(filteredItems, uiState.filters.genreId)
            }
        }
    }

    private fun onItemClick(newId: Long) {
        when (changerType) {
            COUNTRY_CHANGER -> activityViewModel.updateCountryId(newId)
            GENRE_CHANGER -> activityViewModel.updateGenreId(newId)
        }
        findNavController().navigateUp()
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard(binding.searchEditText)
    }

    private fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}