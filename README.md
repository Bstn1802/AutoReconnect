# AutoReconnect [1.17.1][Fabric][Client]

### Description

This mod will automatically try to reconnect you back to a server if you got disconnected.
By default, it will make 4 attempts after 3, 10, 30 and 60 seconds.

### Features

* Multiple individually delayed reconnect attempts
* Toggle for infinitely many reconnect attempts with the last configured delay
* Displays a countdown on the disconnect screen
* Can automatically send commands after reconnecting to a specific server
* Allows you to exit the disconnect screen quickly by pressing the escape key
* Customizable
    * Amount of attempts
    * Delay between each attempt
* Support for ModMenu and AuthMe
* Support for servers with lobbies

### Installation

1. Download and install [Fabric](https://fabricmc.net/use/) for the latest client version
2. Download [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) and put the .jar file into the mods folder
3. Download this mod and put the .jar file into the same folder

### Compatibility

* [ModMenu](https://www.curseforge.com/minecraft/mc-mods/modmenu) <br>
  Properly shows the mod icon, name and author
  and provides a link for the curseforge project page and the github issues page\
  *New!* Graphical user interface to configure the mod in game
* [AuthMe](https://www.curseforge.com/minecraft/mc-mods/auth-me) <br>
  Pauses the countdown if you click on the Re-authenticate button to revalidate the session of the game

### Common questions

* _Can I change the delay?_<br>
  Yes, you can configure the mods config file manually or use ModMenu(_recommended_)
* _Forge version?_<br>
  Simply no. I am not interested in developing mods using Forge.
* _Version for 1.13.x or lower?_<br>
  Fabric does not exist for those versions.

### Screenshots

![countdown](src/main/resources/assets/countdown.png)

![failed](src/main/resources/assets/failed.png)

### License

This mod is available under the CC0 license.
Feel free to learn from it and incorporate it in your own projects.
If you actually just copy code or use this mod in a mod pack I would appreciate it if you mention me
by linking the github page or the curseforge project page.