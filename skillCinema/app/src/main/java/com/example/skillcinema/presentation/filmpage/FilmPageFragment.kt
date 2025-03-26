package com.example.skillcinema.presentation.filmpage

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.skillcinema.App
import com.example.skillcinema.MainActivity
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentFilmpageBinding
import com.example.skillcinema.models.FullFilmDataDto
import com.example.skillcinema.models.GalleryData
import com.example.skillcinema.models.GalleryItem
import com.example.skillcinema.models.ShortFilmData
import com.example.skillcinema.models.ShortFilmDataListDto
import com.example.skillcinema.models.StaffData
import com.example.skillcinema.presentation.FilmAdapter
import com.example.skillcinema.presentation.OffsetItemDecoration
import com.example.skillcinema.presentation.addToCollectionsBottomSheet.AddToCollectionsBottomSheet
import com.example.skillcinema.presentation.galleryViewPager.GalleryViewPagerFragment
import com.example.skillcinema.presentation.listpage.ListPageFragment
import com.example.skillcinema.presentation.seasons.SeasonsFragment
import com.example.skillcinema.presentation.staffListPage.StaffListPageFragment
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

private const val ITEM_COUNT_FOR_ACTORS_ADAPTER = 20
private const val ITEM_COUNT_FOR_STAFF_ADAPTER = 6

class FilmPageFragment : Fragment() {

    private val viewModel: FilmPageViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = (activity?.application as App).repository
                return FilmPageViewModel(repository) as T
            }
        }
    }

    private var _binding: FragmentFilmpageBinding? = null
    private val binding get() = _binding!!

    private val actorsAdapter = StaffAdapter(::onStaffItemClick, ITEM_COUNT_FOR_ACTORS_ADAPTER)
    private val staffAdapter = StaffAdapter(::onStaffItemClick, ITEM_COUNT_FOR_STAFF_ADAPTER)
    private val galleryAdapter = GalleryAdapter(::onGalleryItemClick)
    private val similarFilmsAdapter = FilmAdapter(::onSimilarFilmClick, ::onShowAllItemClick)

    private lateinit var filmData: FullFilmDataDto
    private var filmId = 0L

    private var isFavorite = false
    private var isWantToWatch = false
    private var isWatched = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilmpageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        arguments?.let {
            viewModel.loadData((it.getLong("id")))
        }
        handleLoadingState()
        setupClickListeners()
    }

    private fun setupToolbar() {
        binding.apply {
            filmToolbar.setupWithNavController(findNavController())
            (activity as? AppCompatActivity)?.setSupportActionBar(filmToolbar)
            (activity as? AppCompatActivity)?.supportActionBar?.setDisplayShowTitleEnabled(false)
            filmToolbar.navigationIcon =
                ResourcesCompat.getDrawable(resources, R.drawable.caret_left, null)
            filmToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.filmToolbar.updateLayoutParams<MarginLayoutParams> {
                topMargin = systemBars.top
            }
            insets
        }
    }

    private fun handleLoadingState() {
        viewModel.isLoading.onEach {
            when (it) {
                is FilmPageLoadState.Loading -> {
                    (activity as MainActivity).showProgressIndicator()
                    binding.root.visibility = View.INVISIBLE
                }

                is FilmPageLoadState.Error -> {
                    (activity as MainActivity).hideProgressIndicator()
                    (activity as MainActivity).showErrorBottomSheetFragment()

                    it.throwable?.let { e ->
                        Firebase.crashlytics.log("${this.javaClass.simpleName} : ${e.message}")
                        Firebase.crashlytics.recordException(e)
                    }
                }

                is FilmPageLoadState.Success -> {
                    (activity as MainActivity).hideProgressIndicator()
                    binding.root.visibility = View.VISIBLE

                    observeLiveData()
                    setDataToViews(it)
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeLiveData() {
        viewModel.filmLiveData.observe(viewLifecycleOwner) {
            filmData = it
            filmId = filmData.kinopoiskID

            isWatched = filmData.isWatched
            setImageTint(binding.watchedImage, isWatched)
            if (isWatched) binding.watchedImage.setImageResource(R.drawable.ic_watched)
            else binding.watchedImage.setImageResource(R.drawable.ic_not_watched)

            isFavorite = filmData.isFavorite
            setImageTint(binding.favoriteImage, isFavorite)

            isWantToWatch = filmData.isWantToWatch
            setImageTint(binding.wantToWatchImage, isWantToWatch)
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            favoriteImage.setOnClickListener {
                viewModel.favoriteClicked(filmId, !isFavorite)
            }
            wantToWatchImage.setOnClickListener {
                viewModel.wantToWatchClicked(filmId, !isWantToWatch)
            }
            watchedImage.setOnClickListener {
                viewModel.watchedClicked(filmId, !isWatched)
            }
            shareImage.setOnClickListener {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "${filmData.webURL}")
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
            otherActionImage.setOnClickListener {
                showAddToCollectionsBottomSheetFragment()
            }
        }
    }

    private fun showAddToCollectionsBottomSheetFragment() {
        val bottomSheet = AddToCollectionsBottomSheet(requireContext(), viewModel)
        bottomSheet.show()
    }

    private fun setImageTint(imageView: ImageView, isActive: Boolean) {
        when (isActive) {
            true -> imageView.imageTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )

            false -> imageView.imageTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.image_background_color
                    )
                )
        }
    }

    private fun onStaffItemClick(item: StaffData) {
        findNavController().navigate(
            R.id.action_filmpageFragment_to_actorFragment,
            bundleOf("id" to item.staffID)
        )
    }

    private fun onGalleryItemClick(itemPosition: Int, galleryItems: List<GalleryItem>) {
        findNavController().navigate(
            R.id.action_filmpageFragment_to_galleryViewPagerFragment,
            bundleOf(
                GalleryViewPagerFragment.ITEM_POSITION to itemPosition,
                GalleryViewPagerFragment.TYPE to GalleryViewPagerFragment.NOT_PAGED_LIST,
                GalleryViewPagerFragment.ITEMS to galleryItems.toTypedArray()
            )
        )
    }

    private fun onSimilarFilmClick(filmDataDto: ShortFilmData) {
        findNavController().navigate(
            R.id.action_filmpageFragment_self,
            bundleOf("id" to filmDataDto.kinopoiskID)
        )
    }

    private fun onShowAllItemClick() {
        findNavController().navigate(
            R.id.action_filmpageFragment_to_listpageFragment,
            bundleOf(
                ListPageFragment.TYPE to ListPageFragment.SIMILAR,
                ListPageFragment.FILM_ID to filmId
            )
        )
    }

    private fun setDataToViews(data: FilmPageLoadState.Success) {
        setCoverAndLogo(data.filmData)
        setRatingAndName(data.filmData)
        setYearGenreAndSeasons(data)
        setCountriesLengthAndAgeLimit(data.filmData)
        setDescription(data.filmData)
        setSerialInfo(data)
        setActors(data.actors)
        setStaff(data.staffs)
        setGallery(data.galleryData)
        setSimilarFilms(data.similarFilms)
    }

    private fun setCoverAndLogo(data: FullFilmDataDto) {
        binding.apply {
            Glide.with(this@FilmPageFragment)
                .load(data.coverURL)
                .into(coverImageView)
            Glide.with(this@FilmPageFragment)
                .load(data.logoURL)
                .into(logoImageView)

        }
    }

    private fun setRatingAndName(data: FullFilmDataDto) {
        val rating = data.ratingKinopoisk?.toString() ?: ""
        val name = data.logoURL?.takeIf { it.isNotEmpty() }?.let {
            data.nameOriginal ?: data.nameEn ?: data.nameRu ?: ""
        } ?: data.nameRu ?: data.nameOriginal ?: data.nameEn ?: ""

        binding.ratingAndName.text = "$rating $name".trim()
    }

    private fun setYearGenreAndSeasons(data: FilmPageLoadState.Success) {
        val year = if (data.filmData.year != null) "${data.filmData.year}" else ""
        val genres = data.filmData.genres.joinToString(", ") { it.genre.toString() }
        val seasons = if (data.filmData.serial == true) "${data.seasonsQuantity} ${
            resources.getQuantityString(
                R.plurals.seasons_plurals,
                data.seasonsQuantity.toInt()
            )
        }" else ""
        val parts = listOf(year, genres, seasons).filter { it.isNotEmpty() }
        binding.yearAndGenre.text = parts.joinToString(", ")
    }

    private fun setCountriesLengthAndAgeLimit(data: FullFilmDataDto) {
        val country = data.countries.firstOrNull()?.country ?: ""
        val filmLength = data.filmLength?.let { filmLengthConvert(it) } ?: ""
        val ratingAgeLimits = data.ratingAgeLimits?.removePrefix("age")?.let { "$it+" } ?: ""
        val parts = listOf(country, filmLength, ratingAgeLimits).filter { it.isNotEmpty() }
        binding.countriesLengthAndAgeLimit.text = parts.joinToString(", ")
    }

    private fun filmLengthConvert(longLength: Long): String {
        val hours = longLength / 60
        val minutes = longLength - hours * 60
        return "$hours ч $minutes мин"
    }

    private fun setDescription(data: FullFilmDataDto) {
        binding.apply {
            shortDescription.apply {
                text = data.shortDescription
                visibility = if (data.shortDescription.isNullOrBlank()) View.GONE else View.VISIBLE
            }

            description.apply {
                text = data.description
                visibility = if (data.description.isNullOrBlank()) View.GONE else View.VISIBLE

                setOnClickListener {
                    description.maxLines = if (description.maxLines == 5) Int.MAX_VALUE else 5
                }
            }
        }
    }

    private fun setSerialInfo(data: FilmPageLoadState.Success) {
        if (data.filmData.serial == true) {
            binding.serialContainer.apply {
                visibility = View.VISIBLE
                enableQuantityOfFilmsOrSeries()
                setQuantityOfFilmsOrSeries(
                    "${data.seasonsQuantity} ${
                        resources.getQuantityString(
                            R.plurals.seasons_plurals,
                            data.seasonsQuantity.toInt()
                        )
                    }, ${data.episodesQuantity} ${
                        resources.getQuantityString(
                            R.plurals.episodes_plurals,
                            data.episodesQuantity.toInt()
                        )
                    }"
                )

                quantityViewClicked = {
                    findNavController().navigate(
                        R.id.action_filmpageFragment_to_seasonsFragment,
                        bundleOf(
                            SeasonsFragment.SERIAL_ID to data.filmData.kinopoiskID,
                            SeasonsFragment.SERIAL_TITLE to data.filmData.nameRu
                        )
                    )
                }
            }
        }
    }

    private fun setActors(data: List<StaffData>) {
        if (data.isNotEmpty()) {
            binding.actorsContainer.apply {
                visibility = View.VISIBLE
                setQuantity(data.size.toString())

                quantityViewClicked = {
                    findNavController().navigate(
                        R.id.action_filmpageFragment_to_staffListPageFragment,
                        bundleOf(
                            StaffListPageFragment.TITLE to resources.getString(R.string.the_film_stars),
                        ).apply {
                            putParcelableArray(
                                StaffListPageFragment.STAFF_LIST,
                                data.toTypedArray()
                            )
                        }
                    )
                }

                recyclerView.apply {
                    val spanCount = when (data.size) {
                        1 -> 1
                        2 -> 2
                        3 -> 3
                        else -> 4
                    }
                    layoutManager = GridLayoutManager(
                        requireContext(),
                        spanCount, GridLayoutManager.HORIZONTAL, false
                    )
                    addItemDecoration(
                        OffsetItemDecoration(
                            spacingInPxRight = resources.getDimensionPixelSize(R.dimen.offsets_8dp),
                            spacingInPxBottom = resources.getDimensionPixelSize(R.dimen.offsets_8dp)
                        )
                    )
                    adapter = actorsAdapter
                }
            }
            actorsAdapter.setData(data)
        }
    }

    private fun setStaff(data: List<StaffData>) {
        if (data.isNotEmpty()) {
            binding.staffContainer.apply {
                visibility = View.VISIBLE
                setQuantity(data.size.toString())

                quantityViewClicked = {
                    findNavController().navigate(
                        R.id.action_filmpageFragment_to_staffListPageFragment,
                        bundleOf(
                            StaffListPageFragment.TITLE to resources.getString(R.string.worked_on_the_film),
                        ).apply {
                            putParcelableArray(
                                StaffListPageFragment.STAFF_LIST,
                                data.toTypedArray()
                            )
                        }
                    )
                }

                recyclerView.apply {
                    val spanCount = when (data.size) {
                        1 -> 1
                        else -> 2
                    }
                    layoutManager = GridLayoutManager(
                        requireContext(),
                        spanCount, GridLayoutManager.HORIZONTAL, false
                    )
                    addItemDecoration(
                        OffsetItemDecoration(
                            spacingInPxRight = resources.getDimensionPixelSize(R.dimen.offsets_8dp),
                            spacingInPxBottom = resources.getDimensionPixelSize(R.dimen.offsets_8dp)
                        )
                    )
                    adapter = staffAdapter
                }
            }
            staffAdapter.setData(data)
        }
    }

    private fun setGallery(data: GalleryData) {
        if (data.items.isNotEmpty()) {
            binding.galleryContainer.apply {
                visibility = View.VISIBLE
                setQuantity(data.total.toString())

                quantityViewClicked = {
                    findNavController().navigate(
                        R.id.action_filmpageFragment_to_galleryFragment,
                        bundleOf("id" to filmId)
                    )
                }

                recyclerView.apply {
                    addItemDecoration(
                        OffsetItemDecoration(
                            spacingInPxRight = resources.getDimensionPixelSize(
                                R.dimen.offsets_8dp
                            )
                        )
                    )
                    adapter = galleryAdapter
                }
            }
            galleryAdapter.setData(data)
        }
    }

    private fun setSimilarFilms(data: ShortFilmDataListDto) {
        if (data.items.isNotEmpty()) {
            binding.similarFilmsContainer.apply {
                visibility = View.VISIBLE
                setQuantity(data.total.toString())

                quantityViewClicked = ::onShowAllItemClick

                recyclerView.apply {
                    addItemDecoration(
                        OffsetItemDecoration(
                            spacingInPxRight = resources.getDimensionPixelSize(
                                R.dimen.offsets_8dp
                            )
                        )
                    )
                    adapter = similarFilmsAdapter
                }
            }
            similarFilmsAdapter.setData(data)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}