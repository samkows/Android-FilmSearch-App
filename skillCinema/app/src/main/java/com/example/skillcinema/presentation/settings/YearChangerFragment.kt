package com.example.skillcinema.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.skillcinema.App
import com.example.skillcinema.MainViewModel
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentYearChangerBinding
import com.example.skillcinema.presentation.OffsetItemDecoration
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

//todo DONE
class YearChangerFragment : Fragment() {

    private val activityViewModel: MainViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val dataStore = (activity?.application as App).dataStore
                val repository = (activity?.application as App).repository
                return MainViewModel(dataStore, repository) as T
            }
        }
    }

    private var _binding: FragmentYearChangerBinding? = null
    private val binding get() = _binding!!

    private lateinit var yearsFromAdapter: YearChangerAdapter
    private lateinit var yearsToAdapter: YearChangerAdapter

    private var currentFromRangeStart = 1000
    private var currentToRangeStart = 3000


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentYearChangerBinding.inflate(inflater, container, false)

        binding.materialToolbar.apply {
            setupWithNavController(findNavController())
            (activity as? AppCompatActivity)?.setSupportActionBar(this)
            title = getText(R.string.period)
            navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.caret_left, null)
            setNavigationOnClickListener { findNavController().navigateUp() }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapters()
        setupRangeNavigation()

        activityViewModel.searchFilterFlow.onEach { filter ->
            currentFromRangeStart = (filter.yearFrom / 12) * 12
            currentToRangeStart = (filter.yearTo / 12) * 12

            updateYearsFromAdapter()
            updateYearsToAdapter()

            yearsFromAdapter.setSelectedYear(filter.yearFrom)
            yearsToAdapter.setSelectedYear(filter.yearTo)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        binding.chooseButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initAdapters() {
        yearsFromAdapter = YearChangerAdapter { selectedYear ->
            activityViewModel.updateYearFrom(selectedYear)
        }
        binding.yearsFromRv.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            addItemDecoration(
                OffsetItemDecoration(
                    spacingInPxRight = resources.getDimensionPixelSize(R.dimen.offsets_8dp),
                    spacingInPxBottom = resources.getDimensionPixelSize(R.dimen.offsets_8dp)
                )
            )
            adapter = yearsFromAdapter
        }

        yearsToAdapter = YearChangerAdapter { selectedYear ->
            activityViewModel.updateYearTo(selectedYear)
        }
        binding.yearsBeforeRv.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            addItemDecoration(
                OffsetItemDecoration(
                    spacingInPxRight = resources.getDimensionPixelSize(R.dimen.offsets_8dp),
                    spacingInPxBottom = resources.getDimensionPixelSize(R.dimen.offsets_8dp)
                )
            )
            adapter = yearsToAdapter
        }
    }

    private fun setupRangeNavigation() {
        binding.apply {
            // yearsFrom
            nextYearsButtonFrom.setOnClickListener {
                currentFromRangeStart += 12
                updateYearsFromAdapter()
            }
            previousYearsButtonFrom.setOnClickListener {
                currentFromRangeStart -= 12
                updateYearsFromAdapter()
            }

            // yearsTo
            nextYearsButtonBefore.setOnClickListener {
                currentToRangeStart += 12
                updateYearsToAdapter()
            }
            previousYearsButtonBefore.setOnClickListener {
                currentToRangeStart -= 12
                updateYearsToAdapter()
            }
        }
    }

    private fun updateYearsFromAdapter() {
        val years = (currentFromRangeStart until currentFromRangeStart + 12).toList()
        yearsFromAdapter.setYears(years)
        binding.yearsFromTv.text = "$currentFromRangeStart - ${currentFromRangeStart + 11}"
    }

    private fun updateYearsToAdapter() {
        val years = (currentToRangeStart until currentToRangeStart + 12).toList()
        yearsToAdapter.setYears(years)
        binding.yearsBeforeTv.text = "$currentToRangeStart - ${currentToRangeStart + 11}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}