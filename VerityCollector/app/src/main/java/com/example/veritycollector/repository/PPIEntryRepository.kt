package com.example.veritycollector.repository

import android.util.Log
import com.example.veritycollector.model.PPIEntry
import com.example.veritycollector.model.RREntry
import com.example.veritycollector.services.ServiceBuilder

class PPIEntryRepository {
    companion object {
    val service = ServiceBuilder.create()
    suspend fun addPPIEntry(ppi_entry: PPIEntry){
        try {
            Log.d("REST", "Trying to add verity entry")
           val m = service.addPPIEntry(ppi_entry)
            Log.d("REST", "Added verity entry, $m")
        }catch (ex: Exception){
            Log.d("REST", "Failed to verity add entry, ${ex.message}")
        }
    }
    suspend fun addRREntry(rr_entry: RREntry){
        try {
            Log.d("REST", "Trying to add H10 entry")
            val m = service.addRREntry(rr_entry)
            Log.d("REST", "Added H10 entry, $m")
        }catch (ex: Exception){
            Log.d("REST", "Failed to add H10 entry, ${ex.message}")
        }
    }

}
}