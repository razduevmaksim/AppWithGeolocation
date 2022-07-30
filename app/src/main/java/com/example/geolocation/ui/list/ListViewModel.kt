package com.example.geolocation.ui.list

import android.app.Application
import androidx.lifecycle.*
import com.example.geolocation.db.GeolocationDatabase
import com.example.geolocation.db.repository.GeolocationRealisation
import com.example.geolocation.db.repository.GeolocationRepository
import com.example.geolocation.model.GeolocationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var repository: GeolocationRepository
    private val context = application

    //инициализация БД
    fun initDatabase() {
        val daoGeolocation = GeolocationDatabase.getInstance(context).getGeolocationDao()
        repository = GeolocationRealisation(daoGeolocation)
    }

    //получение всех данных из room
    fun getAll(): LiveData<List<GeolocationModel>> {
        return repository.allGeolocations
    }

    //удаление всех данных
    fun deleteAll() =
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
}