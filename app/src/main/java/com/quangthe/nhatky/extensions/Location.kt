package com.quangthe.nhatky.extensions

import android.Manifest
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.core.location.LocationManagerCompat
import com.simplemobiletools.commons.extensions.toast
import java.util.Locale

fun Context.isLocationEnabled(): Boolean {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return LocationManagerCompat.isLocationEnabled(locationManager)
}

fun Context.hasGPSPermissions() = checkPermission(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) && isLocationEnabled()

fun Context.getLastKnownLocation(): Location? {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return when (
        checkPermission(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) &&
            isLocationEnabled()
    ) {
        true -> {
            val gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            when {
                gpsLocation != null && networkLocation != null -> {
                    if (gpsLocation.elapsedRealtimeNanos - networkLocation.elapsedRealtimeNanos > 0) {
                        if (config.enableDebugOptionToastLocation) toast("GPS Location > Network Location")
                        gpsLocation
                    } else {
                        if (config.enableDebugOptionToastLocation) toast("Network Location > GPS Location")
                        networkLocation
                    }
                }

                gpsLocation != null -> {
                    if (config.enableDebugOptionToastLocation) toast("GPS Location")
                    gpsLocation
                }

                networkLocation != null -> {
                    if (config.enableDebugOptionToastLocation) toast("Network Location")
                    networkLocation
                }

                else -> {
                    null
                }
            }
        }

        false -> {
            null
        }
    }
}

fun Context.getFromLocation(
    latitude: Double,
    longitude: Double,
    maxResults: Int,
): List<Address>? {
//    val lat = java.lang.Double.parseDouble(String.format("%.6f", latitude))
//    val lon = java.lang.Double.parseDouble(String.format("%.7f", longitude))
    val addressList = arrayListOf<Address>()
    try {
        addressList.addAll(Geocoder(this, Locale.getDefault()).getFromLocation(latitude, longitude, maxResults)!!)
    } catch (e: Exception) {
        toast(e.message ?: "Error")
    }
    return addressList
}

fun Context.fullAddress(address: Address): String {
    val sb = StringBuilder()
    when (address.getAddressLine(0) != null) {
        true -> {
            sb.append(address.getAddressLine(0))
        }

        false -> {
            if (address.countryName != null) sb.append(address.countryName).append(" ")
            if (address.adminArea != null) sb.append(address.adminArea).append(" ")
            if (address.locality != null) sb.append(address.locality).append(" ")
            if (address.subLocality != null) sb.append(address.subLocality).append(" ")
            if (address.thoroughfare != null) sb.append(address.thoroughfare).append(" ")
            if (address.featureName != null) sb.append(address.featureName).append(" ")
        }
    }
    return sb.toString()
}
