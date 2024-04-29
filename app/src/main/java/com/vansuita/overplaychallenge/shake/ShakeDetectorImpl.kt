package com.vansuita.overplaychallenge.shake

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.vansuita.overplaychallenge.extension.sensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Date
import kotlin.math.sqrt

private const val shakeThreshold = 50f
private const val shakeTimeThreshold = 5000L

class ShakeDetectorImpl(private val context: Context) : ShakeDetector {

	private var previousShakeTimestamp = 0L
	private val sensorManager by lazy { context.sensorManager() }

	override fun asFlow(): Flow<Date> = callbackFlow {
		val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

		val sensorEventListener = object : SensorEventListener {
			override fun onSensorChanged(event: SensorEvent?) {
				if (event == null) return
				val x = event.values[0]
				val y = event.values[1]
				val z = event.values[2]
				val acceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
				if (acceleration > shakeThreshold) {
					val currentTime = System.currentTimeMillis()
					if (currentTime - previousShakeTimestamp >= shakeTimeThreshold) {
						previousShakeTimestamp = currentTime
						trySend(Date(previousShakeTimestamp))
					}
				}
			}

			override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
				// No implementation needed
			}
		}

		sensorManager.registerListener(
			sensorEventListener,
			accelerometerSensor,
			SensorManager.SENSOR_DELAY_NORMAL
		)

		awaitClose {
			sensorManager.unregisterListener(sensorEventListener)
		}
	}
}