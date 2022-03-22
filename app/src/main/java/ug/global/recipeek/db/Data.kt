package ug.global.recipeek.db

import android.content.Context
import androidx.room.*

@Entity
data class Ingredient(
    @ColumnInfo var name: String, @ColumnInfo var amount: Int, @ColumnInfo var scale: String,
) {
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
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

data class RecipeWithIngredients(
    @Embedded val recipe: Recipe,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val ingredients: List<Ingredient>,
)

@Dao
interface DataDao {
    @Transaction
    @Query("SELECT * FROM recipe")
    fun getRecipes(): List<RecipeWithIngredients>

    @Insert
    fun insertRecipe(recipe: Recipe)

    @Insert
    fun insertIngredients(ingredients: List<Ingredient>)
}

@Database(entities = [Recipe::class, Ingredient::class], version = 4, exportSchema = true, autoMigrations = [
    AutoMigration(from = 1, to = 2),
    AutoMigration(from = 2, to = 3),
    AutoMigration(from = 3, to = 4)
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