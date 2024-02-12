# AutoReconnect [1.20.4][Fabric][Client]

## Description

This mod will automatically try to reconnect you back to a server if you got disconnected.
By default, it will make 4 attempts after 3, 10, 30 and 60 seconds.

_Disclaimer:_ Use at your own risk. When using this on a multiplayer server/realm you might want to check with the admins first whether it's okay to use this mod.

## Features

General:
* Additional button on the disconnect screen which will reconnect you without having to go back to the menu first
* Works with QuickPlay
* Configurable through [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu)

AutoReconnect:
* Automatically reconnect after getting disconnected
  * Multiple attempts
  * Individual delays between attempts
  * Infinite attempts (Optional, repeats last attempt)
* Manual reconnect still possible
* Countdown is showing and can be canceled
* Works for servers, realms and even singleplayer

AutoMessage:
* Automatically send messages/commands after reconnecting, e.g. to join a certain lobby or just say hi\
  (Doesn't trigger when (re)connecting manually)
* Delay between messages and before the first one
* Configure messages for each server, realm or singleplayer world (for details see below)

Extras: 
* Supports [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu) and [AuthMe](https://www.curseforge.com/minecraft/mc-mods/auth-me) (for details see below) 
* The disconnect screen (like many other screens) can be exited by pressing escape
* After being disconnected from a singleplayer world, you won't end up on the multiplayer screen. A fix has been implemented for ([Bug MC-46502](https://bugs.mojang.com/browse/MC-45602)) 

## Requirements/Installation/Setup

This mod works on [Fabric](https://fabricmc.net/use/) and requires the [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) and [ClothConfig](https://www.curseforge.com/minecraft/mc-mods/cloth-config)

[Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu) is highly recommended for easy in-game configuration  

1. If you don't have a fabric profile set up, download and install [Fabric](https://fabricmc.net/use/) and set up a profile to your likings
2. Download the following mods and put them in the mod folder of your profile:\
   (Be careful with the versions you download, make sure the mods are for fabric (not forge) and the right version of minecraft)
   * [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
   * [ClothConfig](https://www.curseforge.com/minecraft/mc-mods/cloth-config)
   * AutoReconnect (this mod)
   * [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu) _(optional)_
3. Setup:
   * Start up minecraft and when you see the title screen, click on "Mods"
   * Find and select this mod in the list of mods to your left
   * Click on the configuration button on top of the icon of this mod in the list on the left or the one at the top right if you selected the mod
   * The configuration of this mod should open and look similar to the screenshots below

## Compatibility/Support

* [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu)
  * Properly shows the mod icon, name and author
  and provides a link for the curseforge project page and the github issues page
  * Graphical user interface to configure the mod in-game
* [AuthMe](https://www.curseforge.com/minecraft/mc-mods/auth-me)
  * Countdown for automatic reconnect will stop if you're re-authenticating to revalidate your session

## Details

AutoReconnect:
* Attempts can be configured by adding strictly positive values (delay in seconds) to the list of delays. For each value in that list a delayed attempt will be made to reconnect you.
* Can be disabled by simple not configuring any attempts. Only manual reconnects are possible then.
* The "infinite" flag can be enabled to configure the last attempt to repeat infinitely
* The countdown can be cancelled by pressing the button with the "✕" or by pressing escape

AutoMessages:
* Will only be executed if an automatic reconnect attempt has been made, so you didn't click on the reconnect button yourself or connect the first time.
* Can target multiple specific servers, realms or singleplayer worlds. Create a configuration under the section "AutoMessages" for each one and enter the name of the server, realm or singleplayer world.
* A delay (in milliseconds) can be configured. This delay will be the same between every message and before the first message after the instant you join the world.

## Future plans

* Reconnect from being kicked into a lobby (feature request [issue #22](https://github.com/Bstn1802/AutoReconnect/issues/22))
* Conditional reconnects, e.g. configure the mod not to reconnect when a moderator kicked you (feature request [issue #36](https://github.com/Bstn1802/AutoReconnect/issues/36))
* I'm _not_ planning to port this mod to forge

## Feedback, Suggestions, Bugs & Issues

* For feedback and suggestions please write a comment on [curseforge](https://www.curseforge.com/minecraft/mc-mods/autoreconnect)
* If you found a bug or an issue, write a comment on [curseforge](https://www.curseforge.com/minecraft/mc-mods/autoreconnect) or open an issue on [github](https://github.com/Bstn1802/AutoReconnect/issues). Make sure to give a detailed description of the issue and post the latest logs, or a potential crash report, preferably by using something like [pastebin](https://pastebin.com/).

## Screenshots

![countdown](screenshots/countdown.png)

![failed](screenshots/failed.png)

![manual](screenshots/manual.png)

![config](screenshots/config.png)

_Last one outdated_

## Licence

This mod and its code is available under the GNU Lesser General Public Licence v3.0.

If you use code from this mod or the mod itself in a mod pack I would appreciate it if you would mention me by linking to this page.
