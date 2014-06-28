package io.endertech.util;

public class Key
{
    public static KeyCode[] keyCodes = KeyCode.values();

    public static enum KeyCode
    {
        TOOL_INCREASE,
        TOOL_DECREASE
    }

    public static KeyCode fromByte(byte keyCode)
    {
        return keyCodes[keyCode];
    }
}
