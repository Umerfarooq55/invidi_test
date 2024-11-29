# Simple Video Player with ExoPlayer

A basic Android application showcasing video playback using **ExoPlayer**. The app seamlessly switches between two videos and resumes playback while following essential Android lifecycle practices. Built with modern tools and architecture, including **Jetpack Compose**, **MVVM**, and **Hilt**.

---

## Features

### 🎥 Dual Video Playback
- Plays two videos from URLs.
- The second video starts **30 seconds** after the first video begins.
- Automatically resumes the first video after the second video finishes.

### ⏯ Player Controls
- Basic playback controls: **Play**, **Pause**, and **Seek**.

### 🌐 Landscape Mode
- Fully functional **landscape orientation** with smooth handling.

### 🔄 Lifecycle-Aware
- Manages app lifecycle events for seamless video playback during:
  - App minimization.
  - Screen rotation.

---

## Tech Stack

- **ExoPlayer**: For video playback.
- **Jetpack Compose**: For modern, declarative UI design.
- **MVVM Architecture**: For separation of concerns and better maintainability.
- **Hilt**: For dependency injection.

---

## Usage

1. Launch the app.
2. The first video starts automatically.
3. After **30 seconds**, the second video begins playing.
4. Once the second video ends, the first video resumes from where it paused.
5. Rotate the device to test landscape functionality.


## Future Enhancements

- Add support for local videos, where users can add multiple videos and play them next to each other
- Customize timer to play the next video according to user choice
- User experience can be improved in terms of video player sizes
- More tests to get 100% code coverage


