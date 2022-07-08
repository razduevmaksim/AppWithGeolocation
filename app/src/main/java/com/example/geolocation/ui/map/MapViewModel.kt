package com.example.geolocation.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geolocation.db.repository.GeolocationRepository
import com.example.geolocation.model.GeolocationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    private lateinit var repository : GeolocationRepository
    fun getAll():LiveData<List<GeolocationModel>>{
        return repository.allGeolocations
    }
}