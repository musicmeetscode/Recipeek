package ug.global.recipeek

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import ug.global.recipeek.viewmodel.RecipesAdapter
import ug.global.recipeek.databinding.ActivityMainBinding
import ug.global.recipeek.db.Recipe
import ug.global.recipeek.viewmodel.AppViewModel

class MainActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityMainBinding

    @SuppressLint("InflateParams", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        val items = arrayOf("Salad", "Dessert", "Cake", "Breakfast", "Dinner", "Juice", "Drinks")
        items.shuffle()
        items.forEach {
            val chip = (layoutInflater.inflate(R.layout.food_type, null, false) as Chip)
            chip.text = it
            mainBinding.chipGroup.addView(chip)
        }
        val recipes = arrayListOf<Recipe>()
        val adapter = RecipesAdapter(recipes, this)
        mainBinding.recyclerView.adapter = adapter
        mainBinding.recyclerView.layoutManager = GridLayoutManager(this, 2)

        ViewModelProvider(this)[AppViewModel::class.java].recipes.observe(this) {
            recipes.addAll(it)
            adapter.notifyDataSetChanged()
        }
    }
}