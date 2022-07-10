package com.example.geolocation.ui.map

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.example.geolocation.*
import com.example.geolocation.databinding.FragmentMapBinding
import com.example.geolocation.model.GeolocationModel
import com.example.geolocation.ui.list.ListViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Math.log


class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
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
        preferences = this.requireActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
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

        //mMap.setOnMarkerDragListener()
    }
}