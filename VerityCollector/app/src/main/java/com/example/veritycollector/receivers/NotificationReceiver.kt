package com.example.veritycollector.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.veritycollector.services.MyIntentService

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        var intent = Intent(context, MyIntentService::class.java)
        Log.d("TAG", "Should show alarm here???")
        context?.startService(intent)
    }
}