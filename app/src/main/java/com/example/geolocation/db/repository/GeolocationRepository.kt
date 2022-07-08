package com.example.geolocation.db.repository

import androidx.lifecycle.LiveData
import com.example.geolocation.model.GeolocationModel

interface GeolocationRepository {
    val allGeolocations: LiveData<List<GeolocationModel>>

    suspend fun insert(geolocationModel: GeolocationModel, onSuccess:() -> Unit)
    suspend fun delete(geolocationModel: GeolocationModel, onSuccess:() -> Unit)
    suspend fun update(geolocationModel: GeolocationModel, onSuccess:() -> Unit)
}