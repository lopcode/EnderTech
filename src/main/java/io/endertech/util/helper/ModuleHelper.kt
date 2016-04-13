package io.endertech.util.helper

import io.drakon.pulsar.control.PulseManager
import io.endertech.EnderTech
import io.endertech.modules.dev.DevEnvironmentPulse
import io.endertech.reference.Reference

/**
 * Helper to connect to Project Pulsar.

 * @author Arkan @drakon.io>
 */
object ModuleHelper {

    private var pulsar: PulseManager = PulseManager(Reference.MOD_ID)
    private var modulesConfigured = false

    fun setupModules() {
        if (modulesConfigured) throw RuntimeException("Someone called ModuleHelper.setupModules() again!")

        if (EnderTech.loadDevModeContent) {
            pulsar.registerPulse(DevEnvironmentPulse())
        }

        // TODO: Add modules to load here.

        modulesConfigured = true
    }

}
