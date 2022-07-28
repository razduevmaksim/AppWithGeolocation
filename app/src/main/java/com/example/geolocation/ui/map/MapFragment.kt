@file:Suppress("DEPRECATION")

package com.example.geolocation.ui.map

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.geolocation.*
import com.example.geolocation.databinding.FragmentMapBinding
import com.example.geolocation.model.GeolocationModel
import com.example.geolocation.ui.myLocationListener.MyLocationListener
import com.example.geolocation.ui.myLocationListener.MyLocationListenerInterface
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback, MyLocationListenerInterface {
    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var myLocationListener: MyLocationListener
    private lateinit var preferences: SharedPreferences
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val channelId = "CHANNEL_ID"
    private val channelName = "CHANNEL_NAME"
    private val notificationId = 0
    private lateinit var title:String
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMapBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        init()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment? as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun init(){
        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        myLocationListener = MyLocationListener()
        myLocationListener.setMyLocationListenerInterface(this)
    }

    private fun addInformationToDatabase(title:String, latitude: String, longitude:String) {
        val viewModel = ViewModelProvider(this)[MapViewModel::class.java]
        viewModel.initDatabase()
        viewModel.insert(
            GeolocationModel(
                title = title,
                latitude = latitude,
                longitude = longitude
            )
        ) {}
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        preferences = this.requireActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        if(context?.let { ActivityCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION) } == PackageManager.PERMISSION_GRANTED
            && context?.let { ActivityCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_COARSE_LOCATION) } == PackageManager.PERMISSION_GRANTED) {
            mMap.clear()

            mMap.isMyLocationEnabled = true

            val sampleRate: Long = preferences.getLong(APP_PREFERENCES_MINUTES, 1L)
            val accuracy: Float = preferences.getFloat(APP_PREFERENCES_METRES, 10.0f)

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                sampleRate,
                accuracy,
                myLocationListener
            )

            val locationProvider = LocationManager.NETWORK_PROVIDER
            val lastKnownLocation =
                locationManager.getLastKnownLocation(locationProvider)
            val userLatitude = lastKnownLocation!!.latitude
            val userLongitude = lastKnownLocation.longitude

            val mapViewModel =
                ViewModelProvider(this)[MapViewModel::class.java]

            mapViewModel.initDatabase()
            mapViewModel.getAll().observe(viewLifecycleOwner) { listGeolocation ->
                listGeolocation.forEach { itemList ->
                    val title = itemList.title
                    val latitude = itemList.latitude.toDouble()
                    val longitude = itemList.longitude.toDouble()
                    val country = LatLng(latitude, longitude)

                    mMap.addMarker(MarkerOptions().position(country).title(title))

                    val currentLocation  = LatLng(userLatitude, userLongitude)

                    if(getDistance(country, currentLocation)<=500){
                        showNotification(title)
                        preferences = this.requireActivity().getSharedPreferences(GEOLOCATION_PREFERENCES, Context.MODE_PRIVATE)
                        val editor = preferences.edit()
                        editor.putString(GEOLOCATION_PREFERENCES_TITLE, title)
                        editor.putFloat(GEOLOCATION_PREFERENCES_LATITUDE, latitude.toFloat())
                        editor.putFloat(GEOLOCATION_PREFERENCES_LONGITUDE, longitude.toFloat())
                        editor.apply()
                    }
                }
            }
        }
        val accuracy: Float = preferences.getFloat(APP_PREFERENCES_METRES, 10.0f)
        val accuracyInInt = accuracy.toInt()

        binding.buttonAdd.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Enter Data")
            builder.setMessage("Enter your title")
            val editTextDialog = EditText(context)
            builder.setView(editTextDialog)
            builder.setNegativeButton("Cancel"){ dialog, _ ->
                dialog.cancel()
            }
            builder.setPositiveButton("Apply"){ _, _ ->
                title = if (editTextDialog.text.toString() == ""){
                    "New Point"
                } else {
                    editTextDialog.text.toString()
                }

                val mapViewModel =
                    ViewModelProvider(this)[MapViewModel::class.java]

                var validationAccuracy = true

                val mUpCameraPosition = mMap.cameraPosition
                val country = LatLng (mUpCameraPosition.target.latitude, mUpCameraPosition.target.longitude)

                mapViewModel.initDatabase()
                mapViewModel.getAll().observe(viewLifecycleOwner) { listGeolocation ->
                    listGeolocation.forEach { itemList ->
                        val pointLatitude = itemList.latitude.toDouble()
                        val pointLongitude = itemList.longitude.toDouble()
                        val pointCountry = LatLng(pointLatitude, pointLongitude)

                        if(getDistance(country, pointCountry)<=accuracyInInt){
                            validationAccuracy = false
                        }
                    }
                    if (validationAccuracy) {
                        mMap.addMarker(MarkerOptions().position(country).title(title))

                        val latitude = country.latitude.toString()
                        val longitude = country.longitude.toString()
                        addInformationToDatabase(title, latitude, longitude)
                    }
                }


            }
            builder.show()
        }
    }

    private fun showNotification(title:String){
        createNotificationChannel()

        val intent = Intent(context, NotificationActivity::class.java)

        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notificationCompat = context?.let {
            NotificationCompat.Builder(it, channelId)
                .setSmallIcon(R.drawable.ic_baseline_navigation_24)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle("Вы приближаетесь к точке $title")
                .setContentText("Нажмите на уведомление, чтобы перейти на страницу с меткой и вашей текущей позицией")
                .setContentIntent(pendingIntent)
                .build()
        }
        val notificationManager = context?.let { NotificationManagerCompat.from(it) }

        if (notificationCompat != null) {
            notificationManager?.notify(notificationId, notificationCompat)
        }
    }

    private fun createNotificationChannel(){
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
            lightColor = Color.BLUE
            enableLights(true)
        }

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun getDistance(LatLng1: LatLng, LatLng2: LatLng): Double {
        val locationA = Location("A")
        locationA.latitude = LatLng1.latitude
        locationA.longitude = LatLng1.longitude

        val locationB = Location("B")
        locationB.latitude = LatLng2.latitude
        locationB.longitude = LatLng2.longitude

        return locationA.distanceTo(locationB).toDouble()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}