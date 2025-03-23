package com.example.skillcinema.presentation.seasons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.skillcinema.App
import com.example.skillcinema.MainActivity
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentSeasonsBinding
import com.example.skillcinema.models.SeasonsData
import com.google.android.material.chip.Chip
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SeasonsFragment : Fragment() {
    companion object {
        const val SERIAL_TITLE = "title"
        const val SERIAL_ID = "id"
    }

    private val viewModel: SeasonsViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = (activity?.application as App).repository
                return SeasonsViewModel(repository) as T
            }
        }
    }
    private var _binding: FragmentSeasonsBinding? = null
    private val binding get() = _binding!!

    private val seasonsViewPagerAdapter by lazy { SeasonsViewPagerAdapter(this) }
    private val viewPager: ViewPager2 by lazy { binding.viewPager }

    private val chipsIds: MutableList<Int> = emptyList<Int>().toMutableList()

    private var checkedChip = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeasonsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val toolbar = binding.seasonsToolbar
        toolbar.setupWithNavController(findNavController())
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        toolbar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.caret_left, null)
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            viewModel.loadData(it.getLong(SERIAL_ID))
            binding.seasonsToolbar.title = it.getString(SERIAL_TITLE)
        }

        viewModel.isLoading.onEach {
            when (it) {
                is SeasonsLoadState.Loading -> {
                    (activity as MainActivity).showProgressIndicator()
                    binding.root.visibility = View.INVISIBLE
                }

                is SeasonsLoadState.Error -> {
                    (activity as MainActivity).hideProgressIndicator()
                    (activity as MainActivity).showErrorBottomSheetFragment()

                    Toast.makeText(context, "${it.message}", Toast.LENGTH_LONG).show()
                }

                is SeasonsLoadState.Success -> {
                    (activity as MainActivity).hideProgressIndicator()
                    binding.root.visibility = View.VISIBLE

                    seasonsViewPagerAdapter.setData(it.seasonsData)
                    viewPager.adapter = seasonsViewPagerAdapter
                    addChips(it.seasonsData)
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        binding.chipGroup.setOnCheckedStateChangeListener { group, _ ->
            if (group.checkedChipId == View.NO_ID) {
                group.check(checkedChip)
            } else {
                checkedChip = group.checkedChipId
                val checkedIndex = chipsIds.indexOf(checkedChip)
                viewPager.setCurrentItem(checkedIndex, true)
            }
        }
        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.chipGroup.check(chipsIds[viewPager.currentItem])
            }
        })
    }

    private fun addChips(data: SeasonsData) {
        data.seasons.forEach {
            val chip = LayoutInflater.from(context).inflate(R.layout.chip, null) as Chip
            chip.text = it.number.toString()
            chip.id = View.generateViewId()
            chipsIds.add(chip.id)
            binding.chipGroup.addView(chip)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class SeasonsViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private lateinit var data: SeasonsData

    fun setData(data: SeasonsData) {
        this.data = data
    }

    override fun getItemCount(): Int = data.seasons.size

    override fun createFragment(position: Int): Fragment {
        val fragment = EpisodesFragment()
        fragment.arguments = Bundle().apply {
            putParcelable(EpisodesFragment.SEASON_DATA, data.seasons[position])
        }
        return fragment
    }
}