package com.recipebookapp

sealed class ListItem {
    data class FlavorHeader(val flavor: Flavor) : ListItem()
    data class RecipeItem(val recipe: Recipe) : ListItem()
}