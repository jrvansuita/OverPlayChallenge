package com.vansuita.overplaychallenge.ui.video

import android.content.Context
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.vansuita.overplaychallenge.R
import com.vansuita.overplaychallenge.composable.ViewPlayerComposable
import com.vansuita.overplaychallenge.composable.rememberVideoPlayerFromUrl
import com.vansuita.overplaychallenge.extension.restart
import org.koin.androidx.compose.koinViewModel

@OptIn(UnstableApi::class)
@Composable
fun VideoScreen(videoUrl: String, viewModel: VideoViewModel = koinViewModel<VideoViewModel>()) {

	val context = LocalContext.current
	val event by viewModel.event.collectAsState(initial = VideoEvent.Ready)

	val player = rememberVideoPlayerFromUrl(videoUrl = videoUrl)

	LaunchedEffect(event) {
		player.reactToEvent(context, event)
	}
	Surface {
		ViewPlayerComposable(
			player = player
		)
	}
}

private fun ExoPlayer.reactToEvent(context: Context, event: VideoEvent) {
	val feedbackMessage = when (event) {
		VideoEvent.Ready -> {
			playWhenReady = true
			null
		}

		is VideoEvent.Restart -> {
			restart()
			context.getString(R.string.restarting_video_distance_meters, event.distance)
		}

		VideoEvent.Pause -> {
			pause()
			context.getString(R.string.pausing_video_shake_detected)
		}

		is VideoEvent.SeekTo -> {
			val currentPosition = currentPosition
			val newPosition = currentPosition + event.amount
			val finalPosition = newPosition.coerceIn(0, duration)
			seekTo(finalPosition)
			context.getString(R.string.seeking_by, event.amount)
		}

		is VideoEvent.VolumeTo -> {
			this.volume = event.amount
			context.getString(R.string.voluming_by, event.amount.toString())
		}
	}

	if (feedbackMessage != null) Toast.makeText(context, feedbackMessage, Toast.LENGTH_LONG).show()
}