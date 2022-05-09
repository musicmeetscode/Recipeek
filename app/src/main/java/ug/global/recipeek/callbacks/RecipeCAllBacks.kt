package ug.global.recipeek.callbacks

import ug.global.recipeek.db.Recipe
import ug.global.recipeek.db.RecipeWithIngredients

interface RecipeCAllBacks {

    fun recipeModified(recipe: RecipeWithIngredients)
    fun cookingStarted(recipe: Recipe)
    fun editingStarted(recipe: RecipeWithIngredients)
}