package ug.global.recipeek

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import ug.global.recipeek.databinding.ActivityMainBinding
import ug.global.recipeek.db.RecipeWithIngredients
import ug.global.recipeek.viewmodel.AppViewModel
import ug.global.recipeek.viewmodel.RecipesAdapter

class MainActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityMainBinding

    @SuppressLint("InflateParams", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }
}