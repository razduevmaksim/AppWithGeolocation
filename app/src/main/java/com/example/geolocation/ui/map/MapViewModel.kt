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

    private val _text = MutableLiveData<String>().apply {
        value = "This is map Fragment"
    }
    val text: LiveData<String> = _text

    fun insert(geolocationModel: GeolocationModel, onSuccess:() -> Unit) =
        viewModelScope.launch (Dispatchers.IO) {
            repository.insert(geolocationModel) {
                onSuccess()
            }
        }
}