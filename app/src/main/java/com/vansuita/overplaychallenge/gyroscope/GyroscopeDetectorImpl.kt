package com.vansuita.overplaychallenge.gyroscope

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.vansuita.overplaychallenge.extension.sensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlin.math.roundToInt

class GyroscopeDetectorImpl(
	private val context: Context
) : GyroscopeDetector {

	private val sensorManager by lazy {
		context.sensorManager()
	}

	override suspend fun zAxisAsFlow(): Flow<ZAxis> = captureGyroscopeEvent().map {
		val zValue = it.values[2]
		val rotationZ = (zValue * 100f).roundToInt() / 100f
		rotationZ
	}

	override suspend fun xAxisAsFlow(): Flow<XAxis> = captureGyroscopeEvent().map {
		val xValue = it.values[0]
		val rotationX = Math.round(xValue * 10f) / 10f
		rotationX
	}

	private suspend fun captureGyroscopeEvent() = callbackFlow {
		val gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

		val sensorEventListener = object : SensorEventListener {
			override fun onSensorChanged(event: SensorEvent?) {
				if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) trySend(event)
			}

			override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
				// No implementation needed
			}
		}

		sensorManager.registerListener(
			sensorEventListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL
		)

		awaitClose {
			sensorManager.unregisterListener(sensorEventListener)
		}
	}
}