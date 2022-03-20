package ug.global.recipeek.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ug.global.recipeek.db.AppDatabase
import ug.global.recipeek.db.RecipeWithIngredients
import ug.musicmeetscode.appexecutors.AppExecutors

class AppViewModel(application: Application) : AndroidViewModel(application) {
    val recipes: MutableLiveData<List<RecipeWithIngredients>> = MutableLiveData<List<RecipeWithIngredients>>()
        .apply {
            AppExecutors.instance?.diskIO()?.execute {
                val recipes = AppDatabase.getInstance(application.applicationContext).dao().getRecipes()
                AppExecutors.instance?.mainThread()?.execute {
                    value = recipes
                }
            }
        }
}