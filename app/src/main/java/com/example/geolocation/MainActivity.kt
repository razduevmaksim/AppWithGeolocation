@file:Suppress("DEPRECATION")

package com.example.geolocation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.geolocation.databinding.ActivityMainBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.annotations.Nullable


const val APP_PREFERENCES = "APP_PREFERENCES"
var APP_PREFERENCES_MINUTES = "APP_PREFERENCES_MINUTES"
var APP_PREFERENCES_METRES = "APP_PREFERENCES_METRES"

const val GEOLOCATION_PREFERENCES = "GEOLOCATION_PREFERENCES"
var GEOLOCATION_PREFERENCES_TITLE = "GEOLOCATION_PREFERENCES_TITLE"
var GEOLOCATION_PREFERENCES_LATITUDE = "GEOLOCATION_PREFERENCES_LATITUDE"
var GEOLOCATION_PREFERENCES_LONGITUDE = "GEOLOCATION_PREFERENCES_LONGITUDE"

const val VALIDATION_PREFERENCES = "VALIDATION_PREFERENCES"
var VALIDATION_PREFERENCES_COUNT = "VALIDATION_PREFERENCES_COUNT"

class MainActivity : AppCompatActivity() {
    private var validationGeolocation = 0
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferences: SharedPreferences
    private lateinit var locationRequest: LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_map, R.id.navigation_list, R.id.navigation_settings
            )
        )
        //checkPermissions()
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        preferences = this.getSharedPreferences(VALIDATION_PREFERENCES, Context.MODE_PRIVATE)

        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 2000
        getCurrentLocation()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {
                    getCurrentLocation()
                } else {
                    turnOnGPS()
                }
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                getCurrentLocation()
            }
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (isGPSEnabled()) {
                LocationServices.getFusedLocationProviderClient(this@MainActivity)
                    .requestLocationUpdates(locationRequest, object : LocationCallback() {
                        override fun onLocationResult(@NonNull locationResult: LocationResult) {
                            super.onLocationResult(locationResult)
                            LocationServices.getFusedLocationProviderClient(this@MainActivity)
                                .removeLocationUpdates(this)
//                                if (locationResult != null && locationResult.locations.size > 0) {
//                                    val index = locationResult.locations.size - 1
//                                    val latitude = locationResult.locations[index].latitude
//                                    val longitude = locationResult.locations[index].longitude
//                                    AddressText.setText("Latitude: $latitude\nLongitude: $longitude")
//                                }
                        }
                    }, Looper.getMainLooper())
            } else {
                turnOnGPS()
            }

        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    private fun turnOnGPS() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(
            applicationContext
        )
            .checkLocationSettings(builder.build())
        result.addOnCompleteListener { task ->
            try {
                task.getResult(ApiException::class.java)
                Toast.makeText(this@MainActivity, "GPS is already turned on", Toast.LENGTH_SHORT)
                    .show()
            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvableApiException = e as ResolvableApiException
                        resolvableApiException.startResolutionForResult(this@MainActivity, 2)
                    } catch (ex: SendIntentException) {
                        ex.printStackTrace()
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
                }
            }
        }
    }

    private fun isGPSEnabled(): Boolean {
        var locationManager: LocationManager? = null
        if (locationManager == null) {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        }
        val isEnabled: Boolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (isEnabled){
            validationGeolocation = preferences.getInt(VALIDATION_PREFERENCES_COUNT, 0)
            if (validationGeolocation == 0) {
                validationGeolocation++
                val editor = preferences.edit()
                editor.putInt(VALIDATION_PREFERENCES_COUNT, validationGeolocation)
                editor.apply()
                recreate()
            }
        }
        return isEnabled
    }
}