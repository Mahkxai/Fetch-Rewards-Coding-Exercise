package com.example.fetchrewardscodingexercise.view

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.fetchrewardscodingexercise.R
import com.example.fetchrewardscodingexercise.databinding.ActivityMainBinding
import com.example.fetchrewardscodingexercise.adapter.ItemAdapter
import com.example.fetchrewardscodingexercise.viewmodel.ItemViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup

const val TAG = "MainActivity"

/**
 * Main activity that displays the fetched list of items with sticky headers.
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
     * +Setup Swipe to Refresh
     */
    private fun observeData() {
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)

        viewModel.listIdsLiveData.observe(this) { listIds ->
            setupSwipeToRefresh()
            stopShimmerEffect()
            binding.tvInitials.visibility = View.VISIBLE
            binding.tvFooter.visibility = View.GONE
            setupListIDs(listIds)
        }

        viewModel.itemsLiveData.observe(this) { listItems ->
            val tvItemsCount = findViewById<TextView>(R.id.tvItemsCout)

            // Make #items display in bold
            val listSize = listItems.size.toString()
            val numResults = "$listSize results"
            val boldNumResults = SpannableString(numResults)
            boldNumResults.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                listSize.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            tvItemsCount.text = boldNumResults

            itemAdapter.items = listItems
        }

        viewModel.loadingLiveData.observe(this) {
            if (!it) {
                stopShimmerEffect()
                swipeRefresh.isRefreshing = false
            }
        }

        viewModel.errorLiveData.observe(this) { e ->
            handleDataFetchError(e)
        }
    }

    /**
     * Categorize and setup chips to display appropriate list items according to listId
     */
    private fun setupListIDs(listIds: List<Int>) {
        val chipGroup = findViewById<ChipGroup>(R.id.chipGroup)
        chipGroup.removeAllViews() // Remove old chips

        for (listId in listIds) {
            val chip = Chip(this)
            val drawable = ChipDrawable.createFromAttributes(
                this,
                null,
                0, R.style.CustomChipStyle
            )
            chip.setChipDrawable(drawable)
            chip.textSize = 10f
            chip.text = "List $listId"
            chip.isClickable = true
            chip.isCheckable = true
            chip.isCheckedIconVisible = false
            chipGroup.addView(chip)

            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    chip.chipStrokeWidth = 5f
                    viewModel.filterItemsByListId(listId)
                } else {
                    chip.chipStrokeWidth = 1f
                }
            }

            chip.setOnClickListener {
                if (!chip.isChecked) {
                    chip.isChecked = true
                }
            }
        }
        // Set the default items display for the first list ID
        (chipGroup.getChildAt(0) as Chip).isChecked = true
        viewModel.filterItemsByListId(listIds[0])
    }

    /**
     * Handle scenarios when there's an error fetching the data.
     * @param error The error message.
     */
    private fun handleDataFetchError(error: String) {
        stopShimmerEffect()
        resetUI()
        Log.e(TAG, error)
        Toast.makeText(this, "Data Fetch Failed!", Toast.LENGTH_SHORT).show()
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

    // Setup SwipeToRefresh to fetch data
    private fun setupSwipeToRefresh() {
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        swipeRefresh.setOnRefreshListener {
            viewModel.fetchItems()
        }
    }


}
