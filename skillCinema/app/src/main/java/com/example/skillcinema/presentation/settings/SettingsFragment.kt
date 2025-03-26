package com.example.skillcinema.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.skillcinema.App
import com.example.skillcinema.MainActivity
import com.example.skillcinema.MainViewModel
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentSettingsBinding
import com.example.skillcinema.models.CountryWithId
import com.example.skillcinema.models.GenreWithId
import com.example.skillcinema.models.SearchParams
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private val activityViewModel: MainViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val dataStore = (activity?.application as App).dataStore
                val repository = (activity?.application as App).repository
                return MainViewModel(dataStore, repository) as T
            }
        }
    }

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private var filterJob: Job? = null
    private var sortJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupClickListeners()
        setupRadioGroups()
        setupRangeSlider()
        handleLoadingState()

        activityViewModel.prepareCountriesAndGenres()
        activityViewModel.settingsUiState.observe(viewLifecycleOwner) { uiState ->
            setDataToViews(uiState.filters)
            updateCountryAndGenreViews(uiState.countries, uiState.genres, uiState.filters)
        }
    }

    private fun setupToolbar() {
        binding.materialToolbar.apply {
            setupWithNavController(findNavController())
            (activity as? AppCompatActivity)?.setSupportActionBar(this)
            title = getText(R.string.search_settings)
            navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.caret_left, null)
            setNavigationOnClickListener { findNavController().navigateUp() }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            countryValueTextView.setOnClickListener {
                navigateToCountryGenreChanger(CountryGenreChangerFragment.COUNTRY_CHANGER)
            }
            genreValueTextView.setOnClickListener {
                navigateToCountryGenreChanger(CountryGenreChangerFragment.GENRE_CHANGER)
            }
            yearValueTextView.setOnClickListener {
                findNavController().navigate(R.id.action_settingsFragment_to_yearChangerFragment)
            }

            watchedChangerView.setOnClickListener {
                val currentFilters = activityViewModel.settingsUiState.value?.filters
                if (currentFilters != null) {
                    activityViewModel.updateIsWatched(!currentFilters.isWatched)
                }
            }
        }
    }

    private fun handleLoadingState() {
        activityViewModel.isLoading.onEach {
            when (it) {
                is SettingsLoadState.Loading -> {
                    (activity as MainActivity).showProgressIndicator()
                    binding.root.visibility = View.INVISIBLE
                }

                is SettingsLoadState.Error -> {
                    (activity as MainActivity).hideProgressIndicator()
                    (activity as MainActivity).showErrorBottomSheetFragment()

                    it.throwable?.let { e ->
                        Firebase.crashlytics.log("${this.javaClass.simpleName} : ${e.message}")
                        Firebase.crashlytics.recordException(e)
                    }
                }

                is SettingsLoadState.Success -> {
                    (activity as MainActivity).hideProgressIndicator()
                    binding.root.visibility = View.VISIBLE
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun navigateToCountryGenreChanger(type: String) {
        findNavController().navigate(
            R.id.action_settingsFragment_to_countryGenreChangerFragment,
            bundleOf(CountryGenreChangerFragment.TYPE to type)
        )
    }

    private fun setupRadioGroups() {
        binding.apply {
            filterRadioGroup.isSelectionRequired = true
            sortRadioGroup.isSelectionRequired = true

            filterRadioGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    filterJob?.cancel()
                    filterJob = viewLifecycleOwner.lifecycleScope.launch {
                        delay(300)
                        activityViewModel.updateType(
                            when (checkedId) {
                                R.id.filter_button_1 -> "ALL"
                                R.id.filter_button_2 -> "FILM"
                                else -> "TV_SERIES"
                            }
                        )
                    }
                }
            }

            sortRadioGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    sortJob?.cancel()
                    sortJob = viewLifecycleOwner.lifecycleScope.launch {
                        delay(300)
                        activityViewModel.updateOrder(
                            when (checkedId) {
                                R.id.sort_button_1 -> "YEAR"
                                R.id.sort_button_2 -> "NUM_VOTE"
                                else -> "RATING"
                            }
                        )
                    }
                }
            }
        }
    }

    private fun setupRangeSlider() {
        binding.ratingSlider.addOnChangeListener { slider, _, fromUser ->
            if (fromUser) {
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(300)
                    val values = slider.values
                    activityViewModel.updateRatingFrom(values.first().toInt())
                    activityViewModel.updateRatingTo(values.last().toInt())
                }
            }
        }
    }

    private fun updateCountryAndGenreViews(
        countries: List<CountryWithId>,
        genres: List<GenreWithId>,
        filters: SearchParams
    ) {
        binding.countryValueTextView.text = countries.find { it.id == filters.countryId }?.country
        binding.genreValueTextView.text =
            genres.find { it.id == filters.genreId }?.genre?.replaceFirstChar { it.uppercase() }
    }

    private fun setDataToViews(filters: SearchParams) {
        binding.apply {
            filterRadioGroup.check(
                when (filters.type) {
                    "ALL" -> R.id.filter_button_1
                    "FILM" -> R.id.filter_button_2
                    else -> R.id.filter_button_3
                }
            )
            sortRadioGroup.check(
                when (filters.order) {
                    "YEAR" -> R.id.sort_button_1
                    "NUM_VOTE" -> R.id.sort_button_2
                    else -> R.id.sort_button_3
                }
            )

            ratingSlider.values = listOf(filters.ratingFrom.toFloat(), filters.ratingTo.toFloat())
            ratingValueTextView.text = if (filters.ratingFrom != 0 || filters.ratingTo != 10) {
                "от ${filters.ratingFrom} до ${filters.ratingTo}"
            } else getText(R.string.any)

            yearValueTextView.text = "с ${filters.yearFrom} до ${filters.yearTo}"

            watchedChangerView.apply {
                if (filters.isWatched) {
                    text = getText(R.string.watched)
                    setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_watched_settings, 0, 0, 0
                    )
                } else {
                    text = getText(R.string.not_watched)
                    setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_not_watched_settings, 0, 0, 0
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.filterRadioGroup.clearOnButtonCheckedListeners()
        binding.sortRadioGroup.clearOnButtonCheckedListeners()
        binding.ratingSlider.clearOnChangeListeners()
        filterJob?.cancel()
        sortJob?.cancel()
        _binding = null
    }
}