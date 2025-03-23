package com.example.skillcinema.presentation.addToCollectionsBottomSheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.skillcinema.databinding.ItemAddToCollectionBinding
import com.example.skillcinema.models.collections.UserCollectionWithFilms

//todo DONE
class AddToUserCollectionsAdapter(
    private val filmId: Long,
    private val checkedChange: (Boolean, Long) -> Unit
) : ListAdapter<UserCollectionWithFilms, AddToCollectionsViewHolder>(
    AddToCollectionsDiffUtilCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddToCollectionsViewHolder {
        return AddToCollectionsViewHolder(
            ItemAddToCollectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AddToCollectionsViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            (holder.binding as ItemAddToCollectionBinding).apply {
                collectionNameText.text = it.userCollection.userCollectionName ?: ""
                collectionSizeText.text = it.films.size.toString()
                collectionCheckbox.isChecked = it.films.any { film -> film.kinopoiskID == filmId }

                collectionCheckbox.setOnClickListener {
                    checkedChange(
                        collectionCheckbox.isChecked,
                        item.userCollection.userCollectionId
                    )
                }
            }
        }
    }
}

class AddToCollectionsDiffUtilCallback : DiffUtil.ItemCallback<UserCollectionWithFilms>() {
    override fun areItemsTheSame(
        oldItem: UserCollectionWithFilms,
        newItem: UserCollectionWithFilms
    ): Boolean =
        oldItem.userCollection.userCollectionId == newItem.userCollection.userCollectionId

    override fun areContentsTheSame(
        oldItem: UserCollectionWithFilms,
        newItem: UserCollectionWithFilms
    ): Boolean = oldItem.films.size == newItem.films.size
}

class AddToCollectionsViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)