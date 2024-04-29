package com.vansuita.overplaychallenge.di

import com.vansuita.overplaychallenge.gyroscope.GyroscopeDetector
import com.vansuita.overplaychallenge.gyroscope.GyroscopeDetectorImpl
import com.vansuita.overplaychallenge.location.LocationUpdater
import com.vansuita.overplaychallenge.location.LocationUpdaterImpl
import com.vansuita.overplaychallenge.shake.ShakeDetector
import com.vansuita.overplaychallenge.shake.ShakeDetectorImpl
import com.vansuita.overplaychallenge.ui.video.VideoViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val appModule = module {
	viewModelOf(::VideoViewModel)
	factoryOf(::LocationUpdaterImpl) { bind<LocationUpdater>() }
	factoryOf(::ShakeDetectorImpl) { bind<ShakeDetector>() }
	factoryOf(::GyroscopeDetectorImpl) { bind<GyroscopeDetector>() }
}