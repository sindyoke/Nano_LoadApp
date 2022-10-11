package com.udacity


sealed class ButtonState (val state: String) {
    object Clicked : ButtonState("Clicked")
    object Loading : ButtonState("Downloading...")
    object Completed : ButtonState("Download")
}