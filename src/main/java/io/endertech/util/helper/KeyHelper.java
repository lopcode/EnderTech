package io.endertech.util.helper;

import org.lwjgl.input.Keyboard;

public class KeyHelper
{
    public static boolean isShiftDown()
    {
        return (Keyboard.isKeyDown(42) || (Keyboard.isKeyDown(54)));
    }
}
