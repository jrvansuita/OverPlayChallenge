package com.vansuita.overplaychallenge.shake

import kotlinx.coroutines.flow.Flow
import java.util.Date

interface ShakeDetector {
	fun asFlow(): Flow<Date>
}