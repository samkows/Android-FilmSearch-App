package com.example.skillcinema.presentation.staffListPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skillcinema.R
import com.example.skillcinema.databinding.FragmentListpageBinding
import com.example.skillcinema.models.StaffData
import com.example.skillcinema.presentation.OffsetItemDecoration

class StaffListPageFragment : Fragment() {
    companion object {
        const val STAFF_LIST = "staff list"
        const val TITLE = "title"
    }

    private var _binding: FragmentListpageBinding? = null
    private val binding get() = _binding!!

    private val staffListAdapter =
        StaffListPageAdapter { data -> onStaffItemClick(data) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListpageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val toolbar = binding.toolbar
        toolbar.setupWithNavController(findNavController())
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        toolbar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.caret_left, null)
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.listPageRecycler.apply {
            layoutParams.width = LayoutParams.MATCH_PARENT
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = staffListAdapter
            addItemDecoration(
                OffsetItemDecoration(
                    spacingInPxLeft = resources.getDimensionPixelSize(R.dimen.offsets_26dp),
                    spacingInPxRight = resources.getDimensionPixelSize(R.dimen.offsets_26dp),
                    spacingInPxBottom = resources.getDimensionPixelSize(R.dimen.offsets_16dp)
                )
            )
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            binding.toolbar.title = it.getString(TITLE)
            staffListAdapter.setData(
                (it.getParcelableArray(STAFF_LIST) as Array<StaffData>?)!!
            )
        }
    }

    private fun onStaffItemClick(data: StaffData) {
        findNavController().navigate(
            R.id.action_staffListPageFragment_to_actorFragment,
            bundleOf("id" to data.staffID)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}