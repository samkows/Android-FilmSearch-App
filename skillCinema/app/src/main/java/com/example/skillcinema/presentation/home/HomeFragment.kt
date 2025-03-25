package com.example.skillcinema.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.skillcinema.App
import com.example.skillcinema.MainActivity
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentHomeBinding
import com.example.skillcinema.models.DynamicListIds
import com.example.skillcinema.models.HomeContainerItem
import com.example.skillcinema.models.ShortFilmData
import com.example.skillcinema.presentation.listpage.ListPageFragment
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

//todo DONE
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = (activity?.application as App).repository
                return HomeViewModel(repository) as T
            }
        }
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeAdapter = HomeAdapter(::onItemClick, ::onShowAllItemClick)

    private lateinit var firstDynamicListIds: DynamicListIds
    private lateinit var secondDynamicListIds: DynamicListIds

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadData()

        binding.recyclerView.apply {
            adapter = homeAdapter
            setItemViewCacheSize(7)
        }

        viewModel.isLoading.onEach {
            when (it) {
                HomeLoadState.Loading -> {
                    (activity as MainActivity).showProgressIndicator()
                    binding.root.visibility = View.INVISIBLE
                }

                is HomeLoadState.Error -> {
                    (activity as MainActivity).hideProgressIndicator()
                    (activity as MainActivity).showErrorBottomSheetFragment()

                    it.throwable?.let { e ->
                        Firebase.crashlytics.log("${this.javaClass.simpleName} : ${e.message}")
                        Firebase.crashlytics.recordException(e)

                        //todo delete toast
                        Toast.makeText(context, "${e.message}", Toast.LENGTH_LONG).show()
                    }
                }

                is HomeLoadState.Success -> {
                    (activity as MainActivity).hideProgressIndicator()
                    binding.root.visibility = View.VISIBLE

                    firstDynamicListIds = it.firstDynamicIds
                    secondDynamicListIds = it.secondDynamicIds

                    val items = listOf(
                        HomeContainerItem(
                            title = getString(R.string.premieres),
                            films = it.premieres.items,
                            type = ListPageFragment.PREMIERES
                        ),
                        HomeContainerItem(
                            title = getString(R.string.popular),
                            films = it.popular.items,
                            type = ListPageFragment.POPULAR
                        ),
                        HomeContainerItem(
                            title = getPluralGenreAndGenitiveCaseCountry(
                                it.firstDynamicIds.genreWithId.genre,
                                it.firstDynamicIds.countryWithId.country
                            ),
                            films = it.firstDynamic.items,
                            type = ListPageFragment.DYNAMIC_1
                        ),
                        HomeContainerItem(
                            title = getString(R.string.top250),
                            films = it.top250.items,
                            type = ListPageFragment.TOP_250
                        ),
                        HomeContainerItem(
                            title = getPluralGenreAndGenitiveCaseCountry(
                                it.secondDynamicIds.genreWithId.genre,
                                it.secondDynamicIds.countryWithId.country
                            ),
                            films = it.secondDynamic.items,
                            type = ListPageFragment.DYNAMIC_2
                        ),
                        HomeContainerItem(
                            title = getString(R.string.serials),
                            films = it.serials.items,
                            type = ListPageFragment.SERIALS
                        )
                    )
                    homeAdapter.updateData(items)
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }


    private fun getPluralGenreAndGenitiveCaseCountry(genre: String, country: String): String {
        val pluralGenre = when (genre.takeLast(2)) {
            "ер" -> genre + "ы"
            "ма" -> genre.dropLast(1) + "ы"
            "ив" -> genre + "ы"
            "рн" -> genre + "ы"
            else -> genre
        }.replaceFirstChar { it.uppercase() }

        val genitiveCaseCountry = when (country.takeLast(1)) {
            "я" -> country.dropLast(1) + "и"
            "г" -> country + "а"
            "а" -> {
                if (country.takeLast(2) == "ка") country.dropLast(1) + "и"
                else country.dropLast(1) + "ы"
            }

            else -> country
        }.replaceFirstChar { it.uppercase() }

        return "$pluralGenre $genitiveCaseCountry"
    }

    private fun onShowAllItemClick(itemType: String, title: String = "") {
        findNavController().navigate(
            R.id.action_navigation_home_to_listpageFragment,
            when (itemType) {
                ListPageFragment.DYNAMIC_1 ->
                    bundleOf(
                        ListPageFragment.TYPE to ListPageFragment.DYNAMIC_1,
                        ListPageFragment.TITLE to title,
                        ListPageFragment.COUNTRY_ID to firstDynamicListIds.countryWithId.id,
                        ListPageFragment.GENRE_ID to firstDynamicListIds.genreWithId.id
                    )

                ListPageFragment.DYNAMIC_2 ->
                    bundleOf(
                        ListPageFragment.TYPE to ListPageFragment.DYNAMIC_2,
                        ListPageFragment.TITLE to title,
                        ListPageFragment.COUNTRY_ID to secondDynamicListIds.countryWithId.id,
                        ListPageFragment.GENRE_ID to secondDynamicListIds.genreWithId.id
                    )

                else -> bundleOf(ListPageFragment.TYPE to itemType)
            }
        )
    }

    private fun onItemClick(shortFilmData: ShortFilmData) {
        findNavController().navigate(
            R.id.action_navigation_home_to_filmpageFragment,
            bundleOf("id" to shortFilmData.kinopoiskID)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}