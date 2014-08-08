package io.endertech.util.helper;

import io.drakon.pulsar.control.PulseManager;
import io.endertech.config.GeneralConfig;
import io.endertech.modules.dev.DevEnvironmentPulse;
import net.minecraft.launchwrapper.Launch;
import static io.endertech.reference.Reference.MOD_ID;

/**
 * Helper to connect to Project Pulsar.
 *
 * @author Arkan <arkan@drakon.io>
 */
public class ModuleHelper
{
    private ModuleHelper() {} // No touchy.

    private static boolean modulesConfigured = false;
    public static final PulseManager pulsar = new PulseManager(MOD_ID, MOD_ID + "-Modules");

    public static void setupModules()
    {
        if (modulesConfigured) throw new RuntimeException("Someone called ModuleHelper.setupModules() again!");

        if ((Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment") || GeneralConfig.forceLoadDevContent)
        {
            pulsar.registerPulse(new DevEnvironmentPulse());
        }

        modulesConfigured = true;
    }

}
