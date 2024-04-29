package com.vansuita.overplaychallenge.ui.video

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vansuita.overplaychallenge.BuildConfig
import com.vansuita.overplaychallenge.gyroscope.GyroscopeDetector
import com.vansuita.overplaychallenge.location.LocationUpdater
import com.vansuita.overplaychallenge.shake.ShakeDetector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.abs

class VideoViewModel(
	private val locationUpdater: LocationUpdater,
	private val shakeDetector: ShakeDetector,
	private val gyroscopeDetector: GyroscopeDetector
) : ViewModel() {

	private val _previousLocation = MutableStateFlow<Location?>(null)

	private val mutableEvent = MutableStateFlow<VideoEvent>(VideoEvent.Ready)
	val event = mutableEvent.asStateFlow()

	init {
		startLocationUpdates()
		startDetectingShakes()
		startListeningToGyroscopeMovements()
	}

	private fun startLocationUpdates() {
		viewModelScope.launch(Dispatchers.IO) {
			locationUpdater.asFlow().distinctUntilChanged().collect { location ->
				_previousLocation.apply {
					if (value == null) {
						value = location
					} else {
						val distance = value!!.distanceTo(location).toInt()

						Log.i(
							"Location",
							"Distance: $distance -> ${location.latitude}-${location.longitude}"
						)

						if (distance >= BuildConfig.DISTANCE_TO_RESTART_VIDEO) {
							mutableEvent.value = VideoEvent.Restart(distance)
							_previousLocation.value = location
						}
					}
				}
			}
		}
	}

	private fun startDetectingShakes() {
		viewModelScope.launch(Dispatchers.Default) {
			shakeDetector.asFlow().collect {
				mutableEvent.value = VideoEvent.Pause
			}
		}
	}

	private fun startListeningToGyroscopeMovements() {
		viewModelScope.launch(Dispatchers.Default) {
			gyroscopeDetector.zAxisAsFlow()
				.filter { abs(it) > MOVEMENT_THRESHOLD }
				.map { calculateSeekAmount(it) }
				.distinctUntilChanged()
				.collect {
					mutableEvent.value = VideoEvent.SeekTo(it)
				}
		}

		viewModelScope.launch(Dispatchers.Default) {
			gyroscopeDetector.xAxisAsFlow()
				.filter { abs(it) > MOVEMENT_THRESHOLD }
				.map { calculateVolumeAmount(it) }
				.distinctUntilChanged()
				.collect {
					Log.i("xAxisAsFlow", it.toString())
					mutableEvent.value = VideoEvent.VolumeTo(it)
				}
		}
	}


	private fun calculateSeekAmount(rotationZ: Float): Long {
		val scalingFactor = 500L
		return (rotationZ * scalingFactor).toLong()
	}

	private fun calculateVolumeAmount(rotationX: Float): Float {
		val scalingFactor = 0.5f
		val value = (1 - rotationX) * scalingFactor
		val volume = Math.round(value * 10f) / 10f
		return volume.coerceIn(0.0f, 1.0f)

	}

	companion object {
		private const val MOVEMENT_THRESHOLD = 0.2f
	}
}