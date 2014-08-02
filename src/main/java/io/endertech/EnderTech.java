package io.endertech;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.block.ETBlocks;
import io.endertech.client.handler.GUIEventHandler;
import io.endertech.client.handler.KeyBindingHandler;
import io.endertech.config.ConfigHandler;
import io.endertech.creativetab.CreativeTabET;
import io.endertech.fluid.ETFluids;
import io.endertech.item.ETItems;
import io.endertech.multiblock.block.BlockMultiblockGlass;
import io.endertech.multiblock.block.BlockTankController;
import io.endertech.multiblock.block.BlockTankPart;
import io.endertech.network.NetworkHandler;
import io.endertech.proxy.CommonProxy;
import io.endertech.reference.Reference;
import io.endertech.util.BlockHelper;
import io.endertech.util.LogHelper;
import io.endertech.util.ModuleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;
import java.io.File;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, certificateFingerprint = Reference.FINGERPRINT, dependencies = "required-after:ThermalExpansion@[1.7.10R4.0.0B1,)")
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

        // Renderers
        proxy.registerTESRs();

        proxy.registerRenderers();

        proxy.registerItemRenderers();

        // Waila
        FMLInterModComms.sendMessage("Waila", "register", "io.endertech.integration.waila.MultiblockWailaProvider.callbackRegister");

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

            ItemStack ironBlock = new ItemStack(Blocks.iron_block);
            ItemStack glassBlock = new ItemStack(Blocks.glass);
            ItemStack bucket = new ItemStack(Items.bucket);

            ItemStack enderTankFrame = new ItemStack(BlockTankPart.itemBlockTankFrame.getItem(), 16, BlockTankPart.itemBlockTankFrame.getItemDamage());
            ItemStack enderTankEnergyInput = new ItemStack(BlockTankPart.itemBlockTankEnergyInput.getItem(), 16, BlockTankPart.itemBlockTankEnergyInput.getItemDamage());
            ItemStack enderTankValve = new ItemStack(BlockTankPart.itemBlockTankValve.getItem(), 16, BlockTankPart.itemBlockTankValve.getItemDamage());
            ItemStack enderTankGlass = new ItemStack(BlockMultiblockGlass.itemBlockMultiblockGlass.getItem(), 16, BlockMultiblockGlass.itemBlockMultiblockGlass.getItemDamage());
            ItemStack enderTankController = BlockTankController.itemBlockTankController;

            GameRegistry.addRecipe(enderTankFrame, new Object[] {"III", "ITI", "IEI", 'I', ironBlock, 'T', tesseract, 'E', enderEyeStack});
            GameRegistry.addRecipe(enderTankEnergyInput, new Object[] {"ICI", "ITI", "IEI", 'I', ironBlock, 'C', capacitorResonant, 'T', tesseract, 'E', enderEyeStack});
            GameRegistry.addRecipe(enderTankValve, new Object[] {"IBI", "ITI", "IEI", 'I', ironBlock, 'B', bucket, 'T', tesseract, 'E', enderEyeStack});
            GameRegistry.addRecipe(enderTankController, new Object[] {"IEI", "ETE", "IEI", 'I', ironBlock, 'T', tesseract, 'E', enderEyeStack});
            GameRegistry.addRecipe(enderTankGlass, new Object[] {"GGG", "GTG", "GEG", 'I', ironBlock, 'G', glassBlock, 'T', tesseract, 'E', enderEyeStack});
        }
    }
}
