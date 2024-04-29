package com.vansuita.overplaychallenge.gyroscope

import kotlinx.coroutines.flow.Flow

interface GyroscopeDetector {
	suspend fun zAxisAsFlow(): Flow<ZAxis>
	suspend fun xAxisAsFlow(): Flow<XAxis>
}

typealias ZAxis = Float
typealias XAxis = Float
