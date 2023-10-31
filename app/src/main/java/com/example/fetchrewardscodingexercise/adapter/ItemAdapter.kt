package com.example.fetchrewardscodingexercise.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.fetchrewardscodingexercise.databinding.ItemBinding
import com.example.fetchrewardscodingexercise.model.Item

/**
 * Adapter class for RecyclerView that displays Items and Section headers.
 */
class ItemAdapter : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    // ViewHolder for individual items
    inner class ItemViewHolder(val binding: ItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    // DiffCallback for efficient list updates
    private val diffCallback = object : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Item, newItem: Item) = oldItem == newItem
    }

    // Handles list differences asynchronously
    private val differ = AsyncListDiffer(this, diffCallback)

    // Represents the data shown by the adapter
    var items: List<Item>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    // Total item count
    override fun getItemCount() = items.size

    // Creates ViewHolder based on the view type
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(ItemBinding.inflate(inflater, parent, false))
    }

    // Binds appropriate data to the ViewHolder
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvItemTitle.text = item.name
    }
}