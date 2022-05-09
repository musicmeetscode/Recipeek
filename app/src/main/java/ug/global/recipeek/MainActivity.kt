package ug.global.recipeek

import android.annotation.SuppressLint
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ug.global.recipeek.callbacks.RecipeCAllBacks
import ug.global.recipeek.databinding.ActivityMainBinding
import ug.global.recipeek.databinding.AddIngredientBinding
import ug.global.recipeek.databinding.AddRecipeBinding
import ug.global.recipeek.databinding.FoodTypeBinding
import ug.global.recipeek.db.AppDatabase
import ug.global.recipeek.db.Ingredient
import ug.global.recipeek.db.Recipe
import ug.global.recipeek.db.RecipeWithIngredients
import ug.global.recipeek.viewmodel.AppViewModel
import ug.global.recipeek.viewmodel.RecipesAdapter
import ug.musicmeetscode.appexecutors.AppExecutors

class MainActivity : AppCompatActivity(), RecipeCAllBacks {
    private lateinit var mainBinding: ActivityMainBinding
    private var newId = 0

    val getUser = registerForActivityResult(ActivityResultContracts.GetContent()) {

//        GoogleSignIn.getSignedInAccountFromIntent(it.)
        Log.e("TAG", it.toString())
    }

    @SuppressLint("InflateParams", "NotifyDataSetChanged", "SetTextI18n") override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppExecutors.instance?.diskIO()?.execute {
            newId = AppDatabase.getInstance(this).dao().getLastId() + 1
        }
        val dialogView = AddRecipeBinding.inflate(layoutInflater)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        val client = GoogleSignIn.getClient(this, gso)
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            mainBinding.nameTxt.text = "Recipeek - Hi " + account.displayName
        } else {
            startActivityForResult(client.signInIntent, 1211)
        }
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

            val dialog = BottomSheetDialog(this)
            dialog.setContentView(dialogView.root)
            dialogView.addIngredient.setOnClickListener {
                val ingredientAlert = MaterialAlertDialogBuilder(this).create()
                val ingredientBinding = AddIngredientBinding.inflate(layoutInflater)
                ingredientAlert.setTitle("Add ingredient")
                ingredientAlert.setView(ingredientBinding.root)
                ingredientAlert.setButton(BUTTON_POSITIVE, "SAve") { _, _ ->
                    val ingredient = Ingredient(ingredientBinding.igredient.editableText.toString(),
                        ingredientBinding.amount.editableText.toString().toFloat(),
                        ingredientBinding.unit.editableText.toString(),
                        newId)
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

    override fun cookingStarted(recipe: Recipe) {


    }

    override fun editingStarted(recipe: RecipeWithIngredients) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1211) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val res = task.getResult(ApiException::class.java)
                mainBinding.nameTxt.text = "Recipeek - Hi " + res.displayName

            } catch (e: ApiException) {
                Log.e("TAG", "onActivityResult: ", e)
            }
        }
    }
}