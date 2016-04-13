package io.endertech.util.helper

import io.endertech.reference.Reference
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager

object LogHelper {
    private val logger = LogManager.getLogger(Reference.MOD_ID)

    fun log(logLevel: Level, format: String, vararg data: Any) {
        logger.log(logLevel, format, *data)
    }

    fun debug(format: String, vararg data: Any) {
        log(Level.DEBUG, format, *data)
    }

    fun error(format: String, vararg data: Any) {
        log(Level.ERROR, format, *data)
    }

    fun fatal(format: String, vararg data: Any) {
        log(Level.FATAL, format, *data)
    }

    fun info(format: String, vararg data: Any) {
        log(Level.INFO, format, *data)
    }

    fun warn(format: String, vararg data: Any) {
        log(Level.WARN, format, *data)
    }
}
