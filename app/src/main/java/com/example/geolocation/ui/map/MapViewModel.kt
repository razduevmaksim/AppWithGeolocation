package com.example.geolocation.ui.map

import android.app.Application
import androidx.lifecycle.*
import com.example.geolocation.db.GeolocationDatabase
import com.example.geolocation.db.repository.GeolocationRealisation
import com.example.geolocation.db.repository.GeolocationRepository
import com.example.geolocation.model.GeolocationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application

    private lateinit var repository : GeolocationRepository
    fun getAll():LiveData<List<GeolocationModel>>{
        return repository.allGeolocations
    }
    fun initDatabase(){
        val daoGeolocation = GeolocationDatabase.getInstance(context).getGeolocationDao()
        repository = GeolocationRealisation(daoGeolocation)
    }
    fun insert(geolocationModel: GeolocationModel, onSuccess:() -> Unit) =
        viewModelScope.launch (Dispatchers.IO) {
            repository.insert(geolocationModel) {
                onSuccess()
            }
        }

}