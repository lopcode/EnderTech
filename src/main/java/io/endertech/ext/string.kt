package io.endertech.ext

import net.minecraft.util.StatCollector

/**
 * String extension methods
 */

fun String.i18n(): String = StatCollector.translateToLocal(this)

fun String.toI18NFormat() = I18NFormat(this)

class I18NFormat(private val formatStr: String)
{
    fun format(vararg data:Any):String = StatCollector.translateToLocalFormatted(formatStr, *data)
}
