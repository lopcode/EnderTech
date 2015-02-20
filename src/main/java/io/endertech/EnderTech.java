package io.endertech;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import io.endertech.block.BlockChargePad;
import io.endertech.block.BlockHealthPad;
import io.endertech.block.ETBlocks;
import io.endertech.client.handler.GUIEventHandler;
import io.endertech.client.handler.KeyBindingHandler;
import io.endertech.config.ConfigHandler;
import io.endertech.config.GeneralConfig;
import io.endertech.creativetab.CreativeTabET;
import io.endertech.gui.GuiHandler;
import io.endertech.item.ETItems;
import io.endertech.multiblock.block.BlockMultiblockGlass;
import io.endertech.multiblock.block.BlockTankController;
import io.endertech.multiblock.block.BlockTankPart;
import io.endertech.network.PacketHandler;
import io.endertech.network.PacketKeyPressed;
import io.endertech.network.PacketTile;
import io.endertech.proxy.CommonProxy;
import io.endertech.reference.Reference;
import io.endertech.util.Exchange;
import io.endertech.util.fluid.BucketHandler;
import io.endertech.util.helper.BlockHelper;
import io.endertech.util.helper.LocalisationHelper;
import io.endertech.util.helper.LogHelper;
import io.endertech.util.helper.ModuleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import java.io.File;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION_NUMBER, certificateFingerprint = Reference.FINGERPRINT, dependencies = "after:ThermalExpansion@[1.7.10R4.0.0B1,)")
public class EnderTech
{
    public static final CreativeTabs tabET = new CreativeTabET();
    public static final GuiHandler guiHandler = new GuiHandler();
    @SuppressWarnings("unused")
    @Mod.Instance(Reference.MOD_ID)
    public static EnderTech instance;
    @SidedProxy(clientSide = "io.endertech.proxy.ClientProxy", serverSide = "io.endertech.proxy.CommonProxy")
    public static CommonProxy proxy;
    public static boolean loadDevModeContent = false;
    public static Item capacitor;

    @EventHandler
    @SuppressWarnings("unused")
    public void invalidFingerprint(FMLFingerprintViolationEvent event)
    {
        if (Reference.FINGERPRINT.equals("@FINGERPRINT@"))
        {
            LogHelper.warn(LocalisationHelper.localiseString("warning.fingerprint.missing"));
        } else
        {
            LogHelper.fatal(LocalisationHelper.localiseString("error.fingerprint.tampered"));
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void preInit(FMLPreInitializationEvent event)
    {
        // Configuration
        ConfigHandler.init(event.getModConfigurationDirectory().getAbsolutePath() + File.separator + Reference.CHANNEL_NAME.toLowerCase() + File.separator);

        LogHelper.debug("Loaded config");

        if ((Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment") || GeneralConfig.forceLoadDevContent)
        {
            loadDevModeContent = true;
        }

        // Pulsar module loading
        //ModuleHelper.setupModules();
        //ModuleHelper.pulsar.preInit(event);

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
        // GUI
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, guiHandler);

        // Packets
        PacketTile.init();
        PacketKeyPressed.init();

        // Pulsar loading
        //ModuleHelper.pulsar.init(event);

        // Renderers
        proxy.registerTESRs();

        proxy.registerRenderers();

        proxy.registerItemRenderers();

        // Waila
        FMLInterModComms.sendMessage("Waila", "register", "io.endertech.integration.waila.MultiblockWailaProvider.callbackRegister");
        FMLInterModComms.sendMessage("Waila", "register", "io.endertech.integration.waila.GenericWailaProvider.callbackRegister");

        LogHelper.debug("init complete");

        // IMC

        MinecraftForge.EVENT_BUS.register(proxy);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void postInit(FMLPostInitializationEvent event)
    {
        // Pulsar loading
        //ModuleHelper.pulsar.postInit(event);

        // Packet postInit
        PacketHandler.instance.postInit();

        if (FMLCommonHandler.instance().getSide().isClient())
        {
            MinecraftForge.EVENT_BUS.register(new GUIEventHandler((Minecraft.getMinecraft())));
        }

        BlockHelper.initSoftBlocks();
        Exchange.initSpecialBlocks();

        LogHelper.info(LocalisationHelper.localiseString("info.postinit.recipes"));
        if (Loader.isModLoaded("ThermalExpansion"))
        {
            ItemStack capacitorReinforced = GameRegistry.findItemStack("ThermalExpansion", "capacitorReinforced", 1);
            ItemStack capacitorResonant = GameRegistry.findItemStack("ThermalExpansion", "capacitorResonant", 1);
            capacitor = capacitorResonant.getItem();
            ItemStack powerCoilElectrumStack = GameRegistry.findItemStack("ThermalExpansion", "powerCoilElectrum", 1);
            ItemStack tesseract = new ItemStack(GameRegistry.findBlock("ThermalExpansion", "Tesseract"));
            ItemStack enderiumIngot = GameRegistry.findItemStack("ThermalFoundation", "ingotEnderium", 1);
            ItemStack electrumIngot = GameRegistry.findItemStack("ThermalFoundation", "ingotElectrum", 1);
            ItemStack enderiumNugget = GameRegistry.findItemStack("ThermalFoundation", "nuggetEnderium", 1);
            ItemStack machineResonant = new ItemStack(GameRegistry.findBlock("ThermalExpansion", "Frame"), 1, 3);
            ItemStack machineRedstone = new ItemStack(GameRegistry.findBlock("ThermalExpansion", "Frame"), 1, 2);
            ItemStack hardenedGlass = new ItemStack(GameRegistry.findBlock("ThermalExpansion", "Glass"));
            ItemStack tankResonant = new ItemStack(GameRegistry.findBlock("ThermalExpansion", "Tank"), 1, 4);

            ItemStack enderEyeStack = new ItemStack(Items.ender_eye);
            ItemStack goldenApple = new ItemStack(Items.golden_apple);

            GameRegistry.addRecipe(new ShapedOreRecipe(ETItems.toolExchangerRedstone, new Object[] {"XEX", "ITI", "XCX", 'E', enderEyeStack, 'I', electrumIngot, 'C', capacitorReinforced, 'T', tesseract}));
            GameRegistry.addRecipe(new ShapedOreRecipe(ETItems.toolExchangerResonant, new Object[] {"XEX", "ITI", "XCX", 'E', enderEyeStack, 'I', enderiumIngot, 'C', capacitorResonant, 'T', tesseract}));
            GameRegistry.addRecipe(new ShapedOreRecipe(ETItems.toolExchangerResonant, new Object[] {"XSX", "IEI", "XCX", 'S', enderEyeStack, 'E', ETItems.toolExchangerRedstone, 'I', enderiumIngot, 'C', capacitorResonant, 'T', tesseract}));

            ItemStack enderTankFrame = new ItemStack(BlockTankPart.itemBlockTankFrame.getItem(), 8, BlockTankPart.itemBlockTankFrame.getItemDamage());
            ItemStack enderTankEnergyInput = new ItemStack(BlockTankPart.itemBlockTankEnergyInput.getItem(), 8, BlockTankPart.itemBlockTankEnergyInput.getItemDamage());
            ItemStack enderTankValve = new ItemStack(BlockTankPart.itemBlockTankValve.getItem(), 8, BlockTankPart.itemBlockTankValve.getItemDamage());
            ItemStack enderTankGlass = new ItemStack(BlockMultiblockGlass.itemBlockMultiblockGlass.getItem(), 16, BlockMultiblockGlass.itemBlockMultiblockGlass.getItemDamage());
            ItemStack enderTankController = BlockTankController.itemBlockTankController;

            GameRegistry.addRecipe(enderTankFrame, new Object[] {"IEI", "EFE", "IEI", 'I', enderiumNugget, 'F', machineResonant, 'E', enderEyeStack});
            GameRegistry.addRecipe(enderTankEnergyInput, new Object[] {"ICI", "EFE", "ITI", 'I', enderiumNugget, 'F', machineResonant, 'C', capacitorResonant, 'T', tesseract, 'E', enderEyeStack});
            GameRegistry.addRecipe(enderTankValve, new Object[] {"IAI", "EFE", "ITI", 'I', enderiumNugget, 'A', tankResonant, 'F', machineResonant, 'T', tesseract, 'E', enderEyeStack});
            GameRegistry.addRecipe(enderTankController, new Object[] {"IEI", "EFE", "ITI", 'I', enderiumNugget, 'F', machineResonant, 'T', tesseract, 'E', enderEyeStack});
            GameRegistry.addRecipe(enderTankGlass, new Object[] {"GIG", "EFE", "GIG", 'I', enderiumNugget, 'G', hardenedGlass, 'F', machineResonant, 'E', enderEyeStack});

            GameRegistry.addRecipe(BlockChargePad.itemChargePadResonant, new Object[] {"IEI", "CFC", "IAI", 'I', enderiumIngot, 'F', machineResonant, 'E', enderEyeStack, 'C', powerCoilElectrumStack, 'T', tesseract, 'A', capacitorResonant});
            GameRegistry.addRecipe(BlockChargePad.itemChargePadRedstone, new Object[] {"IEI", "CFC", "IAI", 'I', electrumIngot, 'F', machineRedstone, 'E', enderEyeStack, 'C', powerCoilElectrumStack, 'T', tesseract, 'A', capacitorReinforced});

            GameRegistry.addRecipe(BlockHealthPad.itemHealthPadResonant, new Object[] {"IEI", "CFC", "IAI", 'I', enderiumIngot, 'F', machineResonant, 'E', enderEyeStack, 'C', goldenApple, 'T', tesseract, 'A', capacitorResonant});
            GameRegistry.addRecipe(BlockHealthPad.itemHealthPadRedstone, new Object[] {"IEI", "CFC", "IAI", 'I', electrumIngot, 'F', machineRedstone, 'E', enderEyeStack, 'C', goldenApple, 'T', tesseract, 'A', capacitorReinforced});
        } else {
            LogHelper.warn(LocalisationHelper.localiseString("warning.thermalexpansion.missing"));
        }
    }
}
