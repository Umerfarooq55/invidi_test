package com.invidi.test

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.media3.common.PlaybackException
import androidx.media3.exoplayer.ExoPlayer
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.kotlin.*
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var mockExoPlayer: ExoPlayer

    @InjectMocks
    lateinit var viewModel: MainViewModel

    private val mainContentUri = Uri.parse("http://example.com/main_content.mp4")
    private val adContentUri = Uri.parse("http://example.com/ad_content.mp4")

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = MainViewModel(mockExoPlayer)
    }

    @Test
    fun `Given main content and ad content, When startVideo is called, Then ExoPlayer is prepared for main content`() = runTest {
        viewModel.startVideo(mainContentUri, adContentUri)

        verify(mockExoPlayer).setMediaItem(any())
        verify(mockExoPlayer).prepare()
        verify(mockExoPlayer).playWhenReady = true
    }

    @Test
    fun `Given an unexpected playback error, When handlePlayerError is called, Then correct error message is emitted`() = runTest {
        //GIVEN
        val playbackException = mock<PlaybackException> {
            on { message } doReturn "Unexpected error"
        }

        viewModel.handlePlayerError(playbackException)
        // GIVEN
        val errorMessageResId = viewModel.errorFlow.first()

        // THEN
        assertEquals(R.string.unexpected_error, errorMessageResId)
    }

    @Test
    fun `Given ViewModel is cleared, When onCleared is called, Then ExoPlayer is released`() {
        viewModel.onCleared()
        verify(mockExoPlayer).release()
    }

    @After
    fun tearDown() {
        Mockito.reset(mockExoPlayer)
    }
}
