package ug.global.recipeek

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.media.RingtoneManager.TYPE_ALARM
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ug.global.recipeek.databinding.ActivityDetailBinding
import ug.global.recipeek.db.RecipeWithIngredients

class DetailActivity : AppCompatActivity() {
    private val CHANNELID: String = "notifications"
    lateinit var detailBinding: ActivityDetailBinding
    @SuppressLint("UnspecifiedImmutableFlag") override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(detailBinding.root)
        val recipe = intent.getSerializableExtra("recipe") as RecipeWithIngredients
        detailBinding.recipeName.text = recipe.recipe.name

        detailBinding.closeIMage.setOnClickListener { supportFinishAfterTransition() }


        detailBinding.recipeCookTime.itemNAme.text = getString(R.string.cook_time)
        detailBinding.recipeCookTime.number.text = recipe.recipe.cookTime.toString()
        detailBinding.recipeCookTime.itemValue.text = getString(R.string.min)

        detailBinding.recipePrepareTime.itemNAme.text = getString(R.string.prep_tim)
        detailBinding.recipePrepareTime.number.text = recipe.recipe.prepTime.toString()
        detailBinding.recipePrepareTime.itemValue.text = getString(R.string.min)

        recipe.ingredients.forEach {
            detailBinding.recipeINgredients.append("\n" + it.name + " - " + it.amount + it.scale)
        }
        detailBinding.instructionsDetail.append(recipe.recipe.instructions)
        detailBinding.instructionsDetail.append("\n\n")
        detailBinding.instructionsDetail.append(recipe.recipe.cooking)

        detailBinding.cookButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = CHANNELID
                val channel = NotificationChannel(CHANNELID, name, NotificationManager.IMPORTANCE_HIGH).apply {
                    description = getString(R.string.channel_description)
                }
                // Register the channel with the system
                val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }

            val max = 100
            val current = 0
            val notificationId = 1211
            val cancelIntent = Intent(this, CancelNotification::class.java)
            val cancelPendingIntent = PendingIntent.getBroadcast(this, 0, cancelIntent, 0)

            val doneIntent = Intent(this, CompleteReceiver::class.java).apply {
                putExtra("RECIPE_ID", recipe.recipe.id)
            }
            val donePendingIntent = PendingIntent.getBroadcast(this, 0, doneIntent, 0)

            val builder = NotificationCompat.Builder(this, CHANNELID).apply {
                setSmallIcon(R.drawable.ic_baseline_cookie_24)
                setContentTitle("Cooking " + recipe.recipe.name)
                setContentText("You are now cooking. Alarm will sound when timer is done")
                setStyle(NotificationCompat.BigTextStyle())
                priority = NotificationCompat.PRIORITY_HIGH
                addAction(R.drawable.ic_baseline_close_24, "Cancel", cancelPendingIntent)
            }

            NotificationManagerCompat.from(this).apply {
                builder.setProgress(max, current, true)
                notify(notificationId, builder.build())
                val cookTime: Long = (recipe.recipe.cookTime * 60 * 1000).toLong()
//                AppExecutors.instance?.diskIO()?.execute {
                object : CountDownTimer(cookTime, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
//                        builder.setProgress(max, (100 * (cookTime - millisUntilFinished) / 100).toInt(), false)
//                        notify(notificationId, builder.build())
                    }

                    override fun onFinish() {
                        val alert = RingtoneManager.getDefaultUri(TYPE_ALARM)
                        val ringTone = RingtoneManager.getRingtone(this@DetailActivity, alert)
                        ringTone.play()

                        builder.setContentText("Your " + recipe.recipe.name + " should now be ready! Enjoy")
                            .addAction(R.drawable.ic_baseline_thumb_up_24, "Cool. Thanks", donePendingIntent)
                        notify(notificationId, builder.build())
                    }

                }.start()
//                }
                supportFinishAfterTransition()
            }
        }
    }

    override fun onBackPressed() {
        supportFinishAfterTransition()
    }
}