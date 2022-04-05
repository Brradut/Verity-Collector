package com.example.veritycollector.activities

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.example.veritycollector.ui.theme.Purple200
import com.example.veritycollector.ui.theme.VerityCollectorTheme
import com.example.veritycollector.utils.AlarmUtils
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.lang.NumberFormatException
import java.lang.ref.PhantomReference
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.*

class NotificationSettingsActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VerityCollectorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    var notificationEnabled = PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean("notificationEnabled", false)
                    var startTime = PreferenceManager.getDefaultSharedPreferences(applicationContext).getString("startTime", "07:00")
                    var endTime = PreferenceManager.getDefaultSharedPreferences(applicationContext).getString("endTime", "22:00")
                    var subintervalIndex = PreferenceManager.getDefaultSharedPreferences(applicationContext).getInt("subintervalIndex", 0)
                    var frequency = PreferenceManager.getDefaultSharedPreferences(applicationContext).getInt("frequency", 0)
                    var randomNotifications = PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean("randomNotifications", false)
                    var enabled by remember { mutableStateOf(notificationEnabled) }
                    Column(verticalArrangement = Arrangement.spacedBy(60.dp), horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.offset(0.dp, 60.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row() {
                                Text(text = "Notification Interval:", fontSize = 25.sp)
                            }
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if(enabled){
                                    Text(text = "Notifications from $startTime to $endTime", fontSize = 25.sp)
                                }else{
                                 PickTime("Start time: ", time=startTime!!, prefName="startTime")
                                 PickTime("End time: ", time = endTime!!, prefName="endTime")
                                }
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Subinterval:", fontSize = 25.sp, color = Color.White)
                            if(enabled) {
                                Text(text = "$subintervalIndex", fontSize = 25.sp)
                            }else{
                                DropdownDemo(subintervalIndex)
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row() {
                                Text(text = "Frequency:", fontSize = 25.sp)
                            }
                            Row() {
                                val textState = remember { mutableStateOf(frequency.toString()) }

                                if(enabled){
                                    Text(text = "$frequency", fontSize = 25.sp)
                                }else{
                                OutlinedTextField(
                                    modifier = Modifier.width(250.dp),
                                    value = textState.value,
                                    onValueChange = {
                                        textState.value = it
                                        try {
                                            PreferenceManager.getDefaultSharedPreferences(
                                                applicationContext
                                            ).edit().putInt("frequency", Integer.parseInt(it))
                                                .apply()
                                        }catch (ex:NumberFormatException){

                                        }
                                                    },

                                        label = {
                                        Text(
                                            text = "Notification Frequency"
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    )
                                )
                            }
                        }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Random notifications? ", fontSize = 25.sp)
                            val chk = remember { mutableStateOf(randomNotifications) }
                            if(enabled){
                                Text(text = "$randomNotifications", fontSize = 25.sp)
                            }else{
                                Checkbox(checked = chk.value, onCheckedChange = { chk.value = it ; PreferenceManager.getDefaultSharedPreferences(applicationContext).edit().putBoolean("randomNotifications", it).apply()})
                            }
                        }

                        Row() {
                            val btnText = remember { mutableStateOf("") }
                            if (!enabled)
                                btnText.value = "Enable Notifications"
                            else
                                btnText.value = "Disable Notifications"
                            Button(onClick = {
                                if (enabled) {
                                    btnText.value = "Enable Notifications"
                                    notificationEnabled = false
                                    enabled = false
                                    AlarmUtils.stopAlarms(applicationContext)
                                } else {
                                    btnText.value = "Disable Notifications"
                                    notificationEnabled = true
                                    enabled = true
                                    AlarmUtils.setupAlarms(startTime!!, endTime!!, frequency, subintervalIndex, randomNotifications)
                                    AlarmUtils.startAlarms(applicationContext)
                                }
                                PreferenceManager.getDefaultSharedPreferences(applicationContext).edit().putBoolean("notificationEnabled", notificationEnabled).apply()
                            }) {
                                Text(text = btnText.value, fontSize = 25.sp)
                            }
                        }
                    }
                }
            }
        }
    }
    @Composable
    fun DropdownDemo(subintervalIndex: Int) {
        var expanded by remember { mutableStateOf(false) }
        val items = listOf("15 min", "30 min", "1 hour", "2 hours", "4 hours", "day")
        var selectedIndex by remember { mutableStateOf(subintervalIndex) }
        Box(modifier = Modifier.size(200.dp, 60.dp), contentAlignment = Alignment.Center) {
            Text(text=items[selectedIndex],modifier = Modifier
                .clickable(onClick = { expanded = true })
                .background(
                    Purple200
                )
                .fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 25.sp, color = Color.Black)
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(200.dp)
                    .background(
                        Purple200
                    )
            ) {
                items.forEachIndexed { index, s ->
                    DropdownMenuItem(onClick = {
                        selectedIndex = index
                        expanded = false
                        PreferenceManager.getDefaultSharedPreferences(applicationContext).edit().putInt("subintervalIndex", selectedIndex).apply()
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text(text = s, textAlign = TextAlign.Center, fontSize = 25.sp, color = Color.Black)
                    }
                }
            }
        }
    }
    @Composable
    fun PickTime(componentText:String, time:String, prefName:String){
        Row( modifier = Modifier.size(200.dp, 100.dp), horizontalArrangement = Arrangement.Center) {
            val dialogState = rememberMaterialDialogState()
            var timeString = remember { mutableStateOf(time) }
            MaterialDialog(
                dialogState = dialogState,
                buttons = {
                    positiveButton("Ok")
                    negativeButton("Cancel")
                }
            ) {
                timepicker { time ->
                    timeString.value = time.toString()
                    PreferenceManager.getDefaultSharedPreferences(applicationContext).edit().putString(prefName, timeString.value).apply()
                }
            }
            Button(onClick = { dialogState.show()}) {
                Text(text = componentText + timeString.value, fontSize = 25.sp)
            }
        }
    }
}

