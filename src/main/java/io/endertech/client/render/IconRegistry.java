package io.endertech.client.render;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import java.util.HashMap;
import java.util.Map;

// Inspired by CoFH's IconRegistry

public class IconRegistry
{
    private static Map<String, IIcon> iconMap = new HashMap<String, IIcon>();

    public static void addAndRegisterIcon(String iconName, String location, IIconRegister iconRegister)
    {
        IIcon icon = iconRegister.registerIcon(location);
        addIcon(iconName, icon);
    }

    public static void addIcon(String iconName, IIcon icon)
    {
        iconMap.put(iconName, icon);
    }

    public static IIcon getIcon(String iconName)
    {
        return iconMap.get(iconName);
    }
}
