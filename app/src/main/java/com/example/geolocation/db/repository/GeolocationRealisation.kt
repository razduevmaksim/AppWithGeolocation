package com.example.geolocation.db.repository

import androidx.lifecycle.LiveData
import com.example.geolocation.db.dao.GeolocationDao
import com.example.geolocation.model.GeolocationModel

class GeolocationRealisation(private val geolocationDao: GeolocationDao): GeolocationRepository {
    override val allGeolocations: LiveData<List<GeolocationModel>>
        get() = geolocationDao.getAll()

    override suspend fun insert(geolocationModel: GeolocationModel, onSuccess: () -> Unit) {
        geolocationDao.insert(geolocationModel)
        onSuccess()
    }

    override suspend fun delete(geolocationModel: GeolocationModel, onSuccess: () -> Unit) {

    }

    override suspend fun update(geolocationModel: GeolocationModel, onSuccess: () -> Unit) {

    }
}