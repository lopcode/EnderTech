package io.endertech.util;

public class Key
{
    public static KeyCode[] keyCodes = KeyCode.values();

    public static enum KeyCode
    {
        TOOL_INCREASE,
        TOOL_DECREASE,
        UNKNOWN
    }

    public static KeyCode fromByte(byte keyCode)
    {
        return keyCodes[keyCode];
    }

    public static byte toByte(KeyCode key)
    {
        return (byte) key.ordinal();
    }
}
