package com.dne.aart.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dne.aart.util.DataManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.fragment_expo_info.*
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.CameraPosition

class ExpoInfoFragment : Fragment() {

    private var expoId = 0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(com.dne.aart.R.layout.fragment_expo_info, container, false)

        val safeArgs: ExpoInfoFragmentArgs by navArgs()
        expoId = safeArgs.expoId

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val expo = DataManager.getExpoById(expoId)
        val lat = expo.location.lat
        val lon = expo.location.long

        // ==================== MAP START =====================
        val mapFragment =
            childFragmentManager.findFragmentById(com.dne.aart.R.id.mapFragment)
                    as SupportMapFragment?
        mapFragment!!.getMapAsync { mMap ->
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            mMap.clear() //clear old markers
            val googlePlex = CameraPosition.builder()
                .target(LatLng(lat, lon))
                .zoom(17f)
                .bearing(0f)
                .tilt(45f)
                .build()

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null)

            val marker = MarkerOptions()
                .position(LatLng(lat, lon))

            // Disables the default actions on marker click
            mMap.setOnMarkerClickListener {
                true
            }

            mMap.addMarker(marker)
        }
        // ==================== MAP END =======================

        tv_title_expoinfo.text = expo.title
        tv_info_expoinfo.text = expo.info

        btn_expand_map.setOnClickListener {
            val intent = Intent(activity!!.applicationContext, MapsActivity::class.java)
            intent.putExtra("lat", lat)
            intent.putExtra("lon", lon)

            startActivity(intent)
        }

        val btnSelectModel = view.findViewById<Button>(com.dne.aart.R.id.btn_select_model)
        btnSelectModel.setOnClickListener {
            val action =
                ExpoInfoFragmentDirections.actionExpoInfoFragmentToArtListFragment()
            action.expoId = expoId
            findNavController().navigate(action)
        }
    }
}