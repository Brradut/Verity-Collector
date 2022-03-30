package com.example.veritycollector.utils

import java.io.File
import java.io.FileOutputStream
import java.util.*

class PropertyUtils {
    companion object{
        val config = this.javaClass.classLoader.getResourceAsStream("config.properties")
        val prefs = this.javaClass.classLoader.getResourceAsStream("prefs.properties")
        val configProperties = Properties()
        val prefsProperties = Properties()

        init{
            configProperties.load(config)
            prefsProperties.load(prefs)
        }
        fun setNotificationInterval(){
            prefsProperties.setProperty("timeInterval", "3-5")
            File("AJUTOR.txt").writeText("Ajutor")
            prefsProperties.store(FileOutputStream(this.javaClass.classLoader.getResource("prefs.properties").path),"")
        }

        fun getRESTEndpoint():String{
            return configProperties.getProperty("url")
        }

    }
}