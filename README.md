# AutoReconnect [1.16/1.17][Fabric]

This mod will automatically try to reconnect you back to a server if you got disconnected.
By default, it will make 4 attempts after 3, 10, 30 and 60 seconds.

### Curseforge
Although I also provide downloads here on github as well, please visit the Curseforge page [here](https://www.curseforge.com/minecraft/mc-mods/autoreconnect).
Especially if you have questions, don't open a new issue everytime, but make a comment on the curseforge page instead.
Also read this readme or the description on the curseforge page before.

### Features

* Multiple individually delayed reconnect attempts
* Displays a countdown on the disconnect screen
* Allows you to exit the disconnect screen quickly by pressing the escape key
* Customizable
    * Amount of attempts
    * Delay between each attempt
* Client side commands
    * `/autoreconnect reload` Reloads the config and displays the settings in chat
    * `/autoreconnect config [<delayList>]` Sets the delay between each attempt<br>
    `[<delayList>]` must be a Nbt List Tag containing Integers, e.g. `[3, 10, 30, 60]` or `[I;3, 10, 30, 60]`
* Support for several mods

### Installation

1. [Download](https://fabricmc.net/use/) and install Fabric
2. Download [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) and put the jar file into the mods folder
3. Do the same for this mod

### Compatibility

* [ModMenu](https://www.curseforge.com/minecraft/mc-mods/modmenu) <br>
  Properly shows the mod icon, name and author
  and provides a link for the curseforge project page and the github issues page
* [AuthMe](https://www.curseforge.com/minecraft/mc-mods/auth-me) <br>
  Cancels the countdown if you click on the Re-authenticate button to revalidate the session of the game

### Screenshots

![countdown](src/main/resources/assets/countdown.png)

![failed](src/main/resources/assets/failed.png)

### License

This mod is available under the CC0 license.
Feel free to learn from it and incorporate it in your own projects.
If you actually just copy code or use this mod in a mod pack I would appreciate it if you mention me
by linking the github page or the curseforge project page.
