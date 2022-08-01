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
    private lateinit var title: String
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

        //инициализация LocationManager и MyLocationListener
        init()

        //подключение карты
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment? as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    //инициализация LocationManager и MyLocationListener
    private fun init() {
        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        myLocationListener = MyLocationListener()
        myLocationListener.setMyLocationListenerInterface(this)
    }

    //добавление данных в room
    private fun addInformationToDatabase(title: String, latitude: String, longitude: String) {
        val viewModel = ViewModelProvider(this)[MapViewModel::class.java]

        //инициализация БД
        viewModel.initDatabase()

        //добавление данных в room
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

        val mapViewModel =
            ViewModelProvider(this)[MapViewModel::class.java]

        preferences =
            this.requireActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

        //проверка на PERMISSION для получения текущего местоположения
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            } == PackageManager.PERMISSION_GRANTED
            && context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } == PackageManager.PERMISSION_GRANTED) {

            //очищение карты
            mMap.clear()

            //отображение текущего местоположения
            mMap.isMyLocationEnabled = true

            //время обновления и точность данных
            val sampleRate: Long = preferences.getLong(APP_PREFERENCES_MINUTES, 1L)
            val accuracy: Float = preferences.getFloat(APP_PREFERENCES_METRES, 10.0f)

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                sampleRate,
                accuracy,
                myLocationListener
            )

            //получение текущего местоположения и данных(latitude and longitude)
            val locationProvider = LocationManager.NETWORK_PROVIDER
            val lastKnownLocation =
                locationManager.getLastKnownLocation(locationProvider)
            if (lastKnownLocation != null) {
                val userLatitude = lastKnownLocation.latitude
                val userLongitude = lastKnownLocation.longitude

                //инициализация БД
                mapViewModel.initDatabase()

                //получение всех данных из БД
                mapViewModel.getAll().observe(viewLifecycleOwner) { listGeolocation ->
                    listGeolocation.forEach { itemList ->
                        val title = itemList.title
                        val latitude = itemList.latitude.toDouble()
                        val longitude = itemList.longitude.toDouble()
                        val country = LatLng(latitude, longitude)

                        //добавление данных на карту
                        mMap.addMarker(MarkerOptions().position(country).title(title))

                        val currentLocation = LatLng(userLatitude, userLongitude)

                        //подсчет расстояния между точкой и текущим местоположением
                        if (getDistance(country, currentLocation) <= 500) {
                            //уведомление о приближении к точке
                            showNotification(title)

                            //запись в SharedPreferences
                            preferences = this.requireActivity()
                                .getSharedPreferences(GEOLOCATION_PREFERENCES, Context.MODE_PRIVATE)
                            val editor = preferences.edit()
                            editor.putString(GEOLOCATION_PREFERENCES_TITLE, title)
                            editor.putFloat(GEOLOCATION_PREFERENCES_LATITUDE, latitude.toFloat())
                            editor.putFloat(GEOLOCATION_PREFERENCES_LONGITUDE, longitude.toFloat())
                            editor.apply()
                        }
                    }
                }
            }
        }
        val accuracy: Float = preferences.getFloat(APP_PREFERENCES_METRES, 10.0f)
        val accuracyInInt = accuracy.toInt()

        //события при нажатии на кнопку добавления
        binding.buttonAdd.setOnClickListener {
            //вызов диалогового окна при клике на кнопку
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Добавление точки")
            builder.setMessage("Введите название точки")
            val editTextDialog = EditText(context)
            builder.setView(editTextDialog)

            //события при клике на "отмена". Выход из диалогового окна
            builder.setNegativeButton("Отмена") { dialog, _ ->
                dialog.cancel()
            }

            //события при клике на "подтвердить". Добавление данных в room
            builder.setPositiveButton("Подтвердить") { _, _ ->
                title = if (editTextDialog.text.toString() == "") {
                    "New Point"
                } else {
                    editTextDialog.text.toString()
                }

                var validationAccuracy = true

                val mUpCameraPosition = mMap.cameraPosition
                val country =
                    LatLng(mUpCameraPosition.target.latitude, mUpCameraPosition.target.longitude)

                //инициализация БД
                mapViewModel.initDatabase()
                //получение всех данных из room
                mapViewModel.getAll().observe(viewLifecycleOwner) { listGeolocation ->
                    listGeolocation.forEach { itemList ->
                        //проверка на добавление точки в соответствии с точностью трекинга геолокации
                        val pointLatitude = itemList.latitude.toDouble()
                        val pointLongitude = itemList.longitude.toDouble()
                        val pointCountry = LatLng(pointLatitude, pointLongitude)

                        if (getDistance(country, pointCountry) <= accuracyInInt) {
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

    //отображение уведомления
    private fun showNotification(title: String) {
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

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            lightColor = Color.BLUE
            enableLights(true)
        }

        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    //получение расстояния между точками
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