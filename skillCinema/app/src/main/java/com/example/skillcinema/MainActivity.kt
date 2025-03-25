package com.example.skillcinema

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.skillcinema.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

//todo DONE
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = (application as App).repository
                val dataStore = (application as App).dataStore
                return MainViewModel(dataStore, repository) as T
            }
        }
    }
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val navView by lazy { binding.navView }
    private val progressIndicator by lazy { binding.progressIndicator }

    private val navController by lazy {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navHostFragment.navController
    }
    private val appBarConfiguration by lazy { AppBarConfiguration(navController.graph) }

    private val destinationChangeListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.filmpageFragment -> {
                    setRootPaddingToZero()
                    showBottomNav()
                }

                R.id.galleryViewPagerFragment -> {
                    setRootPaddingToZero()
                    hideBottomNav()
                }

                R.id.settingsFragment,
                R.id.countryGenreChangerFragment,
                R.id.yearChangerFragment -> hideBottomNav()

                else -> {
                    setRootPaddingToWindowInsets()
                    showBottomNav()
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            viewModel.resetSettingsToDefault()
        }

        navView.apply {
            navController.let { navController ->
                NavigationUI.setupWithNavController(this, navController)

                setOnItemSelectedListener { item ->
                    NavigationUI.onNavDestinationSelected(item, navController)
                    true
                }
                setOnItemReselectedListener {
                    navController.popBackStack(destinationId = it.itemId, inclusive = false)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putBoolean("isRestart", true)
        super.onSaveInstanceState(outState, outPersistentState)
    }

    private fun setRootPaddingToWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setRootPaddingToZero() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }
    }

    fun showProgressIndicator() {
        progressIndicator.visibility = View.VISIBLE
    }

    fun hideProgressIndicator() {
        progressIndicator.visibility = View.GONE
    }

    private fun showBottomNav() {
        navView.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
        navView.visibility = View.GONE
    }

    fun showErrorBottomSheetFragment() {
        BottomSheetDialog(this).apply {
            setContentView(R.layout.error_bottom_sheet_fragment)
            findViewById<ImageView>(R.id.closeIcon)?.setOnClickListener { dismiss() }
            show()
        }
    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(destinationChangeListener)
    }

    override fun onPause() {
        navController.removeOnDestinationChangedListener(destinationChangeListener)
        super.onPause()
    }
}