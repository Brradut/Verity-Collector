package com.example.veritycollector.services

import android.Manifest
import android.app.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import com.example.veritycollector.model.PPIEntry
import com.example.veritycollector.model.RREntry
import com.example.veritycollector.repository.PPIEntryRepository
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiCallback
import com.polar.sdk.api.PolarBleApiDefaultImpl.defaultImplementation
import com.polar.sdk.api.errors.PolarInvalidArgument
import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarHrData
import com.polar.sdk.api.model.PolarOhrPPIData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.*
import java.util.*

class HRService: Service() {

    private val job = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.Main + job)

    private val TAG = "HR_SERVICE"
    private var sentSuccess = false
    private var sentSuccess2 = false


    private var notificationManager: NotificationManager? = null
    private val NOTIFICATION_CHANNEL_ID = "MoodWheel_PERMANENT"
    private var deviceId : String = ""
    private var deviceId2: String = ""
    private val binder: IBinder =LocalBinder()

    private lateinit var api: PolarBleApi
    private var ppiDisposable: Disposable? = null


    //functions needed for foreground service
    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Real time HR"
            val descriptionText = "Notification for recording heart rate"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
                vibrationPattern = longArrayOf()
            }
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun createNotification(batteryPercent: Int): Notification {
        createNotificationChannel()
        if(notificationManager == null)
            notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        var builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Verity Collector")
            .setContentText("Verity Collector is running\nHeart rate is being measured Battery level: ${batteryPercent}%")
            .setSmallIcon(androidx.core.R.drawable.notification_icon_background)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)
        return builder.build()
    }

    //PPI streaming functions
    fun streamPPI(identifier: String){
        if (ppiDisposable == null){
            ppiDisposable = api.startOhrPPIStreaming(identifier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        polarPPIData: PolarOhrPPIData ->
                        Log.d(TAG, "PPI received: ${polarPPIData.samples.map { x->x.ppi }} ${identifier} ${polarPPIData.timeStamp}")
                        scope.launch { sendPPIToServer(polarPPIData, identifier)}
                    },
                    {
                        error: Throwable ->
                        Log.e(TAG, "PPI stream eror: $error")
                    },
                    {
                        Log.d(TAG, "PPI streaming done")
                    }
                )
        }else{
            ppiDisposable?.dispose()
            ppiDisposable = null
        }
    }

    suspend fun sendPPIToServer(data: PolarOhrPPIData, identifier: String){
        if(sentSuccess == false){
            Toast.makeText(applicationContext, "Verity data is now being recorded", Toast.LENGTH_LONG).show()
            sentSuccess = true
        }
        var tmsp = System.currentTimeMillis()
//        var tmsps = data.samples.map { x-> x.ppi.toLong() } as MutableList
//        var i = tmsps.size - 1
//        while(i >= 0){
//            tmsps[i] = tmsp
//            tmsp = tmsp - data.samples[i].ppi
//            i--
//        }
        var i = 0
        while(i < data.samples.size){
            PPIEntryRepository.addPPIEntry(PPIEntry(data.samples[i].ppi, tmsp, identifier))
            i++
        }
    }

    suspend fun sendRRToServer(data: PolarHrData, identifier: String){
        if(sentSuccess2 == false){
            Toast.makeText(applicationContext, "H10 data is now being recorded", Toast.LENGTH_LONG).show()
            sentSuccess2 = true
        }
        var tmsp = System.currentTimeMillis()

        var i = 0
        while (i < data.rrsMs.size){
            PPIEntryRepository.addRREntry(RREntry(data.rrsMs[i],tmsp, identifier))
        i++
        }

    }


    inner class LocalBinder : Binder() {
        fun getService(): HRService {
            return this@HRService
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        api = defaultImplementation(applicationContext, PolarBleApi.ALL_FEATURES or PolarBleApi.FEATURE_POLAR_SENSOR_STREAMING or PolarBleApi.FEATURE_BATTERY_INFO or PolarBleApi.FEATURE_DEVICE_INFO)
        api.setApiCallback(object: PolarBleApiCallback(){
            override fun blePowerStateChanged(blePowerState: Boolean) {
                Log.d(TAG, "BluetoothStateChanged $blePowerState")
            }

            override fun deviceConnected(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "Device connected " + polarDeviceInfo.deviceId)
                Toast.makeText(applicationContext, "Device ${polarDeviceInfo.deviceId} connected", Toast.LENGTH_SHORT).show()
            }

            override fun deviceConnecting(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "Device connecting ${polarDeviceInfo.deviceId}")
            }

            override fun deviceDisconnected(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "Device disconnected ${polarDeviceInfo.deviceId}")
            }

            override fun streamingFeaturesReady(identifier: String, features: Set<PolarBleApi.DeviceStreamingFeature>) {
                for (feature in features) {
                    Log.d(TAG, "Streaming feature is ready: $feature")
                    if(feature == PolarBleApi.DeviceStreamingFeature.PPI)
                         streamPPI(identifier)
                }
            }

            override fun batteryLevelReceived(identifier: String, batteryLevel: Int) {
                Log.d(TAG, "Battery level $identifier $batteryLevel%")
                notificationManager?.notify(1, createNotification(batteryLevel))
            }

            override fun hrNotificationReceived(identifier: String, data: PolarHrData) {
               Log.d(TAG, "HR data received from $identifier: ${data.rrsMs}")
                scope.launch { sendRRToServer(data, identifier)}
            }
        })

        Log.d(TAG, "onCreate")

        startForeground(1, createNotification(0))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        deviceId = intent?.getStringExtra("deviceId").toString()
        deviceId2 = intent?.getStringExtra("deviceId2").toString()
        try{
            api.connectToDevice(deviceId)
            Log.d(TAG, deviceId)
            Log.d(TAG, deviceId2)
            api.connectToDevice(deviceId2)
            }catch (a: PolarInvalidArgument){
            a.printStackTrace()
            Toast.makeText(applicationContext, "Failed to connect to device $deviceId OR $deviceId2", Toast.LENGTH_SHORT)
        }
        Log.d(TAG, "onStart")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}