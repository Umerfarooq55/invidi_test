Simple Video Player with ExoPlayer
A basic Android application demonstrating video playback using ExoPlayer. The app supports seamless video switching and resumes playback while adhering to essential Android lifecycle practices. It’s implemented with modern Android development tools and architecture, including Jetpack Compose, MVVM, and Hilt.

Features
Dual Video Playback:

Plays two videos from URLs.
The second video starts after 30 seconds of the first video.
Automatically resumes the first video after the second video finishes.
Player Controls:

Basic video player functionalities like play, pause, and seek.
Landscape Mode:

Fully functional landscape mode with seamless orientation handling.
Lifecycle-Aware:

Handles app lifecycle events to ensure smooth video playback when the app is minimized or rotated.
Tech Stack
ExoPlayer: For video playback.
Jetpack Compose: For modern, declarative UI design.
MVVM Architecture: For separation of concerns and better maintainability.
Hilt: For dependency injection.

Launch the app.
The first video starts automatically.
After 30 seconds, the second video begins playing.
Once the second video ends, the first video resumes from where it paused.
Rotate the device to test landscape functionality.
