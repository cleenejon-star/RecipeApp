package com.recipebookapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecipeAdapter(
    private val onRecipeClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<ListItem>()

    // View types for headers and recipes
    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_RECIPE = 1
    }

    // ViewHolder for Flavor Headers
    inner class FlavorHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.flavor_header_title)
        fun bind(header: ListItem.FlavorHeader) {
            titleTextView.text = header.flavor.name
        }
    }

    // ViewHolder for Recipe Items
    inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.recipe_item_title)
        fun bind(recipeItem: ListItem.RecipeItem) {
            titleTextView.text = recipeItem.recipe.title
            itemView.setOnClickListener {
                onRecipeClick(recipeItem.recipe)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ListItem.FlavorHeader -> VIEW_TYPE_HEADER
            is ListItem.RecipeItem -> VIEW_TYPE_RECIPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_flavor_header, parent, false)
                FlavorHeaderViewHolder(view)
            }
            VIEW_TYPE_RECIPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_recipe, parent, false)
                RecipeViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val currentItem = items[position]) {
            is ListItem.FlavorHeader -> (holder as FlavorHeaderViewHolder).bind(currentItem)
            is ListItem.RecipeItem -> (holder as RecipeViewHolder).bind(currentItem)
        }
    }

    override fun getItemCount() = items.size

    fun getItemAt(position: Int): ListItem = items[position]

    fun setData(initialItems: List<ListItem>) {
        items.clear()
        items.addAll(initialItems)
        notifyDataSetChanged()
    }

    fun addRecipe(recipe: Recipe) {
        val recipeItem = ListItem.RecipeItem(recipe)
        val insertionIndex = if (recipe.flavor == Flavor.SAVORY) {
            // Insert after Savory header but before the Sweet header
            items.indexOfFirst { it is ListItem.FlavorHeader && it.flavor == Flavor.SWEET }
        } else {
            // Insert at the end of the list (under Sweet)
            items.size
        }
        items.add(insertionIndex, recipeItem)
        notifyItemInserted(insertionIndex)
    }

    fun removeRecipe(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }
}