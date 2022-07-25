package com.example.geolocation

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.geolocation.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

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
        checkPermissions()
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        preferences = this.getSharedPreferences(VALIDATION_PREFERENCES, Context.MODE_PRIVATE)

    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults[0] == RESULT_OK){
            checkPermissions()
        }
        else {
            validationGeolocation = preferences.getInt(VALIDATION_PREFERENCES_COUNT,0)
            if (validationGeolocation == 0) {
                validationGeolocation++
                val editor = preferences.edit()
                editor.putInt(VALIDATION_PREFERENCES_COUNT, validationGeolocation)
                editor.apply()
                recreate()
            }
        }
    }

    private fun checkPermissions(){
        val permissions = arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION)
        if(this.let { ActivityCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION) } == PackageManager.PERMISSION_GRANTED
            && this.let { ActivityCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_COARSE_LOCATION) } == PackageManager.PERMISSION_GRANTED){
            requestPermissions(permissions, 1)
        }
        if(this.let { ActivityCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION) } != PackageManager.PERMISSION_GRANTED
            && this.let { ActivityCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_COARSE_LOCATION) } != PackageManager.PERMISSION_GRANTED){
            requestPermissions(permissions, 1)
        }
    }
}