package com.example.fetchrewardscodingexercise.model

/**
 * Represents an individual item in a list.
 *
 * @property id Unique identifier for the item.
 * @property listId Identifier for the list this item belongs to.
 * @property name The name or description of the item.
 */
data class Item(
    val id: Int,
    val listId: Int,
    val name: String?
)