package io.endertech.util.helper

import org.lwjgl.input.Keyboard

object KeyHelper {
    val isShiftDown: Boolean
        get() = Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)
}
