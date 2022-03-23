package ug.global.recipeek

import android.annotation.SuppressLint
import android.content.DialogInterface.BUTTON_POSITIVE
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ug.global.recipeek.databinding.ActivityMainBinding
import ug.global.recipeek.databinding.AddIngredientBinding
import ug.global.recipeek.databinding.AddRecipeBinding
import ug.global.recipeek.databinding.FoodTypeBinding
import ug.global.recipeek.db.*
import ug.global.recipeek.viewmodel.AppViewModel
import ug.global.recipeek.viewmodel.RecipesAdapter
import ug.musicmeetscode.appexecutors.AppExecutors

class MainActivity : AppCompatActivity(), RecipeCAllBacks {
    private lateinit var mainBinding: ActivityMainBinding
    private var newId = 0

    @SuppressLint("InflateParams", "NotifyDataSetChanged", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dialog = BottomSheetDialog(this)
        AppExecutors.instance?.diskIO()?.execute {
            newId = AppDatabase.getInstance(this).dao().getLastId()
        }
        val dialogView = AddRecipeBinding.inflate(layoutInflater)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        val items = arrayOf("All", "Salad", "Dessert", "Cake", "Breakfast", "Dinner", "Juice", "Drinks")
        items.shuffle()
        items.forEach {
            val chip = (layoutInflater.inflate(R.layout.food_type, null, false) as Chip)
            chip.isChecked = it == "All"
            chip.text = it
            mainBinding.chipGroup.addView(chip)
        }
        val recipes = arrayListOf<RecipeWithIngredients>()
        val adapter = RecipesAdapter(recipes, this)
        mainBinding.recyclerView.adapter = adapter
        mainBinding.recyclerView.layoutManager = GridLayoutManager(this, 2)

        ViewModelProvider(this)[AppViewModel::class.java].recipes.observe(this) {
            if (it.isEmpty()) {
                mainBinding.emptyImage.visibility = View.VISIBLE
                mainBinding.emptyText.visibility = View.VISIBLE
                mainBinding.chipGroup.visibility = View.GONE
                mainBinding.search.isEnabled = false
            }
            recipes.addAll(it)
            adapter.notifyDataSetChanged()
        }
        mainBinding.floatingActionButton.setOnClickListener {
            val ingredients = arrayListOf<Ingredient>()
            dialog.setContentView(dialogView.root)
            dialogView.addIngredient.setOnClickListener {
                val ingredientAlert = MaterialAlertDialogBuilder(this).create()
                val ingredientBinding = AddIngredientBinding.inflate(layoutInflater)
                ingredientAlert.setTitle("Add ingredient")
                ingredientAlert.setView(ingredientBinding.root)
                ingredientAlert.setButton(BUTTON_POSITIVE, "SAve"
                ) { _, _ ->
                    val ingredient = Ingredient(ingredientBinding.igredient.editableText.toString(), ingredientBinding.amount.editableText.toString()
                        .toInt(), ingredientBinding.unit.editableText.toString(), newId)
                    ingredients.add(ingredient)
                    val chip = FoodTypeBinding.inflate(layoutInflater).root
                    chip.text = "${ingredient.name}- ${ingredient.amount}${ingredient.scale}"
                    dialogView.ingredients.addView(chip)
                }
                dialogView.saveRecipee.setOnClickListener {
                    val recipe = Recipe(
                        dialogView.recipeName.editableText.toString(),
                        dialogView.cookTime.editableText.toString().toInt(),
                        dialogView.prepTime.editableText.toString().toInt(),
                        dialogView.instructions.editableText.toString(),
                        dialogView.cooking.editableText.toString(),
                        null,
                        "Cake",
                        dialogView.calories.editableText.toString().toDouble(),
                        dialogView.plates.editableText.toString().toInt()
                    )
                    AppExecutors.instance?.diskIO()?.execute {
                        AppDatabase.getInstance(this).dao().insertRecipe(recipe)
                        AppDatabase.getInstance(this).dao().insertIngredients(ingredients)
                    }

                    mainBinding.emptyImage.visibility = View.GONE
                    mainBinding.emptyText.visibility = View.GONE
                    mainBinding.chipGroup.visibility = View.VISIBLE
                    mainBinding.search.isEnabled = false
                    recipes.add(RecipeWithIngredients(recipe, ingredients))
                    adapter.notifyItemInserted(recipes.size - 1)
                    dialog.dismissWithAnimation = true
                    dialog.dismiss()
                }
                ingredientAlert.show()
            }
            dialog.show()
        }
    }

    override fun recipeModified(recipe: RecipeWithIngredients) {
        AppExecutors.instance?.diskIO()?.execute {
            AppDatabase.getInstance(this).dao().updateRecipe(recipe.recipe)
            recipe.ingredients.forEach {
                AppDatabase.getInstance(this).dao().updateIngredient(it)
            }
        }
    }
}