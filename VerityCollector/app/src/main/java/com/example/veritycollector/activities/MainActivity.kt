package com.example.veritycollector.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.veritycollector.ui.theme.Purple200
import com.example.veritycollector.ui.theme.Purple500
import com.example.veritycollector.utils.PropertyUtils
import com.example.veritycollector.ui.theme.VerityCollectorTheme
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalTime
import java.time.temporal.TemporalAccessor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = this as Context
        setContent {
            VerityCollectorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(modifier = Modifier.fillMaxSize().offset(0.dp, 60.dp), verticalArrangement = Arrangement.spacedBy(60.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = {
//                            PropertyUtils.setNotificationInterval()
//                            val intent = Intent(context, AddEntryActivity::class.java)
//                            startActivity(intent)
                            val i = packageManager.getLaunchIntentForPackage("com.psy.ubbcluj.ro.moodwheel")
                            startActivity(i)
                        }) {
                            Text(text = "Add Entry", fontSize = 30.sp)
                        }
                        Button(onClick = {
                            val intent = Intent(context, BtSettingsActivity::class.java)
                            startActivity(intent)

                        }) {
                            Text(text = "Bluetooth Settings", fontSize = 30.sp)
                        }
                        Button(onClick = {
                            val intent = Intent(context, NotificationSettingsActivity::class.java)
                            startActivity(intent)
                        }) {
                            Text(text = "Notification Settings", fontSize = 30.sp)
                        }
                    }
                }
            }
        }
    }
}

