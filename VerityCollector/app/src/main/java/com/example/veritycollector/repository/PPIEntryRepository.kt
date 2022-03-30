package com.example.veritycollector.repository

import android.util.Log
import com.example.veritycollector.model.PPIEntry
import com.example.veritycollector.services.ServiceBuilder

class PPIEntryRepository {
    companion object {
    val service = ServiceBuilder.create()
    suspend fun addPPIEntry(ppi_entry: PPIEntry){
        try {
            Log.d("REST", "Trying to add entry")
           val m = service.addPPIEntry(ppi_entry)
            Log.d("REST", "Added entry, $m")
        }catch (ex: Exception){
            Log.d("REST", "Failed to add entry, ${ex.message}")
        }

    }
}
}