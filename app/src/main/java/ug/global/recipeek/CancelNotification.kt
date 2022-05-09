package ug.global.recipeek

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class CancelNotification : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
//        TODO("Not yet implemented")
        context?.let { NotificationManagerCompat.from(it).cancel(1211) }
    }

}
