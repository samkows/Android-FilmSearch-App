package com.example.skillcinema.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.skillcinema.CustomViewContainer
import com.example.skillcinema.R
import com.example.skillcinema.databinding.ItemHomeContainerBinding
import com.example.skillcinema.databinding.ItemHomeLogoBinding
import com.example.skillcinema.models.HomeContainerItem
import com.example.skillcinema.models.ShortFilmData
import com.example.skillcinema.models.ShortFilmDataListDto
import com.example.skillcinema.presentation.FilmAdapter
import com.example.skillcinema.presentation.OffsetItemDecoration
import com.example.skillcinema.presentation.listpage.ListPageFragment

class HomeAdapter(
    private val onItemClick: (ShortFilmData) -> Unit,
    private val onShowAllClick: (String, String) -> Unit
): RecyclerView.Adapter<ViewHolder>() {

    companion object {
         const val TYPE_LOGO = 0
         const val TYPE_CONTAINER = 1
    }

    private val items = mutableListOf<HomeContainerItem>()
    fun updateData(newItems: List<HomeContainerItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return when (viewType) {
            TYPE_LOGO -> {
                val binding = ItemHomeLogoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                LogoViewHolder(binding)
            }
            else -> {
                val binding = ItemHomeContainerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                if (binding.container.recyclerView.itemDecorationCount == 0) {
                    binding.container.recyclerView.addItemDecoration(
                        OffsetItemDecoration(
                            spacingInPxRight = binding.root.resources.getDimensionPixelSize(R.dimen.offsets_8dp)
                        )
                    )
                }
                binding.container.recyclerView.setHasFixedSize(true)
                HomeViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int = items.size + 1

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_LOGO
            else -> TYPE_CONTAINER
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(holder) {
            is LogoViewHolder -> {}
            is HomeViewHolder -> {
                val item = items[position - 1]
                holder.binding.container.apply {
                    setTitle(item.title)
                    recyclerView.apply {
                        adapter = FilmAdapter(onItemClick) {
                            onShowAllClick(item.type, item.title)
                        }.apply { setData(item.films) }
                      //  (adapter as FilmAdapter).setData(item.films)
                    }
                    quantityViewClicked = {
                        onShowAllClick(item.type, item.title)
                    }


                }
            }
        }


    }
}
class HomeViewHolder(val binding: ItemHomeContainerBinding): ViewHolder(binding.root)
class LogoViewHolder(binding: ItemHomeLogoBinding) :
    ViewHolder(binding.root)