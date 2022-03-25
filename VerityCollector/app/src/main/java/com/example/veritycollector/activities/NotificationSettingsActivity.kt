package com.example.veritycollector.activities

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.NumberPicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.example.veritycollector.ui.theme.VerityCollectorTheme

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
                    Column(verticalArrangement = Arrangement.spacedBy(60.dp), horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.offset(0.dp, 60.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally){
                            Row() {
                                Text(text = "Notification Interval:", fontSize = 25.sp)
                            }
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                PickTime()
                                PickTime()
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(){
                                Text(text = "Frequency:", fontSize = 25.sp)
                            }
                            Row(){
                                val textState = remember { mutableStateOf("") }
                                OutlinedTextField(
                                    modifier = Modifier.width(250.dp),
                                    value = textState.value,
                                    onValueChange = { textState.value = it },
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
                        Row(verticalAlignment = Alignment.CenterVertically){
                            Text(text = "Random notifications?", fontSize = 25.sp)
                            val chk = remember{ mutableStateOf(false)}
                            Checkbox(checked = chk.value, onCheckedChange = {chk.value = it})
                        }
                        Row(){
                            val btnText = remember{mutableStateOf("Enable Notifications")}
                            Button(onClick = { if(btnText.value == "Enable Notifications") btnText.value = "Disable Notifications" else btnText.value = "Enable Notifications"}){
                                Text(text = btnText.value, fontSize = 25.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}