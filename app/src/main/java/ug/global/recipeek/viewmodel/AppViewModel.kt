package ug.global.recipeek.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ug.global.recipeek.db.Recipe

class AppViewModel(application: Application) : AndroidViewModel(application) {
    val recipes: MutableLiveData<List<Recipe>> = MutableLiveData<List<Recipe>>()
        .apply {
            val list = arrayListOf(Recipe(1), Recipe(1), Recipe(1), Recipe(1), Recipe(1), Recipe(1), Recipe(1))
            value = list
        }
}