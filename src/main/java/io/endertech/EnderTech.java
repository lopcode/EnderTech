package io.endertech;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLFingerprintViolationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.block.ETBlocks;
import io.endertech.client.handler.GUIEventHandler;
import io.endertech.client.handler.KeyBindingHandler;
import io.endertech.config.ConfigHandler;
import io.endertech.creativetab.CreativeTabET;
import io.endertech.fluid.ETFluids;
import io.endertech.item.ETItems;
import io.endertech.network.NetworkHandler;
import io.endertech.proxy.CommonProxy;
import io.endertech.reference.Reference;
import io.endertech.util.BlockHelper;
import io.endertech.util.LogHelper;
import io.endertech.util.ModuleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;
import java.io.File;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, certificateFingerprint = Reference.FINGERPRINT, dependencies = "after:ThermalExpansion@[3.0.0.2,)")
public class EnderTech
{
    @SuppressWarnings("unused")
    @Mod.Instance(Reference.MOD_ID)
    public static EnderTech instance;

    @SidedProxy(clientSide = "io.endertech.proxy.ClientProxy", serverSide = "io.endertech.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static final CreativeTabs tabET = new CreativeTabET();

    @EventHandler
    @SuppressWarnings("unused")
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

        // Pulsar module loading
        ModuleHelper.setupModules();
        ModuleHelper.pulsar.preInit(event);

        // Network handler
        NetworkHandler.init();

        // Version checker

        // Tick handlers
        proxy.registerTickerHandlers();

        // KeyBinding handler
        if (FMLCommonHandler.instance().getSide().isClient())
        {
            KeyBindingHandler.init();
        }

        // Sound handler

        // Blocks
        ETBlocks.init();

        // Items
        ETItems.init();

        // Fluids
        ETFluids.init();

        LogHelper.debug("preInit complete");
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void init(FMLInitializationEvent event)
    {
        // Pulsar loading
        ModuleHelper.pulsar.init(event);

        //LogHelper.debug("DIRT BLOCK >> " + Block.dirt.getUnlocalizedName());
        //LogHelper.info("Sin 360: " + MathHelper.sin(2 * MathHelper.pi));

        // Renderers
        proxy.registerTESRs();

        proxy.registerRenderers();

        LogHelper.debug("init complete");

        // IMC
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void postInit(FMLPostInitializationEvent event)
    {
        // Pulsar loading
        ModuleHelper.pulsar.postInit(event);

        if (FMLCommonHandler.instance().getSide().isClient())
        {
            MinecraftForge.EVENT_BUS.register(new GUIEventHandler((Minecraft.getMinecraft())));
        }

        BlockHelper.initSoftBlocks();

        LogHelper.info("Registering recipes");
        if (Loader.isModLoaded("ThermalExpansion"))
        {
            ItemStack capacitorReinforced = GameRegistry.findItemStack("ThermalExpansion", "capacitorReinforced", 1);
            ItemStack capacitorResonant = GameRegistry.findItemStack("ThermalExpansion", "capacitorResonant", 1);
            ItemStack tesseract = new ItemStack(GameRegistry.findBlock("ThermalExpansion", "Tesseract"));

            ItemStack enderEyeStack = new ItemStack(Items.ender_eye);

            GameRegistry.addRecipe(new ShapedOreRecipe(ETItems.toolExchangerRedstone, new Object[] {"XEX", "CTC", "XCX", 'E', enderEyeStack, 'C', capacitorReinforced, 'T', tesseract}));
            GameRegistry.addRecipe(new ShapedOreRecipe(ETItems.toolExchangerResonant, new Object[] {"XEX", "CTC", "XCX", 'E', enderEyeStack, 'C', capacitorResonant, 'T', tesseract}));
        }
    }
}
