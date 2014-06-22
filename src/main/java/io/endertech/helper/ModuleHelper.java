package io.endertech.helper;

import io.drakon.pulsar.control.PulseManager;

import static io.endertech.lib.Reference.MOD_ID;

/**
 * Helper to connect to Project Pulsar.
 *
 * @author Arkan <arkan@drakon.io>
 */
public class ModuleHelper {

    private ModuleHelper() {} // No touchy.

    private static boolean modulesConfigured = false;
    public static final PulseManager pulsar = new PulseManager(MOD_ID, MOD_ID + "-Modules");

    public static void setupModules() {
        if (modulesConfigured) throw new RuntimeException("Someone called ModuleHelper.setupModules() again!");

        // TODO: Register modules with Pulsar here.
        // e.g. pulsar.registerPulse(new PulseClass());

        modulesConfigured = true;
    }

}
