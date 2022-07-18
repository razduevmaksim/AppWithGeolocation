@file:Suppress("DEPRECATION")

package com.example.geolocation.ui.myLocationListener

import android.location.Location
import android.location.LocationListener
import android.os.Bundle

class MyLocationListener : LocationListener {
    private lateinit var myLocationListenerInterface: MyLocationListenerInterface

    override fun onLocationChanged(location: Location) {
        myLocationListenerInterface.myOnLocationChanged(location)
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "super.onStatusChanged(provider, status, extras)",
        "android.location.LocationListener"
    )
    )
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        super.onStatusChanged(provider, status, extras)
    }

    fun setMyLocationListenerInterface(myLocationListenerInterface: MyLocationListenerInterface){
        this.myLocationListenerInterface = myLocationListenerInterface
    }

}