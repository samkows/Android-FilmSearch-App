package com.example.skillcinema.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.skillcinema.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            binding.onboardingTextView.text = it.getString(ARG_DESCRIPTION)
            binding.onboardingImageView.setImageResource(it.getInt(ARG_IMAGE_RES))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_IMAGE_RES = "imageRes"

        fun newInstance(description: String?, imageResource: Int): OnboardingFragment {
            val fragment = OnboardingFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_DESCRIPTION, description)
                putInt(ARG_IMAGE_RES, imageResource)
            }
            return fragment
        }
    }
}