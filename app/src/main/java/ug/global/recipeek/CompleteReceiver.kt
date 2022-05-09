package ug.global.recipeek

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ug.global.recipeek.db.AppDatabase
import ug.musicmeetscode.appexecutors.AppExecutors

class CompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val id: Int = intent?.extras?.get("RECIPE_ID") as Int
        AppExecutors.instance?.diskIO()?.execute {
            context?.let { AppDatabase.getInstance(it).dao().updateCookTimes(id) }
        }
    }

}
