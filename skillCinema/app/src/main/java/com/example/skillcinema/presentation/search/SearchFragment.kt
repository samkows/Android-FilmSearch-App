package com.example.skillcinema.presentation.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.skillcinema.App
import com.example.skillcinema.MainActivity
import com.example.skillcinema.MainViewModel
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentSearchBinding
import com.example.skillcinema.domain.SearchUseCase
import com.example.skillcinema.models.SearchParams
import com.example.skillcinema.models.ShortFilmData
import com.example.skillcinema.presentation.OffsetItemDecoration
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private val activityViewModel: MainViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val dataStore = (activity?.application as App).dataStore
                val repository = (activity?.application as App).repository
                return MainViewModel(dataStore, repository) as T
            }
        }
    }
    private val viewModel: SearchViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = (requireActivity().application as App).repository
                val searchUseCase = SearchUseCase(repository)
                return SearchViewModel(searchUseCase) as T
            }
        }
    }

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val pagingSearchAdapter = SearchPagingAdapter(::filmItemClick)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchFilters()
        setupSearchEditText()
        setupLoadState()
        setupSearchAdapter()

        binding.settingsImage.setOnClickListener {
            findNavController().navigate(
                R.id.action_navigation_search_to_settingsFragment
            )
        }
    }

    private fun setupRecyclerView() {
        binding.searchRecyclerView.apply {
            adapter = pagingSearchAdapter
            addItemDecoration(
                OffsetItemDecoration(
                    spacingInPxBottom = resources.getDimensionPixelSize(
                        R.dimen.offsets_8dp
                    )
                )
            )
        }
    }

    private fun setupSearchFilters() {
        viewLifecycleOwner.lifecycleScope.launch {
            activityViewModel.searchFilterFlow.collect { filters ->
                val params = SearchParams(
                    countryId = filters.countryId,
                    genreId = filters.genreId,
                    order = filters.order,
                    type = filters.type,
                    ratingFrom = filters.ratingFrom,
                    ratingTo = filters.ratingTo,
                    yearFrom = filters.yearFrom,
                    yearTo = filters.yearTo,
                    isWatched = filters.isWatched
                )
                viewModel.updateSearchParams(params)
            }
        }
    }

    private fun setupSearchEditText() {
        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            viewLifecycleOwner.lifecycleScope.launch {
                delay(300)
                val keyword = text.toString().trim()
                viewModel.updateKeyword(keyword)
            }
        }
    }

    private fun setupLoadState() {
        viewModel.isLoading.onEach { state ->
            when (state) {

                is SearchLoadState.Error -> {
                    (activity as MainActivity).hideProgressIndicator()
                    (activity as MainActivity).showErrorBottomSheetFragment()

                    state.throwable?.let { e ->
                        Firebase.crashlytics.log("${this.javaClass.simpleName} : ${e.message}")
                        Firebase.crashlytics.recordException(e)
                    }
                }

                SearchLoadState.Loading -> {
                    binding.errorTextView.visibility = View.GONE
                    binding.searchRecyclerView.visibility = View.GONE
                    (activity as MainActivity).showProgressIndicator()
                }

                is SearchLoadState.Success -> {
                    (activity as MainActivity).hideProgressIndicator()
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setupSearchAdapter() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pagingDataFlow.collectLatest { flow ->
                flow?.collectLatest { pagingData ->
                    pagingSearchAdapter.submitData(pagingData)
                }
            }
        }
        pagingSearchAdapter.addLoadStateListener { loadState ->
            when (loadState.refresh) {
                is LoadState.NotLoading -> {
                    if (pagingSearchAdapter.itemCount == 0
                        && viewModel.keyword.value.isNotEmpty()
                    ) {
                        binding.errorTextView.visibility = View.VISIBLE
                        binding.searchRecyclerView.visibility = View.GONE
                    } else {
                        binding.errorTextView.visibility = View.GONE
                        binding.searchRecyclerView.visibility = View.VISIBLE
                    }
                }

                is LoadState.Error -> {
                    (activity as MainActivity).hideProgressIndicator()
                    (activity as MainActivity).showErrorBottomSheetFragment()
                }

                LoadState.Loading -> {}
            }
        }
    }

    private fun filmItemClick(data: ShortFilmData) {
        findNavController().navigate(
            R.id.action_navigation_search_to_filmpageFragment,
            bundleOf("id" to data.kinopoiskID)
        )
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