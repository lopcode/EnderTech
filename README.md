![EnderTech Logo](https://raw.githubusercontent.com/carrotengineer/EnderTech/d838d9282bd30014de1fbc4772e0522d1bb30fa8/src/main/resources/assets/endertech/textures/logo.png)

# EnderTech

## No longer in active development!

## Introduction
This is my first foray in to Minecraft modding. It's intended to be a tech and Ender themed mod, starting at around Thermal Expansion's end-game. It will contain powerful, relatively expensive mechanics, with some configurability. The defaults will maintain a balance that makes my own game more fun.

Some features are often in development, so try to use Promoted builds in your world. It's still no guarantee that a recipe or cost won't change, but I'll try to make sure things carry over where possible.

Unless we talk about it first and I agree, I'm very unlikely to accept pull requests. GitHub is used to show the source and manage issues alone.

Curse link: http://www.curse.com/mc-mods/minecraft/223428-endertech

__FAQs__, progress, modpack permissions / etc: https://github.com/carrotengineer/EnderTech/wiki

If you want to talk about the mod, there's an IRC channel on Esper: #EnderTech

## Terms
Things I want you to do:
* Learn from the source,
  * Include a thank-you, or put me in your credits (as "voxelcarrot"),
* Include it in a modpack (public or private),
* If you're feeling generous:
  * Tell me you enjoy using the mod in IRC (Esper/#EnderTech) or on Twitter ([@carrotengineer](https://twitter.com/carrotengineer)),
* Enjoy the mod.

Things I don't want you to do:
* Copy large sections of source (functions and ideas are examples of things that are, generally, fine),
* Redistribute custom built jars (all the jars on Hopper are signed appropriately),
  * Building your own jar is fine, but don't make it public,
  * Redistributing a signed jar from Hopper is fine.
* Copy ideas and pass them off as your own,
* Make money from the mod (no paywalls, adf.ly links, charging for access to items or any other bullshit like that).

### Licenses
The source code of this project is licensed under the terms of the ISC license, listed in the [LICENSE](LICENSE.md) file. A concise summary of the ISC license is available at [choosealicense.org](http://choosealicense.com/licenses/isc/).

Art and other assets are licensed under the terms of the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International (CC BY-NC-ND 4.0) license (also linked in the [LICENSE](LICENSE.md) file).

##Documentation
Blocks, items and mechanics (including recipes and usage) are documented as they're completed in the [Wiki](https://github.com/carrotengineer/EnderTech/wiki).

If you want to see in-development content you must enable it in the 'general.cfg' config file after one run. Be warned: this in-development content will likely disappear or break your world!.

##Builds
Signed jars are available on CurseForge and Hopper. There are two types of build, Promoted and Dev. Promoted builds are on both services, but Dev builds are only available on Jenkins. Use a Promoted build first.

Promoted: http://www.curse.com/mc-mods/minecraft/223428-endertech

Dev builds (signed): http://hopper.bunnies.io/job/EnderTech/

##Building
This mod uses Forge's Gradle wrapper for pretty easy setup and building. There are better guides around the internet for using it, and I don't do anything particularly special.

The general idea:
* **Setup**: `gradlew [setupDevWorkspace/setupDecompWorkspace] [idea/eclipse]`
* **Building**: `gradlew build`

If you run in to odd Gradle issues, doing `gradlew clean` usually fixes it.

## With thanks to
* Pahimar (Modding guides and [EE3](https://github.com/pahimar/Equivalent-Exchange-3)),
* Team CoFH ([Thermal Expansion](http://teamcofh.com/)),
* Azanor ([Thaumcraft](http://www.minecraftforum.net/topic/2011841-thaumcraft-41114-updated-2052014/)),
* ErogenousBeef (huge help with multiblocks, connected textures, [BigReactors / BeefCore](https://github.com/erogenousbeef)),
* [OpenBlocks](https://github.com/OpenMods/OpenBlocks) (Rendering help),
* [Arkan](https://github.com/emberwalker) (Build server),
* [RWTema](http://www.patreon.com/rwtema) (Textures).
