package ug.global.recipeek.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import java.io.Serializable

@Entity data class Ingredient(
    @ColumnInfo var name: String, @ColumnInfo var amount: Float, @ColumnInfo var scale: String, @ColumnInfo(defaultValue = "0") var recipeId: Int,
) : Serializable {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}

@Entity data class Recipe(
    @ColumnInfo var name: String,
    @ColumnInfo var cookTime: Int,
    @ColumnInfo var prepTime: Int,
    @ColumnInfo var instructions: String,
    @ColumnInfo(defaultValue = "") var cooking: String,
    @ColumnInfo var photo: String?,
    @ColumnInfo var type: String,
    @ColumnInfo(defaultValue = "false") var favorite: Boolean = false,
    @ColumnInfo(defaultValue = "0") var cooked: Int = 0,
) : Serializable {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}

data class RecipeWithIngredients(
    @Embedded val recipe: Recipe,
    @Relation(parentColumn = "id", entityColumn = "recipeId") val ingredients: List<Ingredient>,
) : Serializable

@Dao interface DataDao {
    @Transaction @Query("SELECT * FROM recipe") fun getRecipes(): List<RecipeWithIngredients>

    @Insert fun insertRecipe(recipe: Recipe)

    @Query("SELECT MAX(id) FROM recipe") fun getLastId(): Int

    @Insert fun insertIngredients(ingredients: List<Ingredient>)

    @Query("UPDATE recipe SET cooked=cooked+1 WHERE id=:id") fun updateCookTimes(id: Int)

    @Update fun updateRecipe(recipe: Recipe)

    @Update fun updateIngredient(ingredient: Ingredient)
}

@Database(entities = [Recipe::class, Ingredient::class],
    version = 9,
    exportSchema = true,
    autoMigrations = [AutoMigration(from = 1, to = 2), AutoMigration(from = 2, to = 3), AutoMigration(from = 3, to = 4), AutoMigration(from = 4,
        to = 5), AutoMigration(from = 5, to = 6), AutoMigration(from = 6, to = 7, spec = AppDatabase.RemoveCalories::class), AutoMigration(from = 7,
        to = 8,
        spec = AppDatabase.RemoveServes::class), AutoMigration(from = 8, to = 9)

    ]) abstract class AppDatabase : RoomDatabase() {
    @DeleteColumn(tableName = "Recipe", columnName = "calories") class RemoveCalories : AutoMigrationSpec
    @DeleteColumn(tableName = "Recipe", columnName = "serves") class RemoveServes : AutoMigrationSpec

    abstract fun dao(): DataDao

    companion object {
        private var instance: AppDatabase? = null

        @Synchronized fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context, AppDatabase::class.java, "database").build()
            }
            return instance as AppDatabase
        }
    }
}

