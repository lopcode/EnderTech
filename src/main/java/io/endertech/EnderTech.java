package io.endertech;

import codechicken.lib.math.MathHelper;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLFingerprintViolationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.client.GUIBlockOverlay;
import io.endertech.common.CommonProxy;
import io.endertech.config.ConfigHandler;
import io.endertech.gui.CreativeTabItems;
import io.endertech.helper.BlockHelper;
import io.endertech.helper.LogHelper;
import io.endertech.items.ETItems;
import io.endertech.lib.Reference;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.io.File;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, certificateFingerprint = Reference.FINGERPRINT, dependencies = "required-after:ThermalExpansion@[3.0.0.2,);required-after:ForgeMultipart")
public class EnderTech
{
    @Mod.Instance(Reference.MOD_ID)
    public static EnderTech instance;

    @SidedProxy(clientSide = "io.endertech.client.ClientProxy", serverSide = "io.endertech.common.CommonProxy")
    public static CommonProxy proxy;

    public static final CreativeTabs tabItems = new CreativeTabItems();

    @EventHandler
    public void invalidFingerprint(FMLFingerprintViolationEvent event)
    {
        if (Reference.FINGERPRINT.equals("@FINGERPRINT@"))
        {
            LogHelper.warn("Fingerprint was missing from the jar, this mod could have been tampered with!");
        } else
        {
            LogHelper.fatal("Fingerprint doesn't match - this mod has been tampered with!");
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void preInit(FMLPreInitializationEvent event)
    {
        // Version number
        event.getModMetadata().version = Reference.VERSION_NUMBER;

        // Configuration
        ConfigHandler.init(event.getModConfigurationDirectory().getAbsolutePath() + File.separator + Reference.CHANNEL_NAME.toLowerCase() + File.separator);

        LogHelper.debug("Loaded config");

        // Version checker

        // Tick handlers
        proxy.registerTickerHandlers();

        // KeyBinding handler

        // Sound handler

        // Blocks

        // Items
        ETItems.init();

        LogHelper.debug("preInit complete");
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        LogHelper.debug("DIRT BLOCK >> " + Block.dirt.getUnlocalizedName());
        LogHelper.info("Sin 360: " + MathHelper.sin(2 * MathHelper.pi));

        LogHelper.debug("init complete");
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new GUIBlockOverlay((Minecraft.getMinecraft())));

        BlockHelper.initSoftBlocks();

        LogHelper.info("Registering recipes");
        ItemStack capacitorReinforced = GameRegistry.findItemStack("ThermalExpansion", "capacitorReinforced", 1);
        ItemStack capacitorResonant = GameRegistry.findItemStack("ThermalExpansion", "capacitorResonant", 1);
        ItemStack tesseractFrameFull = GameRegistry.findItemStack("ThermalExpansion", "tesseractFrameFull", 1);

        ItemStack enderEyeStack = new ItemStack(Item.eyeOfEnder);

        GameRegistry.addRecipe(new ShapedOreRecipe(ETItems.toolExchangerBasic, new Object[]{"XE", "XC", "XT", 'E', enderEyeStack, 'C', capacitorReinforced, 'T', tesseractFrameFull}));
        GameRegistry.addRecipe(new ShapedOreRecipe(ETItems.toolExchangerAdvanced, new Object[]{"XE", "XC", "XT", 'E', enderEyeStack, 'C', capacitorResonant, 'T', tesseractFrameFull}));
    }
}
