package ug.global.recipeek.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import ug.global.recipeek.DetailActivity
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
        val root: MaterialCardView = itemView.findViewById(R.id.main)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.recipe_layout, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.name.text = recipe.recipe.name
        holder.calories.text = "${recipe.recipe.cookTime + recipe.recipe.prepTime} min"
        try {

            holder.ingredients.text = recipe.ingredients[0].name
        } catch (e: Exception) {
        }
        holder.root.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java).putExtra("recipe", recipe)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(context as Activity,
                holder.root,
                context.getString(R.string.recipe_name))
            context.startActivity(intent, options.toBundle())
        }
    }

    override fun getItemCount(): Int {
        return filteredRecipes.size
    }
}