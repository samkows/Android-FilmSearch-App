package com.example.skillcinema.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.example.skillcinema.App
import com.example.skillcinema.MainActivity
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentProfileBinding
import com.example.skillcinema.models.ShortFilmData
import com.example.skillcinema.models.collections.UserCollection
import com.example.skillcinema.presentation.CreateCollectionDialog
import com.example.skillcinema.presentation.FilmAdapter
import com.example.skillcinema.presentation.OffsetItemDecoration
import com.example.skillcinema.presentation.listpage.ListPageFragment
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

//todo DONE
class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = (activity?.application as App).repository
                return ProfileViewModel(repository) as T
            }
        }
    }

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val watchedAdapter = FilmAdapter(::onFilmItemClick, ::onShowAllWatched)

    private val wasInterestingAdapter =
        WasInterestingAdapter(::onFilmItemClick, ::onClearHistoryClick)

    private val userCollectionsAdapter = UserCollectionsAdapter(
        { data ->
            data.userCollection.userCollectionName?.let {
                onShowAllItemClick(
                    itemType = ListPageFragment.USER_COLLECTION,
                    title = it,
                    collectionId = data.userCollection.userCollectionId
                )
            }
        }, ::onDeleteCollectionClick
    )
    private val appCollectionsAdapter = AppCollectionsAdapter { type ->
        when (type) {
            AppCollectionsAdapter.FAVORITE -> onShowAllFavorite()
            else -> onShowAllWantToWatch()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()

        viewModel.isLoading.onEach {
            handleLoadingState(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        binding.createCollectionView.setOnClickListener {
            CreateCollectionDialog(requireContext(), ::createNewUserCollection)
                .show()
        }
    }

    private fun setupRecyclerViews() {
        setupWatchedRecyclerView()
        setupWasInterestingRecyclerView()
        setupUserCollectionsRecyclerView()
    }

    private fun setupWatchedRecyclerView() {
        binding.watchedContainer.recyclerView.apply {
            adapter = watchedAdapter
            addItemDecoration(
                OffsetItemDecoration(spacingInPxRight = resources.getDimensionPixelSize(R.dimen.offsets_8dp))
            )
        }
    }

    private fun setupWasInterestingRecyclerView() {
        binding.wasInterestingContainer.recyclerView.apply {
            adapter = wasInterestingAdapter
            addItemDecoration(
                OffsetItemDecoration(spacingInPxRight = resources.getDimensionPixelSize(R.dimen.offsets_8dp))
            )
        }
    }

    private fun setupUserCollectionsRecyclerView() {
        binding.collectionsRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            adapter = ConcatAdapter(appCollectionsAdapter, userCollectionsAdapter)
            addItemDecoration(
                CollectionsSpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.offsets_16dp))
            )
        }
    }

    private fun handleLoadingState(state: ProfileLoadState) {
        when (state) {
            ProfileLoadState.Loading -> {
                (activity as MainActivity).showProgressIndicator()
                binding.root.visibility = View.INVISIBLE
            }

            is ProfileLoadState.Error -> {
                (activity as MainActivity).hideProgressIndicator()
                (activity as MainActivity).showErrorBottomSheetFragment()

                state.throwable?.let { e ->
                    Firebase.crashlytics.log("${this.javaClass.simpleName} : ${e.message}")
                    Firebase.crashlytics.recordException(e)
                }
            }

            is ProfileLoadState.Success -> {
                (activity as MainActivity).hideProgressIndicator()
                binding.root.visibility = View.VISIBLE
                observeLiveData(state)
            }
        }
    }

    private fun observeLiveData(state: ProfileLoadState.Success) {
        state.isWatched.observe(viewLifecycleOwner) { data ->
            updateWatchedContainer(data)
        }

        state.isFavorite.observe(viewLifecycleOwner) { data ->
            appCollectionsAdapter.updateFavoriteItem(data.size.toString())
        }

        state.isWantToWatch.observe(viewLifecycleOwner) { data ->
            appCollectionsAdapter.updateWantToWatchItem(data.size.toString())
        }

        state.userCollections?.observe(viewLifecycleOwner) { data ->
            userCollectionsAdapter.setData(data)
        }

        state.isItWasInteresting.observe(viewLifecycleOwner) { data ->
            updateWasInterestingContainer(data)
        }
    }

    private fun updateWatchedContainer(data: List<ShortFilmData>) {
        binding.watchedContainer.apply {
            if (data.isNotEmpty()) {
                visibility = View.VISIBLE
                setQuantity(data.size.toString())
                quantityViewClicked = {
                    onShowAllItemClick(itemType = ListPageFragment.WATCHED)
                }
                watchedAdapter.setData(data)
            } else {
                visibility = View.GONE
            }
        }
    }

    private fun updateWasInterestingContainer(data: List<ShortFilmData>) {
        binding.wasInterestingContainer.apply {
            if (data.isNotEmpty()) {
                visibility = View.VISIBLE
                setQuantity(data.size.toString())
                quantityViewClicked = {
                    onShowAllItemClick(itemType = ListPageFragment.WAS_INTERESTING)
                }
                wasInterestingAdapter.setData(data)
            } else {
                visibility = View.GONE
            }
        }
    }

    private fun createNewUserCollection(collectionName: String) {
        viewModel.createUserCollection(collectionName)
    }

    private fun onClearHistoryClick() {
        viewModel.clearItWasInterestingHistory()
    }

    private fun onDeleteCollectionClick(userCollection: UserCollection) {
        viewModel.deleteUserCollection(userCollection)
    }

    private fun onShowAllItemClick(itemType: String, title: String = "", collectionId: Long = 0) {
        findNavController().navigate(
            R.id.action_navigation_profile_to_listpageFragment,
            bundleOf(
                ListPageFragment.TYPE to itemType,
                ListPageFragment.TITLE to title,
                ListPageFragment.COLLECTION_ID to collectionId
            )
        )
    }

    private fun onShowAllWatched() = onShowAllItemClick(ListPageFragment.WATCHED)
    private fun onShowAllFavorite() = onShowAllItemClick(ListPageFragment.FAVORITE)
    private fun onShowAllWantToWatch() = onShowAllItemClick(ListPageFragment.WANT_TO_WATCH)

    private fun onFilmItemClick(shortFilmData: ShortFilmData) {
        findNavController().navigate(
            R.id.action_navigation_profile_to_filmpageFragment,
            bundleOf("id" to shortFilmData.kinopoiskID)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}