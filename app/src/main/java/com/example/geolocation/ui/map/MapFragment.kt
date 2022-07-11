package com.example.geolocation.ui.map

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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
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
        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        myLocationListener = MyLocationListener()
        myLocationListener.setMyLocationListenerInterface(this)
        checkPermissions()
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
        val permissions = arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION)
        if(context?.let { ActivityCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION) } != PackageManager.PERMISSION_GRANTED
            && context?.let { ActivityCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_COARSE_LOCATION) } != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, 1)
            }
        }else{

            mMap.isMyLocationEnabled = true
            //2 - частота, 10 - метры из 3 вкладки
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2,10.0f,myLocationListener)
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

    //При изменении позиции. Добавление к дистанции метров
    override fun OnLocationChanged(location: Location){
        var distance = 0.0f
        if (location.hasSpeed() && lastLocation != null){
            distance += lastLocation.distanceTo(location)
        }
        lastLocation = location
        //String.valueOf(disance) - показывает дистанцию, кот. прошли
    }
}