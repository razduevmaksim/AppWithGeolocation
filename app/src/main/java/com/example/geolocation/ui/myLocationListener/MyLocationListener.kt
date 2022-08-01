package com.example.geolocation.ui.myLocationListener

import android.location.Location
import android.location.LocationListener

class MyLocationListener : LocationListener {
    private lateinit var myLocationListenerInterface: MyLocationListenerInterface

    override fun onLocationChanged(location: Location) {
        myLocationListenerInterface.myOnLocationChanged(location)
    }

    fun setMyLocationListenerInterface(myLocationListenerInterface: MyLocationListenerInterface){
        this.myLocationListenerInterface = myLocationListenerInterface
    }

}