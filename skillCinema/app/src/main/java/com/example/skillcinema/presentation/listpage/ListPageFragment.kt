package com.example.skillcinema.presentation.listpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.recyclerview.widget.GridLayoutManager
import com.example.skillcinema.App
import com.example.skillcinema.MainActivity
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentListpageBinding
import com.example.skillcinema.models.ShortFilmData
import com.example.skillcinema.presentation.OffsetItemDecoration
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

//todo DONE
class ListPageFragment : Fragment() {
    companion object {
        const val TYPE = "type"
        const val TITLE = "title"

        const val COUNTRY_ID = "country id"
        const val GENRE_ID = "genre id"
        const val FILM_ID = "film id"
        const val COLLECTION_ID = "collection id"

        const val PREMIERES = "premieres"
        const val POPULAR = "popular"
        const val TOP_250 = "top250"
        const val SERIALS = "serials"
        const val DYNAMIC_1 = "dynamic 1"
        const val DYNAMIC_2 = "dynamic 2"

        const val SIMILAR = "similar"

        const val FAVORITE = "favorite"
        const val WANT_TO_WATCH = "want to watch"
        const val WATCHED = "watched"
        const val WAS_INTERESTING = "was interesting"
        const val USER_COLLECTION = "user collection"
    }

    private val viewModel: ListPageViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = (activity?.application as App).repository
                return ListPageViewModel(repository) as T
            }
        }
    }

    private var _binding: FragmentListpageBinding? = null
    private val binding get() = _binding!!

    private val pagingFilmAdapter = ListPagePagingAdapter(::onItemClick)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListpageBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        readArgumentsAndLoadData()
        handleLoadingState()
    }

    private fun setupToolbar() {
        val toolbar = binding.toolbar
        toolbar.setupWithNavController(findNavController())
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        toolbar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.caret_left, null)
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
    }

    private fun setupRecyclerView() {
        with(binding.listPageRecycler) {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = pagingFilmAdapter
            addItemDecoration(
                OffsetItemDecoration(
                    spacingInPxRight = resources.getDimensionPixelSize(R.dimen.offsets_16dp),
                    spacingInPxBottom = resources.getDimensionPixelSize(R.dimen.offsets_16dp)
                )
            )
        }
    }

    private fun readArgumentsAndLoadData() {
        arguments?.let {
            binding.toolbar.title = when (it.getString(TYPE)) {
                PREMIERES -> resources.getString(R.string.premieres)
                POPULAR -> resources.getString(R.string.popular)
                TOP_250 -> resources.getString(R.string.top250)
                SERIALS -> resources.getString(R.string.serials)
                SIMILAR -> resources.getString(R.string.similar_films)
                FAVORITE -> resources.getString(R.string.favorite)
                WANT_TO_WATCH -> resources.getString(R.string.want_to_watch)
                WATCHED -> resources.getString(R.string.watched)
                WAS_INTERESTING -> resources.getString(R.string.you_were_interesting)
                else -> "${it.getString(TITLE)}"
            }
            when (it.getString(TYPE)) {
                DYNAMIC_1 -> viewModel.getData(
                    type = it.getString(TYPE)!!,
                    countryId = it.getLong(COUNTRY_ID),
                    genreId = it.getLong(GENRE_ID)
                )

                DYNAMIC_2 -> viewModel.getData(
                    type = it.getString(TYPE)!!,
                    countryId = it.getLong(COUNTRY_ID),
                    genreId = it.getLong(GENRE_ID)
                )

                SIMILAR -> viewModel.getData(
                    type = it.getString(TYPE)!!,
                    filmId = it.getLong(FILM_ID)
                )

                USER_COLLECTION -> viewModel.getCollectionData(
                    collectionType = it.getString(TYPE)!!,
                    collectionId = it.getLong(COLLECTION_ID)
                )

                WATCHED -> viewModel.getCollectionData(
                    collectionType = it.getString(TYPE)!!
                )

                FAVORITE -> viewModel.getCollectionData(
                    collectionType = it.getString(TYPE)!!
                )

                WANT_TO_WATCH -> viewModel.getCollectionData(
                    collectionType = it.getString(TYPE)!!
                )

                WAS_INTERESTING -> viewModel.getCollectionData(
                    collectionType = it.getString(TYPE)!!
                )

                else -> viewModel.getData(type = it.getString(TYPE)!!)
            }
        }
    }

    private fun handleLoadingState() {
        viewModel.isLoading.onEach {
            when (it) {
                ListPageLoadState.Loading -> {
                    (activity as MainActivity).showProgressIndicator()
                    binding.root.visibility = View.INVISIBLE
                }

                is ListPageLoadState.Error -> {
                    (activity as MainActivity).hideProgressIndicator()
                    (activity as MainActivity).showErrorBottomSheetFragment()

                    it.throwable?.let { e ->
                        Firebase.crashlytics.log("${this.javaClass.simpleName} : ${e.message}")
                        Firebase.crashlytics.recordException(e)

                        //todo delete toast
                        Toast.makeText(context, "${e.message}", Toast.LENGTH_LONG).show()
                    }
                }

                is ListPageLoadState.Success -> {
                    (activity as MainActivity).hideProgressIndicator()
                    binding.root.visibility = View.VISIBLE

                    it.data.onEach { pagingData ->
                        pagingFilmAdapter.submitData(pagingData)
                    }.launchIn(viewLifecycleOwner.lifecycleScope)
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun onItemClick(shortFilmData: ShortFilmData) {
        findNavController().navigate(
            R.id.action_listpageFragment_to_filmpageFragment,
            bundleOf("id" to shortFilmData.kinopoiskID)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}