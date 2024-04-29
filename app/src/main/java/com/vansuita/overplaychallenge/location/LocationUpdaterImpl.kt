package com.vansuita.overplaychallenge.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.vansuita.overplaychallenge.BuildConfig
import com.vansuita.overplaychallenge.extension.isLocationEnabled
import com.vansuita.overplaychallenge.extension.isLocationPermissionGranted
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LocationUpdaterImpl(
	private val context: Context,
) : LocationUpdater {

	@SuppressLint("MissingPermission")
	override fun asFlow(): Flow<Location> = callbackFlow {
		val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
		val locationRequest = LocationRequest.Builder(BuildConfig.LOCATION_UPDATE_INTERVAL)
			.setIntervalMillis(BuildConfig.LOCATION_UPDATE_INTERVAL)
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
			.build()
	
		val locationCallback = object : LocationCallback() {
			override fun onLocationResult(locationResult: LocationResult) {
				locationResult.lastLocation?.let { trySend(it) }
			}
		}

		if (context.isLocationPermissionGranted() && context.isLocationEnabled()) {
			fusedLocationClient.requestLocationUpdates(
				locationRequest, locationCallback, Looper.getMainLooper()
			)
		}

		awaitClose {
			fusedLocationClient.removeLocationUpdates(locationCallback)
		}
	}
}
