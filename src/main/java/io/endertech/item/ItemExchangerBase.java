package io.endertech.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ItemExchangerBase extends ItemETEnergyContainer
{
    public HashMap<String, IIcon> animatedCenters = new HashMap<String, IIcon>();

    @Override
    public void registerIcons(IIconRegister iconRegister)
    {
        super.registerIcons(iconRegister);

        Iterator it = items.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();

            String originalName = (String) entry.getValue();
            String animatedTextureName = originalName.replace("exchanger", "exchangerAnim");
            String animatedCenter = "endertech:exchanger/" + animatedTextureName;
            animatedCenters.put(originalName, iconRegister.registerIcon(animatedCenter));
        }
    }

    public IIcon getAnimatedIconFromDamage(int i)
    {
        if (!this.items.containsKey(i))
        {
            return null;
        }

        return animatedCenters.get(this.items.get(i));
    }

    @Override
    public IIcon getIconFromDamageForRenderPass(int damage, int pass)
    {
        if (pass == 0) return this.getIconFromDamage(damage);
        else if (pass == 1) return this.getAnimatedIconFromDamage(damage);
        else return null;
    }

    @Override
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }

    @Override
    public int getRenderPasses(int metadata)
    {
        return 2;
    }
}
