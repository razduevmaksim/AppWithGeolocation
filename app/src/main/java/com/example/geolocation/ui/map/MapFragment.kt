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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        //SharedPreferences
        val mapViewModel =
            ViewModelProvider(this)[MapViewModel::class.java]
        preferences = this.requireActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        binding.buttonAdd.setOnClickListener {
            val  mUpCameraPosition = mMap.cameraPosition
            val country = LatLng (mUpCameraPosition.target.latitude, mUpCameraPosition.target.longitude)
            mMap.addMarker(MarkerOptions().position(country).title("New point").draggable(true))

            val editPermission = true
            val latitude = country.latitude.toString()
            val longitude = country.longitude.toString()
            //Передача данных в хранилище
            val editor = preferences.edit()
            editor.putString(ITEM_TITLE, "New point")
            editor.putString(ITEM_LATITUDE, latitude)
            editor.putString(ITEM_LONGITUDE, longitude)
            editor.putBoolean(ITEM_EDIT_PERMISSION, editPermission)
            editor.apply()
        }

        //mMap.setOnMarkerDragListener()
    }
}