package com.example.veritycollector.activities

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.example.veritycollector.R
import com.example.veritycollector.services.HRService
import com.example.veritycollector.ui.theme.VerityCollectorTheme
import com.example.veritycollector.utils.PermissionsUtils
import java.security.Permission

class BtSettingsActivity: ComponentActivity() {

    private val TAG = "IDK"
    private val PERMISSION_REQUEST_CODE = 1

    var serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }


    private val bluetoothOnActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode != Activity.RESULT_OK) {
            Log.w(TAG, "Bluetooth off")
        }
    }

    private fun checkBT() {
        PermissionsUtils.requestLocationPermission(this)

        val btManager = applicationContext.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter: BluetoothAdapter? = btManager.adapter
        if (bluetoothAdapter == null) {
            Toast.makeText(applicationContext, "Device doesn't support Bluetooth", Toast.LENGTH_LONG)
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            bluetoothOnActivityResultLauncher.launch(enableBtIntent)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PermissionsUtils.requestBTPermission(this)
                    requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT), PERMISSION_REQUEST_CODE)
                } else {
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_REQUEST_CODE)
                }
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_REQUEST_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            for (index in 0..grantResults.lastIndex) {
                Log.d("IDK", "${grantResults[index]}")
                if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                    Log.w(TAG, "Needed permissions are missing ${permissions[index]} ")
                    Toast.makeText(applicationContext, "Needed permissions are missing", Toast.LENGTH_LONG).show()
                    return
                }
            }
            Log.d(TAG, "Needed permissions are granted")
        }

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = this as Context
        checkBT()
        setContent {
            VerityCollectorTheme {
                // A surface container using the 'background' color from the theme
                androidx.compose.material.Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background){
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(60.dp), modifier = Modifier.offset(0.dp, 60.dp)) {
                        val textState = remember { mutableStateOf(PreferenceManager.getDefaultSharedPreferences(applicationContext).getString("device_id", "")) }
                        val textState2 =  remember { mutableStateOf(PreferenceManager.getDefaultSharedPreferences(applicationContext).getString("device_id_2", "")) }
                        Column() {
                            Text(text = "Device ID (Verity Sense):", fontSize = 25.sp)
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                value = textState.value!!,
                                onValueChange = {
                                    textState.value = it
                                    PreferenceManager.getDefaultSharedPreferences(applicationContext).edit().putString("device_id", it).apply()
                                                },
                                label = {
                                    Text(
                                        text = "Device ID"
                                    )
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Done
                                )
                            )
                        }
                        Column(){
                            Text(text = "Device ID (H10):", fontSize = 25.sp)
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                value = textState2.value!!,
                                onValueChange = {
                                    textState2.value = it
                                    PreferenceManager.getDefaultSharedPreferences(applicationContext).edit().putString("device_id_2", it).apply()
                                },
                                label = {
                                    Text(
                                        text = "Device ID"
                                    )
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Done
                                )
                            )
                        }
                        Button(onClick = {
                            checkBT()
                            val serviceIntent = Intent(applicationContext, HRService::class.java)
                            stopService(serviceIntent)

                            serviceIntent.putExtra("deviceId", textState.value)
                            serviceIntent.putExtra("deviceId2", textState2.value)
                            startService(serviceIntent)
                            val success = applicationContext.bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE)
                            Log.d("HR_SERVICE", "bindService returned: $success")

                        }) {
                            Text(text = "Enable HR Collection", fontSize = 25.sp)
                        }
                    }
                }
            }
        }
    }
}