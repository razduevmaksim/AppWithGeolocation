package com.example.geolocation.ui.MyLocationListener

import android.location.Location
import com.google.android.gms.maps.LocationSource

interface MyLocationListenerInterface {
    public fun OnLocationChanged(location: Location){

    }
}