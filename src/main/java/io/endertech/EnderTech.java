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
import io.endertech.item.ETItems;
import io.endertech.multiblock.block.BlockMultiblockGlass;
import io.endertech.multiblock.block.BlockTankController;
import io.endertech.multiblock.block.BlockTankPart;
import io.endertech.network.PacketHandler;
import io.endertech.network.PacketKeyPressed;
import io.endertech.network.PacketTile;
import io.endertech.proxy.CommonProxy;
import io.endertech.reference.Reference;
import io.endertech.util.fluid.BucketHandler;
import io.endertech.util.helper.BlockHelper;
import io.endertech.util.helper.LogHelper;
import io.endertech.util.helper.ModuleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
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

        // Packet handler
        PacketHandler.instance.init();

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
        BucketHandler.initialize();

        LogHelper.debug("preInit complete");
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void init(FMLInitializationEvent event)
    {
        // Packets
        PacketTile.init();
        PacketKeyPressed.init();

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

        // Packet postInit
        PacketHandler.instance.postInit();

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
            ItemStack enderiumIngot = GameRegistry.findItemStack("ThermalFoundation", "ingotEnderium", 1);
            ItemStack electrumIngot = GameRegistry.findItemStack("ThermalFoundation", "ingotElectrum", 1);
            ItemStack machineResonant = new ItemStack(GameRegistry.findBlock("ThermalExpansion", "Frame"), 1, 3);
            ItemStack hardenedGlass = new ItemStack(GameRegistry.findBlock("ThermalExpansion", "Glass"));
            ItemStack tankResonant = new ItemStack(GameRegistry.findBlock("ThermalExpansion", "Tank"), 1, 4);

            ItemStack enderEyeStack = new ItemStack(Items.ender_eye);

            GameRegistry.addRecipe(new ShapedOreRecipe(ETItems.toolExchangerRedstone, new Object[] {"XEX", "ITI", "XCX", 'E', enderEyeStack, 'I', electrumIngot, 'C', capacitorReinforced, 'T', tesseract}));
            GameRegistry.addRecipe(new ShapedOreRecipe(ETItems.toolExchangerResonant, new Object[] {"XEX", "ITI", "XCX", 'E', enderEyeStack, 'I', enderiumIngot, 'C', capacitorResonant, 'T', tesseract}));

            ItemStack enderTankFrame = new ItemStack(BlockTankPart.itemBlockTankFrame.getItem(), 8, BlockTankPart.itemBlockTankFrame.getItemDamage());
            ItemStack enderTankEnergyInput = new ItemStack(BlockTankPart.itemBlockTankEnergyInput.getItem(), 8, BlockTankPart.itemBlockTankEnergyInput.getItemDamage());
            ItemStack enderTankValve = new ItemStack(BlockTankPart.itemBlockTankValve.getItem(), 8, BlockTankPart.itemBlockTankValve.getItemDamage());
            ItemStack enderTankGlass = new ItemStack(BlockMultiblockGlass.itemBlockMultiblockGlass.getItem(), 16, BlockMultiblockGlass.itemBlockMultiblockGlass.getItemDamage());
            ItemStack enderTankController = BlockTankController.itemBlockTankController;

            GameRegistry.addRecipe(enderTankFrame, new Object[] {"IEI", "EFE", "IEI", 'I', enderiumIngot, 'F', machineResonant, 'E', enderEyeStack});
            GameRegistry.addRecipe(enderTankEnergyInput, new Object[] {"ICI", "EFE", "ITI", 'I', enderiumIngot, 'F', machineResonant, 'C', capacitorResonant, 'T', tesseract, 'E', enderEyeStack});
            GameRegistry.addRecipe(enderTankValve, new Object[] {"IAI", "EFE", "ITI", 'I', enderiumIngot, 'A', tankResonant, 'F', machineResonant, 'T', tesseract, 'E', enderEyeStack});
            GameRegistry.addRecipe(enderTankController, new Object[] {"IEI", "EFE", "ITI", 'I', enderiumIngot, 'F', machineResonant, 'T', tesseract, 'E', enderEyeStack});
            GameRegistry.addRecipe(enderTankGlass, new Object[] {"GIG", "EFE", "GIG", 'I', enderiumIngot, 'G', hardenedGlass, 'F', machineResonant, 'E', enderEyeStack});
        }
    }
}
