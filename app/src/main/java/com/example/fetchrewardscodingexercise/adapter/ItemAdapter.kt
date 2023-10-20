package com.example.fetchrewardscodingexercise.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.fetchrewardscodingexercise.databinding.ItemBinding
import com.example.fetchrewardscodingexercise.databinding.SectionHeaderBinding
import com.example.fetchrewardscodingexercise.model.Item
import com.example.fetchrewardscodingexercise.model.Section

/**
 * Adapter class for RecyclerView that displays Items and Section headers.
 */
class ItemAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        // Constants for different view types
        const val TYPE_SECTION = 0
        const val TYPE_ITEM = 1
    }

    // ViewHolder for individual items
    inner class ItemViewHolder(val binding: ItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    // ViewHolder for section headers
    inner class SectionViewHolder(val binding: SectionHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

    // DiffCallback for efficient list updates
    private val diffCallback = object : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is Item && newItem is Item -> oldItem.id == newItem.id
                oldItem is Section && newItem is Section -> oldItem.listId == newItem.listId
                else -> false
            }
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean = oldItem == newItem
    }

    // Handles list differences asynchronously
    private val differ = AsyncListDiffer(this, diffCallback)

    // Represents the data shown by the adapter
    var items: List<Any>
        get() = differ.currentList
        set(value) {
            val processedList = value.processIntoSections()
            differ.submitList(processedList)
        }

    // Determines the type of view for a given position
    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Section -> TYPE_SECTION
            is Item -> TYPE_ITEM
            else -> throw IllegalArgumentException("Unsupported type at position $position")
        }
    }

    // Total item count
    override fun getItemCount() = items.size

    // Creates ViewHolder based on the view type
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_SECTION -> SectionViewHolder(SectionHeaderBinding.inflate(inflater, parent, false))
            TYPE_ITEM -> ItemViewHolder(ItemBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Unsupported view type: $viewType")
        }
    }

    // Binds appropriate data to the ViewHolder
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is Section -> bindHeader(holder as SectionViewHolder, item)
            is Item -> bindItem(holder as ItemViewHolder, item)
        }
    }

    // Binds data to Section header ViewHolder
    private fun bindHeader(holder: SectionViewHolder, section: Section) {
        holder.binding.tvSectionHeader.text = "List ID: ${section.listId}"
    }

    // Binds data to Item ViewHolder
    private fun bindItem(holder: ItemViewHolder, item: Item) {
        holder.binding.tvItemTitle.text = item.name
    }

    // Helper function to process the list into Sections according to listId
    private fun List<Any>.processIntoSections(): List<Any> {
        val processedList = mutableListOf<Any>()
        filterIsInstance<Item>().groupBy { it.listId }.forEach { (listId, items) ->
            processedList.add(Section(listId))
            processedList.addAll(items)
        }
        return processedList
    }
}
