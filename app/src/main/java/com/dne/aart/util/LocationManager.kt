package com.dne.aart.util

import android.app.Activity
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

object LocationManager {

    var lastLocation: Location = Location("")

    // set listener for current location
    fun getLastLocation(activity: Activity){
        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(activity)

        fusedLocationClient.lastLocation.addOnSuccessListener {
                location -> lastLocation = location
        }
    }

    // calculate distance from current location to given location
    fun getDistanceTo(expoLocation: LatLng): Float {
        val location = Location("")
        location.latitude = expoLocation.latitude
        location.longitude = expoLocation.longitude
        val distanceInMeters = lastLocation.distanceTo(location)
        var distanceInKm = (distanceInMeters / 1000.0f)
        distanceInKm = distanceInKm.formatDecimal(2).toFloat()

        return distanceInKm
    }

    // Decimal Formatter
    private fun Float.formatDecimal(numberOfDecimals: Int = 2): String =
        "%.${numberOfDecimals}f".format(this)
}