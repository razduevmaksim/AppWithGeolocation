package com.example.geolocation.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.geolocation.*
import com.example.geolocation.R
import com.example.geolocation.databinding.FragmentMapBinding
import com.example.geolocation.model.GeolocationModel
import com.example.geolocation.ui.MyLocationListener.MyLocationListener
import com.example.geolocation.ui.MyLocationListener.MyLocationListenerInterface
import com.google.android.gms.maps.*
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback, MyLocationListenerInterface {
    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var lastLocation: Location
    private lateinit var myLocationListener: MyLocationListener
    private lateinit var preferences: SharedPreferences

    private var _binding: FragmentMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mapViewModel =
            ViewModelProvider(this)[MapViewModel::class.java]

        _binding = FragmentMapBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment? as SupportMapFragment
        mapFragment.getMapAsync(this)
        init()

       checkPermissions()
    }

    fun init(){
        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        myLocationListener = MyLocationListener()
        myLocationListener.setMyLocationListenerInterface(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults[0] == AppCompatActivity.RESULT_OK){
            checkPermissions()
        }
    }

    private fun checkPermissions(){
        preferences = this.requireActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        val permissions = arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION)
        if(context?.let { ActivityCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION) } != PackageManager.PERMISSION_GRANTED
            && context?.let { ActivityCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_COARSE_LOCATION) } != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, 1)
            }
        }else{
            mMap.isMyLocationEnabled = true

            val sampleRate:Long = preferences.getLong(APP_PREFERENCES_MINUTES, 1L)
            val accuracy:Float = preferences.getFloat(APP_PREFERENCES_METRES, 10.0f)

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,sampleRate,accuracy,myLocationListener)
        }
    }

    private fun init(title:String, latitude: String, longitude:String) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        binding.buttonAdd.setOnClickListener {
            val  mUpCameraPosition = mMap.cameraPosition
            val country = LatLng (mUpCameraPosition.target.latitude, mUpCameraPosition.target.longitude)
            mMap.addMarker(MarkerOptions().position(country).title("New point").draggable(true))

            val title = "New Point"
            val latitude = country.latitude.toString()
            val longitude = country.longitude.toString()

            init(title,latitude, longitude)
        }
    }

    override fun myOnLocationChanged(p0: Location) {
        super.myOnLocationChanged(p0)
//        var distance : Float = 0.0f
//        if (p0.hasSpeed() && lastLocation !=null){
//            distance+=lastLocation.distanceTo(p0)
//        }
//        lastLocation = p0
//        binding.information1.text = distance.toString()
//        binding.information2.text = p0.speed.toString()
    }
}