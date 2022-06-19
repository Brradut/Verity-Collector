package com.example.veritycollector.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat

class SchedulerService: Service() {

    var notificationManager: NotificationManager? = null
    override fun onBind(intent: Intent?): IBinder? {
       return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("TAG", "Scheduler started")
        startForeground(2, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TAG", "on start command")
        return super.onStartCommand(intent, flags, startId)
    }

     private fun createNotificationChannel(ctx: Context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "App waker"
            val descriptionText = "keeps app awake"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("ongoing_notification_notification_service", name, importance).apply {
                description = descriptionText
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            }
            notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        createNotificationChannel(this)
        if(notificationManager == null)
            notificationManager = getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        var builder = NotificationCompat.Builder(this, "ongoing_notification_notification_service")
            .setContentTitle("Verity Collector Notifications")
            .setContentText("Service for Notifications")
            .setSmallIcon(androidx.core.R.drawable.notification_icon_background)
            .setContentIntent(PendingIntent.getActivity(this, 0, packageManager.getLaunchIntentForPackage("com.example.veritycollector"), PendingIntent.FLAG_IMMUTABLE))
            .setOngoing(true)
        return builder.build()
    }

}