package com.example.geolocation.ui.MyLocationListener

import android.location.Location
import com.google.android.gms.maps.LocationSource

interface MyLocationListenerInterface {
    fun myOnLocationChanged(p0: Location) {
    }
}