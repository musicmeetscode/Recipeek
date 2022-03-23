package ug.global.recipeek.db

import android.content.Context
import androidx.room.*
import java.io.Serializable

@Entity
data class Ingredient(
    @ColumnInfo var name: String, @ColumnInfo var amount: Int, @ColumnInfo var scale: String, @ColumnInfo(defaultValue = "0") var recipeId: Int,
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

@Entity
data class Recipe(
    @ColumnInfo var name: String,
    @ColumnInfo var cookTime: Int,
    @ColumnInfo var prepTime: Int,
    @ColumnInfo var instructions: String,
    @ColumnInfo(defaultValue = "") var cooking: String,
    @ColumnInfo var photo: String?,
    @ColumnInfo var type: String,
    @ColumnInfo var calories: Double,
    @ColumnInfo var serves: Int,
    @ColumnInfo(defaultValue = "false") var favorite: Boolean = false,
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

data class RecipeWithIngredients(
    @Embedded val recipe: Recipe,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val ingredients: List<Ingredient>,
) : Serializable

@Dao
interface DataDao {
    @Transaction
    @Query("SELECT * FROM recipe")
    fun getRecipes(): List<RecipeWithIngredients>

    @Insert
    fun insertRecipe(recipe: Recipe)

    @Query("SELECT MAX(id) FROM recipe")
    fun getLastId(): Int

    @Insert
    fun insertIngredients(ingredients: List<Ingredient>)

    @Update
    fun updateRecipe(recipe: Recipe)

    @Update
    fun updateIngredient(ingredient: Ingredient)
}

@Database(entities = [Recipe::class, Ingredient::class], version = 5, exportSchema = true, autoMigrations = [
    AutoMigration(from = 1, to = 2),
    AutoMigration(from = 2, to = 3),
    AutoMigration(from = 3, to = 4),
    AutoMigration(from = 4, to = 5)

])
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): DataDao

    companion object {
        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context, AppDatabase::class.java, "database").build()
            }
            return instance as AppDatabase
        }
    }
}

interface RecipeCAllBacks {
    fun recipeModified(recipe: RecipeWithIngredients)
}