package com.example.fetchrewardscodingexercise.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fetchrewardscodingexercise.model.Item
import com.example.fetchrewardscodingexercise.network.RetrofitInstance
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

/**
 * ViewModel for managing and fetching items from the API.
 */
class ItemViewModel : ViewModel() {
    // LiveData to hold fetched items.
    val itemsLiveData = MutableLiveData<List<Item>>()

    // LiveData to indicate loading status.
    val loadingLiveData = MutableLiveData<Boolean>()

    // LiveData to communicate errors.
    val errorLiveData = MutableLiveData<String>()

    /**
     * Fetches items from the API.
     */
    fun fetchItems() {
        viewModelScope.launch {
            setLoading(true)

            val response = try {
                RetrofitInstance.api.getItems()
            } catch (e: IOException) {
                handleException("IOException, you might not have an internet connection")
                return@launch
            } catch (e: HttpException) {
                handleException("HttpException, unexpected response")
                return@launch
            }

            if (response.isSuccessful && response.body() != null) {
                val filteredAndSortedItems = processItems(response.body()!!)
                itemsLiveData.postValue(filteredAndSortedItems)
            } else {
                handleException("Response not successful")
            }

            setLoading(false)
        }
    }

    /**
     * Filters null or empty names and
     * sorts items first by listId and then by the integer value of the name.
     */
    private fun processItems(items: List<Item>): List<Item> {
        return items
            .filter { it.name != null && it.name.trim().isNotEmpty() }
            .sortedWith(compareBy(
                { it.listId },
                { it.name?.substring(5)?.toIntOrNull() ?: 0 })
            )
    }

    /**
     * Handles exceptions by updating the loading and error LiveData.
     */
    private fun handleException(errorMessage: String) {
        setLoading(false)
        errorLiveData.postValue(errorMessage)
    }

    /**
     * Updates the loading status LiveData.
     */
    private fun setLoading(isLoading: Boolean) {
        loadingLiveData.postValue(isLoading)
    }
}
