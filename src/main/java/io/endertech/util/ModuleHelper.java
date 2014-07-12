package io.endertech.util;

import io.drakon.pulsar.control.PulseManager;
import io.endertech.modules.TestPulse;
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

        // TODO: Register modules with Pulsar here.
        // e.g. pulsar.registerPulse(new PulseClass());
        pulsar.registerPulse(new TestPulse());

        modulesConfigured = true;
    }

}
