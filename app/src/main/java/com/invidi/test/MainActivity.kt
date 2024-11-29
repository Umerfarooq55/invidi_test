package com.invidi.test

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.ui.PlayerView
import com.invidi.test.ui.theme.InvidiTestTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.material3.*



const val FIRST_VIDEO_URL = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
const val SECOND_VIDEO_URL = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"

/**
 * [MainViewModel] is responsible for managing the state and interactions of the video player.
 * It handles playback of main and ad content, error reporting, and resource management.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val mainContentUri = remember { Uri.parse(FIRST_VIDEO_URL) }
            val adContentUri = remember { Uri.parse(SECOND_VIDEO_URL) }

            InvidiTestTheme {
                val viewModel = hiltViewModel<MainViewModel>()

                // Start video playback
                viewModel.startVideo(
                    mainContentUri = mainContentUri,
                    adContentUri = adContentUri
                )

                // Track lifecycle events to handle player behavior
                var lifecycleEvent by remember { mutableStateOf(Lifecycle.Event.ON_CREATE) }
                val lifecycleOwner = LocalLifecycleOwner.current

                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        lifecycleEvent = event
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                // Handle error messages from the ViewModel
                LaunchedEffect(Unit) {
                    viewModel.errorFlow.collect { errorMessageResId ->
                        Toast.makeText(
                            this@MainActivity,
                            getString(errorMessageResId),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(text = stringResource(id = R.string.app_name)) },
                            colors = topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primary)
                        )
                    }
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp)
                    ) {
                        AndroidView(
                            factory = { context ->
                                PlayerView(context).also { playerView ->
                                    playerView.player = viewModel.exoPlayer
                                }
                            },
                            update = { playerView ->
                                // Handle player state based on lifecycle events
                                when (lifecycleEvent) {
                                    Lifecycle.Event.ON_PAUSE -> {
                                        playerView.onPause()
                                        viewModel.exoPlayer.pause()
                                    }
                                    Lifecycle.Event.ON_RESUME -> {
                                        playerView.onResume()
                                        viewModel.exoPlayer.playWhenReady = true
                                    }
                                    else -> Unit
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16 / 9f)
                        )
                    }
                }
            }
        }
    }
}
