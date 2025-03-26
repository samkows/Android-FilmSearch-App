package com.example.skillcinema.presentation.galleryViewPager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnAttach
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.example.skillcinema.App
import com.example.skillcinema.MainActivity
import com.example.skillcinema.databinding.FragmentGalleryViewPagerBinding
import com.example.skillcinema.models.GalleryItem
import com.example.skillcinema.models.GalleryTypes
import com.example.skillcinema.presentation.gallery.GalleryViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class GalleryViewPagerFragment : Fragment() {
    companion object {
        const val TYPE = "type"
        const val PAGED_LIST = "paged list"
        const val NOT_PAGED_LIST = "not paged list"
        const val PERSON_PHOTO = "person photo"

        const val ITEM_POSITION = "position"
        const val ITEMS = "all items"
        const val FILM_ID = "film id"
        const val GALLERY_TYPE = "gallery type"
        const val PERSON_POSTER_URL = "person poster url"
    }

    private val viewModel: GalleryViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = (activity?.application as App).repository
                return GalleryViewModel(repository) as T
            }
        }
    }

    private var _binding: FragmentGalleryViewPagerBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryViewPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { args ->
            when (args.getString(TYPE)) {
                PERSON_PHOTO -> setPersonPhoto(args.getString(PERSON_POSTER_URL)!!)

                NOT_PAGED_LIST -> {
                    val itemPosition = args.getInt(ITEM_POSITION)
                    val items = args.getParcelableArray(ITEMS)?.toList() as List<GalleryItem>
                    setupStaticGallery(items, itemPosition)
                }

                else -> {
                    val itemPosition = args.getInt(ITEM_POSITION)
                    val filmId = args.getLong(FILM_ID)
                    val typeName = args.getString(GALLERY_TYPE)
                    val type = typeName?.let { GalleryTypes.valueOf(it) }

                    if (type != null) setupPagedGallery(filmId, type, itemPosition)
                }
            }
        }
    }

    private fun setPersonPhoto(url: String) {
        val items = listOf(GalleryItem(imageURL = url, previewURL = ""))
        setupStaticGallery(items, 0)
    }

    private fun setupStaticGallery(items: List<GalleryItem>, position: Int) {
        val imageAdapter = ImageAdapter(items)
        binding.viewPager.apply {
            adapter = imageAdapter
            doOnAttach {
                setCurrentItem(position, false)
            }
        }
    }

    private fun setupPagedGallery(filmId: Long, type: GalleryTypes, position: Int) {
        (activity as MainActivity).showProgressIndicator()
        binding.root.visibility = View.INVISIBLE

        val flowItems = viewModel.loadGalleryDataByOneType(filmId, type)
        val pagedAdapter = PagedImageAdapter()
        binding.viewPager.adapter = pagedAdapter

        flowItems.onEach { pagedAdapter.submitData(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewLifecycleOwner.lifecycleScope.launch {
            pagedAdapter.loadStateFlow.collectLatest { loadState ->
                when (loadState.refresh) {
                    is LoadState.NotLoading -> {
                        binding.viewPager.setCurrentItem(position, false)
                        (activity as? MainActivity)?.hideProgressIndicator()
                        binding.root.visibility = View.VISIBLE
                    }

                    is LoadState.Error -> {}
                    LoadState.Loading -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}