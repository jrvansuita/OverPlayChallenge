package com.vansuita.overplaychallenge.ui.video

sealed class VideoEvent {
	data object Ready : VideoEvent()
	class Restart(val distance: Int) : VideoEvent()
	data object Pause : VideoEvent()
	class SeekTo(val amount: Long) : VideoEvent()
	class VolumeTo(val amount: Float) : VideoEvent()
}