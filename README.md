![EnderTech Logo](https://raw.githubusercontent.com/Drayshak/EnderTech/d838d9282bd30014de1fbc4772e0522d1bb30fa8/src/main/resources/assets/endertech/textures/logo.png)

#EnderTech

##Introduction
This is my first foray in to Minecraft modding. It's intended to be a tech and Ender themed mod, starting at around Thermal Expansion's end-game. It will contain powerful, relatively expensive mechanics, with some configurability. The defaults will maintain a balance that makes my own game more fun.

Some features are often in development, so try to use Promoted builds in your world. It's still no guarantee that a recipe or cost won't change, but I'll try to make sure things carry over where possible.

Unless we talk about it first and I agree, I'm very unlikely to accept pull requests. GitHub is used to show the source and manage issues alone.

Curse link: http://www.curse.com/mc-mods/minecraft/223428-endertech

__FAQs__, progress, modpack permissions / etc: https://github.com/Drayshak/EnderTech/wiki

If you want to talk about the mod, there's an IRC channel on Esper: #EnderTech

##License and terms
Things I want you to do:
* Learn from the source,
  * Include a thank-you, or put me in your credits (as "Drayshak"),
* Include it in a modpack (public or private),
* If you're feeling generous:
  * Tell me you enjoy using the mod in IRC (Esper/#EnderTech) or on Twitter ([@Drayshak](https://twitter.com/drayshak)),
* Enjoy the mod.

Things I don't want you to do:
* Copy large sections of source (functions and ideas are examples of things that are, generally, fine),
* Redistribute custom built jars (all the jars on Hydria are signed appropriately),
  * Building your own jar is fine, but don't make it public,
  * Redistributing a signed jar from Hydria is fine.
* Copy ideas and pass them off as your own,
* Make money from the mod (no paywalls, adf.ly links, charging for access to items or any other bullshit like that).

Formally, code in this project is licensed under the BSD 2-clause license, and art other assets are licensed under the [Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International (CC BY-NC-ND 4.0)](http://creativecommons.org/licenses/by-nc-nd/4.0/) license unless otherwise stated.

### Code license
Copyright (c) 2014, Sky Welch
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

##Documentation
Blocks, items and mechanics (including recipes and usage) are documented as they're completed in the [Wiki](https://github.com/Drayshak/EnderTech/wiki).

If you want to see in-development content you must enable it in the 'general.cfg' config file after one run. Be warned: this in-development content will likely disappear or break your world!.

##Builds
Signed jars are available on CurseForge and Hydria. There are two types of build, Promoted and Dev. Promoted builds are on both services, but Dev builds are only available on Jenkins. Use a Promoted build first.

Promoted: http://www.curse.com/mc-mods/minecraft/223428-endertech

Dev builds (currently *unsigned*): http://hopper.bunnies.io/job/EnderTech/

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
