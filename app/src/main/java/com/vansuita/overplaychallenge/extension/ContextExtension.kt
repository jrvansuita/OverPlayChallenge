package com.vansuita.overplaychallenge.extension

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.hardware.SensorManager
import android.location.LocationManager
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat

private val locationPermissionSet: Array<String>
	get() = arrayOf(ACCESS_FINE_LOCATION)

fun Context.isLocationPermissionGranted() = locationPermissionSet.any {
	ActivityCompat.checkSelfPermission(this, it) == PERMISSION_GRANTED
}

fun Context.locationService() = getSystemService(Context.LOCATION_SERVICE) as LocationManager


fun Context.isLocationEnabled() = LocationManagerCompat.isLocationEnabled(locationService())

fun ComponentActivity.requestLocationPermission() = ActivityCompat.requestPermissions(
	this,
	locationPermissionSet,
	1
)

fun ComponentActivity.resolveLocationPermission() {
	if (!isLocationPermissionGranted()) {
		requestLocationPermission()
	}
}

fun Context.sensorManager(): SensorManager {
	return getSystemService(Context.SENSOR_SERVICE) as SensorManager
}
