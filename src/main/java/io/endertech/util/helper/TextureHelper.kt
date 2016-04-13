package io.endertech.util.helper

object TextureHelper {
    fun metaToType(meta: Int): String {
        if (meta == 0)
            return "Creative"
        else if (meta == 1)
            return "Redstone"
        else if (meta == 2) return "Resonant"

        return "Unknown"
    }
}
