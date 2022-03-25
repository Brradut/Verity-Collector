package com.example.veritycollector.activities

import android.inputmethodservice.Keyboard
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.veritycollector.ui.theme.VerityCollectorTheme

class AddEntryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VerityCollectorTheme {
                // A surface container using the 'background' color from the theme
                androidx.compose.material.Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background) {
                    Column() {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            val textState = remember { mutableStateOf("") }
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                value = textState.value,
                                onValueChange = { textState.value = it },
                                label = {
                                    Text(
                                        text = "How are you feeling?"
                                    )
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Done
                                )
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            Text("Interval")
                        }
                        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                            PickTime()
                            PickTime()
                        }
                        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
                            .fillMaxWidth()
                            .offset(0.dp, 60.dp)){
                            Button(onClick = { /*TODO*/ }) {
                               Text(text = "Submit", fontSize = 20.sp)
                            }
                        }
                    }
                }
        }
        }
    }

}