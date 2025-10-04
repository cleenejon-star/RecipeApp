package com.recipebookapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.recipebookapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupButtonListeners()
        setupSwipeToDelete()
    }

    private fun setupRecyclerView() {
        // The click listener for showing a dialog
        recipeAdapter = RecipeAdapter { recipe ->
            AlertDialog.Builder(this)
                .setTitle(recipe.title)
                .setMessage(recipe.description)
                .setPositiveButton("OK", null)
                .show()
        }

        binding.mainRecipesList.apply {
            adapter = recipeAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        // Set initial data with headers
        val initialList = mutableListOf<ListItem>(
            ListItem.FlavorHeader(Flavor.SAVORY),
            ListItem.FlavorHeader(Flavor.SWEET)
        )
        recipeAdapter.setData(initialList)
    }

    private fun setupButtonListeners() {
        binding.mainAddSavoryButton.setOnClickListener {
            addRecipe(Flavor.SAVORY)
        }

        binding.mainAddSweetButton.setOnClickListener {
            addRecipe(Flavor.SWEET)
        }
    }

    private fun addRecipe(flavor: Flavor) {
        val title = binding.mainRecipeTitle.text.toString()
        val description = binding.mainRecipeDescription.text.toString()

        if (title.isNotBlank() && description.isNotBlank()) {
            val newRecipe = Recipe(title, description, flavor)
            recipeAdapter.addRecipe(newRecipe)
            // Clear the form after adding [cite: 69]
            binding.mainRecipeTitle.text.clear()
            binding.mainRecipeDescription.text.clear()
        } else {
            Snackbar.make(binding.root, "Please fill out both fields.", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupSwipeToDelete() {
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false // We don't need drag-and-drop

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                recipeAdapter.removeRecipe(position)
            }

            // Disable swipe for flavor headers
            override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                return if (viewHolder is RecipeAdapter.FlavorHeaderViewHolder) {
                    0 // No swipe
                } else {
                    super.getSwipeDirs(recyclerView, viewHolder)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(binding.mainRecipesList)
    }
}