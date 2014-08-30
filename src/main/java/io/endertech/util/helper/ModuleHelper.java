package io.endertech.util.helper;

import io.drakon.pulsar.control.PulseManager;
import io.endertech.EnderTech;
import io.endertech.modules.dev.DevEnvironmentPulse;
import static io.endertech.reference.Reference.MOD_ID;

/**
 * Helper to connect to Project Pulsar.
 *
 * @author Arkan <arkan@drakon.io>
 */
public class ModuleHelper
{
    public static final PulseManager pulsar = new PulseManager(MOD_ID, MOD_ID + "-Modules");
    private static boolean modulesConfigured = false;

    private ModuleHelper() {} // No touchy.

    public static void setupModules()
    {
        if (modulesConfigured) throw new RuntimeException("Someone called ModuleHelper.setupModules() again!");

        if (EnderTech.loadDevModeContent)
        {
            pulsar.registerPulse(new DevEnvironmentPulse());
        }

        modulesConfigured = true;
    }

}
