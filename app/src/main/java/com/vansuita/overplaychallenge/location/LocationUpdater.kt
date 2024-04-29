package com.vansuita.overplaychallenge.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationUpdater {
	fun asFlow(): Flow<Location>
}