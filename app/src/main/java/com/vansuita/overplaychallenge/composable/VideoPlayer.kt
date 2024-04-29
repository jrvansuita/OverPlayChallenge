package com.vansuita.overplaychallenge.composable

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun rememberVideoPlayerFromUrl(
	videoUrl: String
): ExoPlayer {
	val context = LocalContext.current
	val player = remember {
		ExoPlayer.Builder(context).build()
			.apply {
				val defaultDataSourceFactory = DefaultDataSource.Factory(context)
				val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(
					context,
					defaultDataSourceFactory
				)
				val source = ProgressiveMediaSource.Factory(dataSourceFactory)
					.createMediaSource(MediaItem.fromUri(videoUrl))

				setMediaSource(source)
				prepare()
			}
	}

	player.playWhenReady = true
	player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
	player.repeatMode = Player.REPEAT_MODE_ONE

	DisposableEffect(Unit) {
		onDispose {
			player.release()
		}
	}

	return player
}


@OptIn(UnstableApi::class)
@Composable
fun ViewPlayerComposable(
	player: ExoPlayer
) {
	val context = LocalContext.current

	AndroidView(factory = {
		PlayerView(context).apply {
			hideController()
			useController = false
			resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM

			this.player = player
			layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
		}
	})
}
