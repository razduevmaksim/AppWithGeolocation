package com.example.geolocation.ui.MyLocationListener
import android.location.Location
import android.location.LocationListener
import com.google.android.gms.maps.LocationSource

class MyLocationListener : LocationListener {
    private lateinit var myLocationListenerInterface: MyLocationListenerInterface

    override fun onLocationChanged(location: Location) {
        myLocationListenerInterface.OnLocationChanged(location)
    }

    fun setMyLocationListenerInterface(myLocationListenerInterface: MyLocationListenerInterface){
        this.myLocationListenerInterface = myLocationListenerInterface
    }

}