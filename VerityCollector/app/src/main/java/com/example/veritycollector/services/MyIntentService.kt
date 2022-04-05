package com.example.veritycollector.services

import android.R
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.veritycollector.activities.MainActivity
import com.example.veritycollector.utils.AlarmUtils
import kotlinx.coroutines.Job

class MyIntentService: IntentService("name") {
    val NOTIFICATION_CHANNEL_ID = "verity_collector_remind_moodwheel"
    var notificationManager: NotificationManager? = null

    override fun onHandleIntent(intent: Intent?) {
        var not = createNotification()
        notificationManager?.notify(2, not)
        AlarmUtils.startAlarms(this)
    }

    private fun createNotificationChannel(ctx: Context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Verity collector reminder modwheel"
            val descriptionText = "Notification to remind the user to complete questionnaire on moodwheel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                vibrationPattern = longArrayOf(0, 500, 100, 500)
                enableVibration(true)

            }
            notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        createNotificationChannel(this)
        if(notificationManager == null)
            notificationManager = getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        var builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("How are you feeling?")
            .setContentText("Please complete the questionnaire: ")
            .setSmallIcon(androidx.core.R.drawable.notification_icon_background)
            .setContentIntent(PendingIntent.getActivity(this, 0, packageManager.getLaunchIntentForPackage("com.psy.ubbcluj.ro.moodwheel"), PendingIntent.FLAG_IMMUTABLE))
            .setAutoCancel(true)
        return builder.build()
    }

}
