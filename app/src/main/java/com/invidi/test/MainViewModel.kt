package com.invidi.test

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Constants for error type matching
private const val SOURCE = "Source"
private const val RENDERER = "Renderer"
private const val TIME_TO_SWITCH_VIDEO = 30_000L

/**
 * [MainViewModel] is responsible for managing the state and interactions of the video player.
 * It handles playback of main and ad content, error reporting, and resource management.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
   internal val exoPlayer: ExoPlayer
) : ViewModel() {

    // Media items for main content and ad content
    private lateinit var mainContent: MediaItem
    private lateinit var adContent: MediaItem

    // SharedFlow for emitting error messages as resource IDs
    private val _errorFlow = MutableSharedFlow<Int>(replay = 1)
    val errorFlow: SharedFlow<Int> get() = _errorFlow

    init {
        // Add a listener to handle player events
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED && exoPlayer.currentMediaItem == adContent) {
                    returnToMainContent()
                }
            }
            override fun onPlayerError(error: PlaybackException) {
                handlePlayerError(error)
            }
        })
    }

    /**
     * Starts playback of the main content and schedules an ad insertion.
     * @param mainContentUri The URI of the main content.
     * @param adContentUri The URI of the ad content.
     */
    internal fun startVideo(mainContentUri: Uri, adContentUri: Uri) {
        mainContent = MediaItem.fromUri(mainContentUri)
        adContent = MediaItem.fromUri(adContentUri)

        playMedia(mainContent)

        viewModelScope.launch {
            delay(TIME_TO_SWITCH_VIDEO)
            insertAdContent()
        }
    }

    /**
     * Inserts ad content into the playback.
     */
    private fun insertAdContent() {
        playMedia(adContent)
    }

    /**
     * Returns to the main content after ad playback ends.
     */
    internal fun returnToMainContent() {
        playMedia(mainContent, resumePosition = TIME_TO_SWITCH_VIDEO)
    }

    /**
     * Plays the specified media item.
     * @param mediaItem The media item to play.
     * @param resumePosition The position to start playback from, default is 0.
     */
    private fun playMedia(mediaItem: MediaItem, resumePosition: Long = 0L) {
        exoPlayer.pause()
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.seekTo(resumePosition)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    /**
     * Handles playback errors and emits error messages as resource IDs.
     */
    internal fun handlePlayerError(error: PlaybackException) {
        val errorMessageResId = when {
            error.message?.contains(SOURCE) == true -> R.string.error_loading_media
            error.message?.contains(RENDERER) == true -> R.string.renderer_error
            error.cause is java.net.UnknownHostException -> R.string.network_error
            else -> R.string.unexpected_error
        }

        viewModelScope.launch {
            _errorFlow.emit(errorMessageResId)
        }

        exoPlayer.stop()
    }

    /**
     * Cleans up resources when the ViewModel is cleared.
     */
    public override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }
}
