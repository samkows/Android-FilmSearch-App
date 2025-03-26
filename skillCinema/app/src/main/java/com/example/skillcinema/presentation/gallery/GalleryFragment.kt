package com.example.skillcinema.presentation.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.example.skillcinema.App
import com.example.skillcinema.MainActivity
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentGalleryBinding
import com.example.skillcinema.models.GalleryItem
import com.example.skillcinema.models.GalleryTypes
import com.example.skillcinema.presentation.galleryViewPager.GalleryViewPagerFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class GalleryFragment : Fragment() {

    private val viewModel: GalleryViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = (activity?.application as App).repository
                return GalleryViewModel(repository) as T
            }
        }
    }

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private lateinit var data: ArrayList<Pair<GalleryTypes, Flow<PagingData<GalleryItem>>>>
    private var filmId = 0L

    private val pagedListAdapter =
        GalleryPagedListAdapter { galleryItem -> onItemClick(galleryItem, data) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        arguments?.let {
            filmId = it.getLong("id")
            viewModel.loadData(filmId)
        }
        handleLoadingState()
    }

    private fun setupToolbar() {
        binding.galleryToolbar.apply {
            setupWithNavController(findNavController())
            (activity as? AppCompatActivity)?.setSupportActionBar(this)
            title = getString(R.string.gallery)
            navigationIcon =
                ResourcesCompat.getDrawable(resources, R.drawable.caret_left, null)
            setNavigationOnClickListener { findNavController().navigateUp() }
        }
    }

    private fun setupRecyclerView() {
        val gridLayoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        gridLayoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (pagedListAdapter.getItemViewType(position)) {
                    pagedListAdapter.ITEM_BIG_TYPE -> gridLayoutManager.spanCount
                    else -> 1
                }
            }
        }
        binding.galleryRecycler.apply {
            addItemDecoration(
                GallerySpacingItemDecoration(
                    resources.getDimensionPixelSize(R.dimen.offsets_16dp),
                )
            )
            layoutManager = gridLayoutManager
            adapter = pagedListAdapter
        }
    }

    private fun handleLoadingState() {
        viewModel.isLoading.onEach {
            when (it) {
                is GalleryLoadState.Loading -> {
                    (activity as MainActivity).showProgressIndicator()
                    binding.root.visibility = View.INVISIBLE
                }

                is GalleryLoadState.Error -> {
                    (activity as MainActivity).hideProgressIndicator()
                    (activity as MainActivity).showErrorBottomSheetFragment()

                    it.throwable?.let { e ->
                        Firebase.crashlytics.log("${this.javaClass.simpleName} : ${e.message}")
                        Firebase.crashlytics.recordException(e)
                    }
                }

                is GalleryLoadState.Success -> {
                    (activity as MainActivity).hideProgressIndicator()
                    binding.root.visibility = View.VISIBLE

                    data = it.galleryDataByTypes
                    setDataToTabsAndAdapter(data)
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setDataToTabsAndAdapter(data: ArrayList<Pair<GalleryTypes, Flow<PagingData<GalleryItem>>>>) {
        data.forEach {
            val tab = binding.galleryTabLayout.newTab().apply {
                setCustomView(R.layout.custom_tab)
                customView?.findViewById<TextView>(R.id.tabText)?.text =
                    it.first.getLocalizedName(requireContext())
                customView?.findViewById<TextView>(R.id.tabNumber)?.text =
                    it.first.quantity.toString()
            }
            binding.galleryTabLayout.addTab(tab)
        }

        val flowData = data[binding.galleryTabLayout.selectedTabPosition].second
        flowData.onEach {
            pagedListAdapter.submitData(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        binding.galleryTabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    val flowGalleryData = data[tab.position].second
                    flowGalleryData.onEach {
                        pagedListAdapter.submitData(it)
                    }.launchIn(viewLifecycleOwner.lifecycleScope)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun onItemClick(
        itemPosition: Int,
        data: ArrayList<Pair<GalleryTypes, Flow<PagingData<GalleryItem>>>>
    ) {
        val type = data[binding.galleryTabLayout.selectedTabPosition].first
        findNavController().navigate(
            R.id.action_galleryFragment_to_galleryViewPagerFragment,
            bundleOf(
                GalleryViewPagerFragment.TYPE to GalleryViewPagerFragment.PAGED_LIST,
                GalleryViewPagerFragment.FILM_ID to filmId,
                GalleryViewPagerFragment.GALLERY_TYPE to type.name,
                GalleryViewPagerFragment.ITEM_POSITION to itemPosition,
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.galleryTabLayout.clearOnTabSelectedListeners()
        _binding = null
    }
}