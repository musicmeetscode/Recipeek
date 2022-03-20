package ug.global.recipeek.db

import android.content.Context
import androidx.room.*

@Entity
data class Ingredient(
    @ColumnInfo var name: String, @ColumnInfo var amount: String, @ColumnInfo var scale: String,
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
    @ColumnInfo var photo: String?,
    @ColumnInfo var type: String,
    @ColumnInfo var calories: Double,
    @ColumnInfo var serves: Int,
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
}

@Database(entities = [Recipe::class, Ingredient::class], version = 1, exportSchema = true)
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