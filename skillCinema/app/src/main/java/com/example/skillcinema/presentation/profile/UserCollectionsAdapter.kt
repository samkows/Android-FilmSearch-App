package com.example.skillcinema.presentation.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.skillcinema.databinding.ItemCollectionBinding
import com.example.skillcinema.models.collections.UserCollection
import com.example.skillcinema.models.collections.UserCollectionWithFilms

class UserCollectionsAdapter(
    private val onCollectionClick: (UserCollectionWithFilms) -> Unit,
    private val onDeleteClick: (UserCollection) -> Unit
) : RecyclerView.Adapter<CollectionViewHolder>() {

    private var data: List<UserCollectionWithFilms> = emptyList()

    fun setData(data: List<UserCollectionWithFilms>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        return CollectionViewHolder(
            ItemCollectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val item = data.getOrNull(position)
        item?.let {
            holder.binding.apply {
                collectionName.text = it.userCollection.userCollectionName
                filmsQuantity.text = it.films.size.toString()
                root.setOnClickListener { onCollectionClick(item) }
                closeView.setOnClickListener { onDeleteClick(item.userCollection) }
            }
        }
    }
}

class CollectionViewHolder(val binding: ItemCollectionBinding) :
    RecyclerView.ViewHolder(binding.root)