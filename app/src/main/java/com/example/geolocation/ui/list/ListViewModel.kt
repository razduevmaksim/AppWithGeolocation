package com.example.geolocation.ui.list

import android.app.Application
import androidx.lifecycle.*
import com.example.geolocation.db.GeolocationDatabase
import com.example.geolocation.db.repository.GeolocationRealisation
import com.example.geolocation.db.repository.GeolocationRepository
import com.example.geolocation.model.GeolocationModel

class ListViewModel(application: Application) : AndroidViewModel(application){

    private lateinit var repository : GeolocationRepository
    private val context = application

    private val _text = MutableLiveData<String>().apply {
        value = "This is list Fragment"
    }
    val text: LiveData<String> = _text

    fun initDatabase(){
        val daoGeolocation = GeolocationDatabase.getInstance(context).getGeolocationDao()
        repository = GeolocationRealisation(daoGeolocation)
    }

    fun getAll():LiveData<List<GeolocationModel>>{
        return repository.allGeolocations
    }

}