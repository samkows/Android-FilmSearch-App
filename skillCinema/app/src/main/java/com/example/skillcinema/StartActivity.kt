package com.example.skillcinema

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.skillcinema.databinding.ActivityStartBinding
import com.example.skillcinema.presentation.onboarding.OnboardingFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            //show loader
            delay(1000)

            if (!isOnBoardingFinished()) {
                binding.apply {
                    startImageView.visibility = View.GONE
                    circularProgressIndicator.visibility = View.GONE

                    viewPager.visibility = View.VISIBLE
                    tabLayout.visibility = View.VISIBLE
                    skipTextView.visibility = View.VISIBLE

                    viewPager.adapter = ViewPagerAdapter(this@StartActivity)
                    TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

                    skipTextView.setOnClickListener {
                        onBoardingFinished()
                    }
                }
            } else {
                startActivity(Intent(this@StartActivity, MainActivity::class.java))
            }
        }
    }

    private fun isOnBoardingFinished(): Boolean {
        val sharedPref = getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref?.getBoolean("Finished", false) ?: false
    }

    private fun onBoardingFinished() {
        val sharedPref = getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        sharedPref?.edit {
            this.putBoolean("Finished", true)
        }
        startActivity(Intent(this, MainActivity::class.java))
    }
}

class ViewPagerAdapter(
    private val activity: StartActivity
) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnboardingFragment.newInstance(
                activity.getString(R.string.find_out_about_the_premieres),
                R.drawable.onboarding_image
            )

            1 -> OnboardingFragment.newInstance(
                activity.getString(R.string.create_collections),
                R.drawable.onboarding_image_2
            )

            else -> OnboardingFragment.newInstance(
                activity.getString(R.string.share_with_friends),
                R.drawable.onboarding_image_3
            )
        }
    }
}