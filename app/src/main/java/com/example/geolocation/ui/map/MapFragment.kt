package com.example.geolocation.ui.map

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.geolocation.*
import com.example.geolocation.R
import com.example.geolocation.databinding.FragmentMapBinding
import com.example.geolocation.model.GeolocationModel
import com.example.geolocation.ui.MyLocationListener.MyLocationListener
import com.example.geolocation.ui.MyLocationListener.MyLocationListenerInterface
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback, MyLocationListenerInterface {
    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var myLocationListener: MyLocationListener
    private lateinit var preferences: SharedPreferences

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

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment? as SupportMapFragment
        mapFragment.getMapAsync(this)
        init()

    }

    private fun init(){
        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        myLocationListener = MyLocationListener()
        myLocationListener.setMyLocationListenerInterface(this)
    }

    private fun addInformationToDatabase(latitude: String, longitude:String) {
        val viewModel = ViewModelProvider(this)[MapViewModel::class.java]
        viewModel.initDatabase()
        viewModel.insert(
            GeolocationModel(
                title = "New Point",
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                mMap.isMyLocationEnabled = true

                val sampleRate: Long = preferences.getLong(APP_PREFERENCES_MINUTES, 1L)
                val accuracy: Float = preferences.getFloat(APP_PREFERENCES_METRES, 10.0f)

                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    sampleRate,
                    accuracy,
                    myLocationListener
                )

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
                    }
                }
            }
        }

        binding.buttonAdd.setOnClickListener {
            val  mUpCameraPosition = mMap.cameraPosition
            val country = LatLng (mUpCameraPosition.target.latitude, mUpCameraPosition.target.longitude)
            mMap.addMarker(MarkerOptions().position(country).title("New point"))

            val latitude = country.latitude.toString()
            val longitude = country.longitude.toString()

            addInformationToDatabase(latitude, longitude)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}