package com.quangthe.nhatky.commons.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object LocationUtil {

    suspend fun getAddressFromLatLng(context: Context, lat: Double, lng: Double): String? = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCoroutine { continuation ->
                    geocoder.getFromLocation(lat, lng, 1) { addresses ->
                        continuation.resume(formatAddress(addresses.firstOrNull()))
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                formatAddress(addresses?.firstOrNull())
            }
        } catch (e: Exception) {
            Log.w("LocationUtil", "Failed to get address from LatLng: $e")
            null
        }
    }

    suspend fun getLatLngFromAddress(context: Context, addressString: String): Address? = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocationName(addressString, 1)
            return@withContext addresses?.firstOrNull()
        } catch (e: Exception) {
            Log.w("LocationUtil", "Failed to get LatLng from address: $e")
            null
        }
    }

    private fun formatAddress(address: Address?): String? {
        if (address == null) return null
        val sb = StringBuilder()
        for (i in 0..address.maxAddressLineIndex) {
            sb.append(address.getAddressLine(i))
            if (i < address.maxAddressLineIndex) sb.append(", ")
        }
        return sb.toString()
    }
}
