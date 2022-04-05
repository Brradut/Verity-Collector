package com.example.veritycollector.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.veritycollector.receivers.NotificationReceiver
import java.time.*
import java.util.*

class AlarmUtils {
    companion object {

        var aux: Long = 0
        val queue: Queue<Long> = LinkedList<Long>()
        var beginTime: Long = 0
        val subintervals = listOf<Long>(900000, 180000, 3600000, 7200000, 14400000, 0)
        var sf /*subinterval/frequency*/: Long = 0
        val percent = 5

        fun startAlarms(ctx: Context){
            var intent = Intent(ctx, NotificationReceiver::class.java)
            var pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            var alarmManager = ctx.getSystemService(Service.ALARM_SERVICE) as AlarmManager
            if (!queue.isEmpty())
                alarmManager.set(AlarmManager.RTC_WAKEUP, queue.poll(), pendingIntent)

        }
        fun stopAlarms(ctx: Context){
            queue.clear()
            var intent = Intent(ctx, NotificationReceiver::class.java)
            var pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            var alarmManager = ctx.getSystemService(Service.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
        }

        fun setupAlarms(
            startTimeString: String,
            endTimeString: String,
            frequency: Int,
            subintervalIndex: Int,
            randomNotifications: Boolean
        ) {
            var startTime: Long = LocalTime.parse(startTimeString).atDate(LocalDate.now())
                .toInstant(ZoneOffset.ofHours(3)).toEpochMilli()
            var endTime: Long = LocalTime.parse(endTimeString).atDate(LocalDate.now())
                .toInstant(ZoneOffset.ofHours(3)).toEpochMilli()
            var currentTime = System.currentTimeMillis()

            queue.clear()

            if (currentTime < startTime)
                beginTime = startTime
            else
                beginTime = currentTime

            var subinterval: Long = 0


            if (subintervalIndex == 5)
                subinterval = endTime - beginTime
            else
                subinterval = subintervals[subintervalIndex]

            sf = subinterval / (frequency.toLong())

            var t = beginTime + sf
            while (t <= endTime) {
                if (randomNotifications) {
                    var x = (-sf / percent..sf / percent).random()
                    if (t + x <= endTime) {
                        queue.add(t + x)
                    }else{
                        queue.add(t)
                    }
                } else{
                    queue.add(t)
                }
                t = t + sf
            }
            for (i in queue) {
                Log.d("Tag", "${LocalDateTime.ofInstant(Instant.ofEpochMilli(i), ZoneId.systemDefault() )}")
            }
        }
    }
}