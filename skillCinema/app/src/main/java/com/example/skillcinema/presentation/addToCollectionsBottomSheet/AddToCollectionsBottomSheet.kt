package com.example.skillcinema.presentation.addToCollectionsBottomSheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import com.bumptech.glide.Glide
import com.example.skillcinema.databinding.BottomSheetFragmentAddToCollectionsBinding
import com.example.skillcinema.models.FullFilmDataDto
import com.example.skillcinema.presentation.CreateCollectionDialog
import com.example.skillcinema.presentation.filmpage.FilmPageViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

//todo DONE
class AddToCollectionsBottomSheet(
    private val context: Context,
    private val viewModel: FilmPageViewModel
) : BottomSheetDialog(context) {

    private lateinit var binding: BottomSheetFragmentAddToCollectionsBinding
    private lateinit var filmData: FullFilmDataDto

    private lateinit var userCollectionsAdapter: AddToUserCollectionsAdapter
    private lateinit var appCollectionsAdapter: AddToAppCollectionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BottomSheetFragmentAddToCollectionsBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        viewModel.filmLiveData.observe(context as LifecycleOwner) {
            filmData = it
        }
        setupViews()
        setupRecyclerView()
        observeLiveData()
    }

    private fun setupViews() {
        binding.filmInfoContainer.apply {
            Glide.with(context)
                .load(filmData.posterURLPreview)
                .into(imageViewFilmographyItem)

            filmTitleTextView.text =
                filmData.nameRu ?: filmData.nameOriginal ?: filmData.nameEn ?: ""
            filmGenreTextView.text = filmData.genres.joinToString(", ") { it.genre.toString() }

            filmData.ratingKinopoisk?.let { rating ->
                filmRatingTextView.visibility = View.VISIBLE
                filmRatingTextView.text = rating.toString()
            } ?: run {
                filmRatingTextView.visibility = View.GONE
            }
        }

        binding.fragmentCloseIcon.setOnClickListener {
            dismiss()
        }
    }

    private fun setupRecyclerView() {
        userCollectionsAdapter = AddToUserCollectionsAdapter(
            filmData.kinopoiskID, ::addOrRemoveFromUserCollection
        )
        appCollectionsAdapter = AddToAppCollectionsAdapter(
            ::addOrRemoveFromAppCollection, ::createNewUserCollectionAlertDialog
        )
        val concatAdapter = ConcatAdapter(userCollectionsAdapter, appCollectionsAdapter)

        binding.collectionsRecyclerView.apply {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = concatAdapter
        }
    }

    private fun observeLiveData() {
        viewModel.userCollectionsWithFilms.observe(context as LifecycleOwner) {
            userCollectionsAdapter.submitList(it)
        }

        viewModel.isFavoriteFilmsLiveData.observe(context as LifecycleOwner) {
            appCollectionsAdapter.updateFavoriteItem(it.size, filmData.isFavorite)
        }

        viewModel.isWantToWatchFilmsLiveData.observe(context as LifecycleOwner) {
            appCollectionsAdapter.updateWantToWatchItem(it.size, filmData.isWantToWatch)
        }
    }

    private fun addOrRemoveFromUserCollection(isAddToCollection: Boolean, userCollectionId: Long) {
        if (isAddToCollection) viewModel.insertInUserCollection(
            userCollectionId, filmData.kinopoiskID
        )
        else viewModel.deleteFromUserCollection(userCollectionId, filmData.kinopoiskID)
    }

    private fun addOrRemoveFromAppCollection(collectionType: Int, isAddToCollection: Boolean) {
        when (collectionType) {
            AddToAppCollectionsAdapter.FAVORITE -> viewModel.favoriteClicked(
                filmData.kinopoiskID,
                isAddToCollection
            )

            AddToAppCollectionsAdapter.WANT_TO_WATCH -> viewModel.wantToWatchClicked(
                filmData.kinopoiskID,
                isAddToCollection
            )
        }
    }

    private fun createNewUserCollectionAlertDialog() {
        CreateCollectionDialog(context) { collectionName ->
            viewModel.createUserCollectionAndAddFilm(collectionName, filmData.kinopoiskID)
        }.show()
    }
}