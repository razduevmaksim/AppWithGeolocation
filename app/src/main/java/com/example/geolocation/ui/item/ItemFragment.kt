@file:Suppress("DEPRECATION")

package com.example.geolocation.ui.item

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.geolocation.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class ItemFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //подключение карты
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapItem) as SupportMapFragment? as SupportMapFragment
        mapFragment.getMapAsync(this)
        setHasOptionsMenu(true)

    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireActivity().onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true

        preferences = this.requireActivity().getSharedPreferences(GEOLOCATION_PREFERENCES_ITEM, Context.MODE_PRIVATE)

        val title = preferences.getString(GEOLOCATION_PREFERENCES_TITLE_ITEM, "New Point")
        val latitude = preferences.getFloat(GEOLOCATION_PREFERENCES_LATITUDE_ITEM, 0.0f)
        val longitude = preferences.getFloat(GEOLOCATION_PREFERENCES_LONGITUDE_ITEM, 0.0f)

        val country = LatLng(latitude.toDouble(), longitude.toDouble())
        mMap.addMarker(MarkerOptions().position(country).title(title))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(country))

    }
}