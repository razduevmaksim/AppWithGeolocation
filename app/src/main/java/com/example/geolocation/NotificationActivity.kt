package com.example.geolocation

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.geolocation.databinding.ActivityNotificationBinding


class NotificationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityNotificationBinding
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapNotification) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        preferences = this.getSharedPreferences(GEOLOCATION_PREFERENCES, Context.MODE_PRIVATE)

        val title = preferences.getString(GEOLOCATION_PREFERENCES_TITLE, "New Point")
        val latitude = preferences.getFloat(GEOLOCATION_PREFERENCES_LATITUDE, 0.0f)
        val longitude = preferences.getFloat(GEOLOCATION_PREFERENCES_LONGITUDE, 0.0f)

        val country = LatLng(latitude.toDouble(), longitude.toDouble())
        mMap.addMarker(MarkerOptions().position(country).title(title))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(country))
    }
}