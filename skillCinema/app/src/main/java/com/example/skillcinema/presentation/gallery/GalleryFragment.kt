package com.example.skillcinema.presentation.gallery

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

    lateinit var gridLayoutManager: GridLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val toolbar = binding.galleryToolbar
        toolbar.setupWithNavController(findNavController())
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.gallery)
        toolbar.navigationIcon = resources.getDrawable(R.drawable.caret_left)
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        // val
        gridLayoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        gridLayoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (pagedListAdapter.getItemViewType(position)) {
                    pagedListAdapter.ITEM_TYPE2 -> gridLayoutManager.spanCount
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
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            filmId = it.getLong("id")
            viewModel.loadData(filmId)
        }
        viewModel.isLoading.onEach {
            when (it) {
                is GalleryLoadState.Loading -> {
                    (activity as MainActivity).showProgressIndicator()
                    binding.root.visibility = View.INVISIBLE
                }

                is GalleryLoadState.Error -> {
                    (activity as MainActivity).hideProgressIndicator()
                    (activity as MainActivity).showErrorBottomSheetFragment()

                    Toast.makeText(context, "${it.message}", Toast.LENGTH_LONG).show()
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
            val tab = binding.galleryTabLayout.newTab()
            tab.text = it.first.name
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

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
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
        _binding = null
    }
}