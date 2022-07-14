package com.example.geolocation.ui.MyLocationListener
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import com.google.android.gms.maps.LocationSource

class MyLocationListener : LocationListener {
    private lateinit var myLocationListenerInterface: MyLocationListenerInterface

    override fun onLocationChanged(location: Location) {
        myLocationListenerInterface.myOnLocationChanged(location)
    }

    override fun onProviderDisabled(provider: String) {
        super.onProviderDisabled(provider)
    }
    override fun onProviderEnabled(provider: String) {
        super.onProviderEnabled(provider)
    }
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        super.onStatusChanged(provider, status, extras)
    }

    fun setMyLocationListenerInterface(myLocationListenerInterface: MyLocationListenerInterface){
        this.myLocationListenerInterface = myLocationListenerInterface
    }

}