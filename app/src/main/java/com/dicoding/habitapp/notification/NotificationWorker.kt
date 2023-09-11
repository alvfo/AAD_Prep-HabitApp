package com.dicoding.habitapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dicoding.habitapp.R
import com.dicoding.habitapp.ui.countdown.CountDownActivity
import com.dicoding.habitapp.ui.detail.DetailHabitActivity
import com.dicoding.habitapp.ui.list.HabitListActivity
import com.dicoding.habitapp.utils.HABIT_ID
import com.dicoding.habitapp.utils.HABIT_TITLE
import com.dicoding.habitapp.utils.NOTIFICATION_CHANNEL_ID
import com.dicoding.habitapp.utils.NOTIF_UNIQUE_WORK

class NotificationWorker(private val ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val habitId = inputData.getInt(HABIT_ID, 0)
    private val habitTitle = inputData.getString(HABIT_TITLE)

    override fun doWork(): Result {
        val prefManager =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val shouldNotify =
            prefManager.getBoolean(applicationContext.getString(R.string.pref_key_notify), false)

        //TODO 12 : If notification preference on, show notification with pending intent
        if (shouldNotify) {
            val taskBulder = TaskStackBuilder.create(ctx)
            taskBulder.addParentStack(CountDownActivity::class.java)
            taskBulder.addNextIntent(Intent(ctx, HabitListActivity::class.java))

            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            // Create a notification channel (required for Android Oreo and above)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = NOTIFICATION_CHANNEL_ID
                val channelName = NOTIF_UNIQUE_WORK
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, channelName, importance)
                notificationManager.createNotificationChannel(channel)
            }

            // Create the notification
            val notificationBuilder = NotificationCompat.Builder(ctx, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(habitTitle)
                    .setContentText(ctx.getString(R.string.notify_content))
                    .setSmallIcon(R.drawable.ic_notifications)
                    .setColor(ContextCompat.getColor(ctx, android.R.color.transparent))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)


            // Create a pending intent for the notification
            val intent = Intent(applicationContext, DetailHabitActivity::class.java).putExtra(HABIT_ID, habitId)
            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            notificationBuilder.setContentIntent(pendingIntent)

            // Show the notification
            val notification = notificationBuilder.build()
            notificationManager.notify(habitId, notification)
        }

        return Result.success()
    }

}
