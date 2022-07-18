package com.example.geolocation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.geolocation.databinding.ActivityMainBinding
import com.example.geolocation.model.GeolocationModel

const val APP_PREFERENCES = "APP_PREFERENCES"
var APP_PREFERENCES_MINUTES = "APP_PREFERENCES_MINUTES"
var APP_PREFERENCES_METRES = "APP_PREFERENCES_METRES"
const val GEOLOCATION_PREFERENCES = "GEOLOCATION_PREFERENCES"
var GEOLOCATION_PREFERENCES_TITLE = "GEOLOCATION_PREFERENCES_TITLE"
var GEOLOCATION_PREFERENCES_LATITUDE = "GEOLOCATION_PREFERENCES_LATITUDE"
var GEOLOCATION_PREFERENCES_LONGITUDE = "GEOLOCATION_PREFERENCES_LONGITUDE"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

}