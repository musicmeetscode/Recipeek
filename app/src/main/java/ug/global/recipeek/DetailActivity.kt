package ug.global.recipeek

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ug.global.recipeek.databinding.ActivityDetailBinding
import ug.global.recipeek.db.RecipeWithIngredients

class DetailActivity : AppCompatActivity() {
    lateinit var detailBinding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(detailBinding.root)
        val recipe = intent.getSerializableExtra("recipe") as RecipeWithIngredients
        detailBinding.recipeName.text = recipe.recipe.name

        detailBinding.closeIMage.setOnClickListener { supportFinishAfterTransition() }

        detailBinding.caloriesRecipe.itemNAme.text = getString(R.string.calo)
        detailBinding.caloriesRecipe.number.text = recipe.recipe.calories.toString()
        detailBinding.caloriesRecipe.itemValue.text = "cal"

        detailBinding.recipeCookTime.itemNAme.text = getString(R.string.cook_time)
        detailBinding.recipeCookTime.number.text = recipe.recipe.cookTime.toString()
        detailBinding.recipeCookTime.itemValue.text = getString(R.string.min)

        detailBinding.recipePrepareTime.itemNAme.text = getString(R.string.prep_tim)
        detailBinding.recipePrepareTime.number.text = recipe.recipe.prepTime.toString()
        detailBinding.recipePrepareTime.itemValue.text = getString(R.string.min)

        recipe.ingredients.forEach {
            detailBinding.recipeINgredients.append("\n" + it.name + " - " + it.amount + it.scale)
        }
        detailBinding.instructionsDetail.append(recipe.recipe.instructions)
        detailBinding.instructionsDetail.append("\n\n")
        detailBinding.instructionsDetail.append(recipe.recipe.cooking)
    }

    override fun onBackPressed() {
        supportFinishAfterTransition()
    }
}