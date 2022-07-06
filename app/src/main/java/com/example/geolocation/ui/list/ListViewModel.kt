package com.example.geolocation.ui.list

import android.app.Application
import androidx.lifecycle.*
import com.example.geolocation.db.GeolocationDatabase
import com.example.geolocation.db.repository.GeolocationRealisation
import com.example.geolocation.db.repository.GeolocationRepository
import com.example.geolocation.model.GeolocationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListViewModel(application: Application) : AndroidViewModel(application){

    lateinit var repository : GeolocationRepository
    val context = application

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

    fun insert(geolocationModel: GeolocationModel, onSuccess:() -> Unit) =
        viewModelScope.launch (Dispatchers.IO) {
            repository.insert(geolocationModel) {
                onSuccess()
            }
        }
}