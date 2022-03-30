package com.example.veritycollector.services

import com.example.veritycollector.model.PPIEntry
import com.example.veritycollector.utils.PropertyUtils
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface RESTService{
 @POST("ppi_entry")
 suspend fun addPPIEntry(@Body ppi_entry: PPIEntry):Response<Unit>
}

class ServiceBuilder{
 companion object{
  fun create():RESTService{
   return Retrofit.Builder().baseUrl(PropertyUtils.getRESTEndpoint()).addConverterFactory(
    GsonConverterFactory.create()).build().create(RESTService::class.java)
 }
}
}
