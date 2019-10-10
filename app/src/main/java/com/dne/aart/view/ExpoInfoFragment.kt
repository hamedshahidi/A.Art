package com.dne.aart.view


import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dne.aart.R
import com.dne.aart.model.Expo
import com.dne.aart.util.DataManager
import com.google.android.gms.location.FusedLocationProviderClient
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
    private var isAdmin: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_expo_info, container, false)

        getSafeArgs()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val expo = DataManager.getExpoById(expoId)
        val location = expo.location

        initializeUi(view, location)

        populateUi(expo, location)
    }

    private fun initializeUi(view: View, location: com.dne.aart.model.Location) {
        val btnSelectModel = view.findViewById<Button>(R.id.btn_select_model)
        val btnBack = view.findViewById<Button>(R.id.btn_back)

        btnSelectModel.setOnClickListener { onBtnSelectModelPressed() }
        btn_expand_map.setOnClickListener { onBtnExpandMapPressed(location) }
        btnBack.setOnClickListener { onBtnBackPressed() }
    }

    private fun populateUi(expo: Expo, location: com.dne.aart.model.Location) {
        tv_title_expoinfo.text = expo.title
        tv_info_expoinfo.text = expo.info

        showMap(location)
    }

    private fun showMap(location: com.dne.aart.model.Location) {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment)
                    as SupportMapFragment?
        mapFragment!!.getMapAsync { map ->
            map.mapType = GoogleMap.MAP_TYPE_NORMAL

            // clear old markers if there is any
            map.clear()
            placeMarkerOnMap(map, location)
            animateCameraToLocation(map, location)
        }
    }

    private fun placeMarkerOnMap(map: GoogleMap, location: com.dne.aart.model.Location) {
        // set marker location
        val marker = MarkerOptions()
            .position(LatLng(location.lat, location.long))

        // Disables the default actions on marker click
        map.setOnMarkerClickListener {
            true
        }
        map.addMarker(marker)
    }

    private fun animateCameraToLocation(map: GoogleMap, location: com.dne.aart.model.Location) {
        // move camera to location
        val googlePlex = CameraPosition.builder()
            .target(LatLng(location.lat, location.long))
            .zoom(17f)
            .bearing(0f)
            .tilt(45f)
            .build()
        map.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null)
    }

    private fun getSafeArgs() {
        val safeArgs: ExpoInfoFragmentArgs by navArgs()
        expoId = safeArgs.expoId
    }

    private fun onBtnBackPressed() {
        findNavController()
            .navigate(
                R.id.expoListFragment, null, NavOptions.Builder()
                    .setPopUpTo(R.id.expoListFragment, true)
                    .build()
            )
    }

    private fun onBtnExpandMapPressed(location: com.dne.aart.model.Location) {
        val intent = Intent(activity!!.applicationContext, MapsActivity::class.java)
        intent.putExtra("lat", location.lat)
        intent.putExtra("lon", location.long)

        startActivity(intent)
    }

    private fun onBtnSelectModelPressed() {
        val action =
            ExpoInfoFragmentDirections.actionExpoInfoFragmentToArtListFragment()
        action.expoId = expoId
        findNavController().navigate(action)
    }
}