package io.endertech.items;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.endertech.helper.LogHelper;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ItemETBase extends Item
{
    private boolean hasTextures = true;

    public ItemETBase(int id)
    {
        super(id);
        this.setHasSubtypes(true);
    }

    public HashMap<Integer, String> items = new HashMap<Integer, String>();
    public HashMap<String, Icon> icons = new HashMap<String, Icon>();

    public ItemStack addItem(int number, String name, boolean shouldRegister)
    {
        if (this.items.containsKey(number))
            return null;

        this.items.put(number, name);

        ItemStack item = new ItemStack(this, 1, number);
        if (shouldRegister)
            GameRegistry.registerCustomItemStack(name, item);

        return item;
    }

    public ItemStack addItem(int number, String name)
    {
        return addItem(number, name, true);
    }

    @Override
    public void registerIcons(IconRegister iconRegister)
    {
        if (!this.hasTextures)
        {
            return;
        }

        Iterator it = items.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();

            String name = (String) entry.getValue();
            String icon_name = "endertech:exchanger/" + name;
            LogHelper.info("Registering icon: " + icon_name);
            icons.put(name, iconRegister.registerIcon(icon_name));
        }
    }

    @Override
    public Icon getIconFromDamage(int i)
    {
        if (!this.items.containsKey(i))
        {
            return null;
        }

        return icons.get(this.items.get(i));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        Iterator it = items.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();

            par3List.add(new ItemStack(par1, 1, (Integer) entry.getKey()));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        int i = stack.getItemDamage();
        if (!this.items.containsKey(i))
        {
            return "item.invalid";
        }

        return getUnlocalizedName() + '.' + this.items.get(i);
    }
}
