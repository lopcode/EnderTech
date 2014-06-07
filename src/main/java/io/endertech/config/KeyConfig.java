package io.endertech.config;

public class KeyConfig
{
    public static final String keyToolIncreaseDescription = "Tool Increase";
    public static final byte keyToolIncreaseCode = 0x01;

    public static final String keyToolDecreaseDescription = "Tool Decrease";
    public static final byte keyToolDecreaseCode = 0x02;

    public static byte descriptionToCode(String description)
    {
        if (description.equals(keyToolIncreaseDescription)) {
            return keyToolIncreaseCode;
        } else if (description.equals(keyToolDecreaseDescription)) {
            return keyToolDecreaseCode;
        }

        return 0;
    }
}
