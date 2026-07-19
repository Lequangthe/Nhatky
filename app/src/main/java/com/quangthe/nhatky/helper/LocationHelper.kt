package com.quangthe.nhatky.helper

import android.content.Context
import android.location.Geocoder
import com.quangthe.nhatky.models.Location
import java.util.Locale

object LocationHelper {
    fun getAddress(context: Context, latitude: Double, longitude: Double): String? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses?.isNotEmpty() == true) {
                addresses[0].getAddressLine(0)
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
