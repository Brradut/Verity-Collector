package com.example.veritycollector.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.veritycollector.services.SchedulerService

class BootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Tag", "WE BOOTIN YO")
        //val serviceIntent = Intent(context, SchedulerService::class.java)
        //context?.startForegroundService(serviceIntent)
    }
}