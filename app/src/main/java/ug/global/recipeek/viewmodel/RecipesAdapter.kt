package ug.global.recipeek.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ug.global.recipeek.R
import ug.global.recipeek.db.RecipeWithIngredients

class RecipesAdapter(var recipes: ArrayList<RecipeWithIngredients>, var context: Context) : RecyclerView.Adapter<RecipesAdapter.ViewHolder>() {
    var filteredRecipes = recipes

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photo: ImageView = itemView.findViewById(R.id.photo)
        val name: TextView = itemView.findViewById(R.id.name)
        val ingredients: TextView = itemView.findViewById(R.id.first_ingredients)
        val calories: TextView = itemView.findViewById(R.id.calories)
        val favorite: ImageView = itemView.findViewById(R.id.favorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.recipe_layout, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (recipe, ingredients) = recipes[position]
        holder.name.text = recipe.name
        holder.calories.text = "${recipe.calories} Cal"
        holder.ingredients.text = ingredients[0].name
    }

    override fun getItemCount(): Int {
        return filteredRecipes.size
    }
}