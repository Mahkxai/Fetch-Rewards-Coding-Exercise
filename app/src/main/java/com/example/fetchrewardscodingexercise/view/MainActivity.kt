package com.example.fetchrewardscodingexercise.view

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fetchrewardscodingexercise.adapter.ItemAdapter
import com.example.fetchrewardscodingexercise.databinding.ActivityMainBinding
import com.example.fetchrewardscodingexercise.utils.StickyHeaderItemDecoration
import com.example.fetchrewardscodingexercise.viewmodel.ItemViewModel

const val TAG = "MainActivity"

/**
 * Main activity that displays a fetched list of items with sticky headers.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: ItemViewModel
    private lateinit var binding: ActivityMainBinding
    private val itemAdapter = ItemAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup UI components.
        setupStatusBar()
        setupRecyclerView()
        setupViewModel()
        setupFetchButton()
    }

    /**
     * Set up the status bar appearance based on the OS version.
     */
    private fun setupStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.statusBarColor = Color.WHITE
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }
    }

    /**
     * Initializes and configures the RecyclerView.
     */
    private fun setupRecyclerView() = binding.rvItems.apply {
        adapter = itemAdapter
        layoutManager = LinearLayoutManager(this@MainActivity)
        addItemDecoration(StickyHeaderItemDecoration(itemAdapter))
    }

    /**
     * Sets up the ViewModel and observes LiveData.
     */
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[ItemViewModel::class.java]
        observeData()
    }

    /**
     * Set up the fetch button to fetch items on click and hide itself.
     */
    private fun setupFetchButton() {
        binding.btnFetchItems.setOnClickListener {
            it.visibility = View.GONE
            startShimmerEffect()

            binding.shimmerViewContainer.post {
                viewModel.fetchItems()
            }
        }
    }

    /**
     * Observes data from the ViewModel and updates the UI accordingly.
     */
    private fun observeData() {
        viewModel.itemsLiveData.observe(this) {
            stopShimmerEffect()
            binding.tvInitials.visibility = View.VISIBLE
            binding.tvFooter.visibility = View.GONE
            itemAdapter.items = it
        }

        viewModel.loadingLiveData.observe(this) {
            if (!it) stopShimmerEffect()
        }

        viewModel.errorLiveData.observe(this) { e ->
            handleDataFetchError(e)
        }
    }

    /**
     * Handle scenarios when there's an error fetching the data.
     * @param error The error message.
     */
    private fun handleDataFetchError(error: String) {
        stopShimmerEffect()
        resetUI()
        Log.e(TAG, error)
        Toast.makeText(this, "An error occurred: $error", Toast.LENGTH_SHORT).show()
    }

    /**
     * Reset the UI elements to their default state in case of an error.
     */
    private fun resetUI() {
        with(binding) {
            tvInitials.visibility = View.GONE
            rvItems.visibility = View.GONE
            btnFetchItems.visibility = View.VISIBLE
            binding.tvFooter.visibility = View.VISIBLE
        }
    }

    /**
     * Start the shimmering effect.
     */
    private fun startShimmerEffect() {
        with(binding.shimmerViewContainer) {
            visibility = View.VISIBLE
            startShimmer()
        }
    }

    /**
     * Stop the shimmering effect.
     */
    private fun stopShimmerEffect() {
        with(binding.shimmerViewContainer) {
            stopShimmer()
            visibility = View.GONE
        }
    }
}
