package com.example.skillcinema.presentation.actor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.bumptech.glide.Glide
import com.example.skillcinema.App
import com.example.skillcinema.MainActivity
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentActorBinding
import com.example.skillcinema.models.ShortFilmData
import com.example.skillcinema.presentation.FilmAdapter
import com.example.skillcinema.presentation.OffsetItemDecoration
import com.example.skillcinema.presentation.galleryViewPager.GalleryViewPagerFragment
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ActorFragment : Fragment() {

    private val viewModel: ActorViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = (activity?.application as App).repository
                return ActorViewModel(repository) as T
            }
        }
    }

    private var _binding: FragmentActorBinding? = null
    private val binding get() = _binding!!

    private val theBestFilmsAdapter = FilmAdapter(::onTheBestFilmClick) {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        arguments?.let {
            viewModel.loadData(it.getLong("id"))
        }
        handleLoadingState()
    }

    private fun setupToolbar() {
        binding.actorToolbar.apply {
            setupWithNavController(findNavController())
            (activity as? AppCompatActivity)?.setSupportActionBar(this)
            (activity as? AppCompatActivity)?.supportActionBar?.setDisplayShowTitleEnabled(false)
            navigationIcon =
                ResourcesCompat.getDrawable(resources, R.drawable.caret_left, null)
            setNavigationOnClickListener { findNavController().navigateUp() }
        }
    }

    private fun handleLoadingState() {
        viewModel.isLoading.onEach {
            when (it) {
                is ActorLoadState.Loading -> {
                    (activity as MainActivity).showProgressIndicator()
                    binding.root.visibility = View.INVISIBLE
                }

                is ActorLoadState.Error -> {
                    (activity as MainActivity).hideProgressIndicator()
                    (activity as MainActivity).showErrorBottomSheetFragment()

                    it.throwable?.let { e ->
                        Firebase.crashlytics.log("${this.javaClass.simpleName} : ${e.message}")
                        Firebase.crashlytics.recordException(e)
                    }
                }

                is ActorLoadState.Success -> {
                    (activity as MainActivity).hideProgressIndicator()
                    binding.root.visibility = View.VISIBLE

                    setDataToViews(it)
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setDataToViews(data: ActorLoadState.Success) {
        with(binding) {
            Glide.with(this@ActorFragment)
                .load(data.personData.posterURL)
                .into(actorImage)

            actorImage.setOnClickListener {
                findNavController().navigate(
                    R.id.action_actorFragment_to_galleryViewPagerFragment,
                    bundleOf(
                        GalleryViewPagerFragment.TYPE to GalleryViewPagerFragment.PERSON_PHOTO
                    ).apply {
                        putString(
                            GalleryViewPagerFragment.PERSON_POSTER_URL,
                            data.personData.posterURL
                        )
                    }
                )
            }

            actorName.text = data.personData.nameRu
            actorSubtitle.text = data.personData.profession

            bestContainer.recyclerView.apply {
                addItemDecoration(
                    OffsetItemDecoration(
                        spacingInPxRight = resources.getDimensionPixelSize(
                            R.dimen.offsets_8dp
                        )
                    )
                )
                adapter = theBestFilmsAdapter.apply { setData(data.theBestFilms) }
            }

            filmographyContainer.apply {
                enableQuantityOfFilmsOrSeries()
                setQuantityOfFilmsOrSeries(
                    "${data.personData.films.size}  ${
                        resources.getQuantityString(
                            R.plurals.films_plurals,
                            data.personData.films.size
                        )
                    }"
                )
                quantityViewClicked = {
                    findNavController().navigate(
                        R.id.action_actorFragment_to_filmographyFragment,
                        bundleOf("id" to data.personData.personID)
                    )
                }
            }
        }
    }

    private fun onTheBestFilmClick(filmDataDto: ShortFilmData) {
        findNavController().navigate(
            R.id.action_actorFragment_to_filmpageFragment,
            bundleOf("id" to filmDataDto.kinopoiskID)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}