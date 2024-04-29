package com.vansuita.overplaychallenge.ui.video

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import com.vansuita.overplaychallenge.BuildConfig
import com.vansuita.overplaychallenge.extension.resolveLocationPermission
import com.vansuita.overplaychallenge.ui.theme.OverPlayChallengeTheme

class VideoActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContent {
			OverPlayChallengeTheme {
				LaunchedEffect(Unit) {
					this@VideoActivity.resolveLocationPermission()
				}

				VideoScreen(
					videoUrl = BuildConfig.VIDEO_URL
				)
			}
		}
	}
}