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
import androidx.paging.PagingData
import com.example.skillcinema.App
import com.example.skillcinema.MainActivity
import com.example.skillcinema.databinding.FragmentGalleryViewPagerBinding
import com.example.skillcinema.models.GalleryItem
import com.example.skillcinema.models.GalleryTypes
import com.example.skillcinema.presentation.gallery.GalleryViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class GalleryViewPagerFragment : Fragment() {
    companion object {
        const val ITEM_POSITION = "position"
        const val ITEMS = "all items"

        const val TYPE = "type"
        const val PAGED_LIST = "paged list"
        const val NOT_PAGED_LIST = "not paged list"
        const val PERSON_PHOTO = "person photo"

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

    private lateinit var items: List<GalleryItem>
    private var itemPosition = 0
    private lateinit var flowItems: Flow<PagingData<GalleryItem>>

    private var _binding: FragmentGalleryViewPagerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        (activity as MainActivity).setRootPaddingToZero()
//        (activity as MainActivity).hideBottomNav()
        _binding = FragmentGalleryViewPagerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            if (it.getString(TYPE) == PERSON_PHOTO) {
                itemPosition = 0
                val photoUrl = it.getString(PERSON_POSTER_URL)!!
                val data = GalleryItem(imageURL = photoUrl, previewURL = "")
                items = listOf(data)

                val imageAdapter = ImageAdapter(items)
                binding.viewPager.apply {
                    adapter = imageAdapter
                    doOnAttach {
                        setCurrentItem(itemPosition, false)
                    }
                }
            } else if (it.getString(TYPE) == NOT_PAGED_LIST) {
                itemPosition = it.getInt(ITEM_POSITION)
                items = it.getParcelableArray(ITEMS)?.toList() as List<GalleryItem>

                val imageAdapter = ImageAdapter(items)
                binding.viewPager.apply {
                    adapter = imageAdapter
                    doOnAttach {
                        setCurrentItem(itemPosition, false)
                    }
                }

            } else {
                (activity as MainActivity).showProgressIndicator()
                binding.root.visibility = View.INVISIBLE

                itemPosition = it.getInt(ITEM_POSITION)
                val filmId = it.getLong(FILM_ID)
                val typeName = it.getString(GALLERY_TYPE)
                val type = typeName?.let { it1 -> GalleryTypes.valueOf(it1) }
                if (type != null) {
                    flowItems = viewModel.loadGalleryDataByOneType(filmId, type)
                }
                val pagedAdapter = PagedImageAdapter()
                binding.viewPager.adapter = pagedAdapter

                flowItems.onEach {
                    pagedAdapter.submitData(it)
                }.launchIn(viewLifecycleOwner.lifecycleScope)

                viewLifecycleOwner.lifecycleScope.launch {
                    delay(1000)
                    binding.viewPager.setCurrentItem(itemPosition, false)

                    (activity as MainActivity).hideProgressIndicator()
                    binding.root.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        (activity as MainActivity).setRootPaddingToWindowInsets()
//        (activity as MainActivity).showBottomNav()
        _binding = null
    }
}