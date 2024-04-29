package com.vansuita.overplaychallenge.extension

import androidx.media3.exoplayer.ExoPlayer

fun ExoPlayer.restart() = seekTo(0)