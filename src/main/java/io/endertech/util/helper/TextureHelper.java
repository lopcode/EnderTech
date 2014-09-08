package io.endertech.util.helper;

public class TextureHelper
{
    public static String metaToType(int meta)
    {
        if (meta == 0) return "Creative";
        else if (meta == 1) return "Redstone";
        else if (meta == 2) return "Resonant";

        return "Unknown";
    }
}
