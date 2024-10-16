# Ancient Warfare 2 [![Discord](https://img.shields.io/discord/440863937777369088.svg?colorB=7289DA&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAHYAAABWAgMAAABnZYq0AAAACVBMVEUAAB38%2FPz%2F%2F%2F%2Bm8P%2F9AAAAAXRSTlMAQObYZgAAAAFiS0dEAIgFHUgAAAAJcEhZcwAACxMAAAsTAQCanBgAAAAHdElNRQfhBxwQJhxy2iqrAAABoElEQVRIx7WWzdGEIAyGgcMeKMESrMJ6rILZCiiBg4eYKr%2Fd1ZAfgXFm98sJfAyGNwno3G9sLucgYGpQ4OGVRxQTREMDZjF7ILSWjoiHo1n%2BE03Aw8p7CNY5IhkYd%2F%2F6MtO3f8BNhR1QWnarCH4tr6myl0cWgUVNcfMcXACP1hKrGMt8wcAyxide7Ymcgqale7hN6846uJCkQxw6GG7h2MH4Czz3cLqD1zHu0VOXMfZjHLoYvsdd0Q7ZvsOkafJ1P4QXxrWFd14wMc60h8JKCbyQvImzlFjyGoZTKzohwWR2UzSONHhYXBQOaKKsySsahwGGDnb%2FiYPJw22sCqzirSULYy1qtHhXGbtgrM0oagBV4XiTJok3GoLoDNH8ooTmBm7ZMsbpFzi2bgPGoXWXME6XT%2BRJ4GLddxJ4PpQy7tmfoU2HPN6cKg%2BledKHBKlF8oNSt5w5g5o8eXhu1IOlpl5kGerDxIVT%2BztzKepulD8utXqpChamkzzuo7xYGk%2FkpSYuviLXun5bzdRf0Krejzqyz7Z3p0I1v2d6HmA07dofmS48njAiuMgAAAAASUVORK5CYII%3D)](https://discord.gg/jNhkDfU) [![Build Status](https://github.com/P3pp3rF1y/AncientWarfare2/workflows/Mod%20Build/badge.svg)](https://github.com/P3pp3rF1y/AncientWarfare2/actions?workflow=Mod+Build)
This is a rewrite of Ancient Warfare mod for Minecraft for Minecraft versions 1.20

Modules initially available will be:

* Core (mandatory for all sub-modules)  

* Automation -- adds quarries, automated farms, tree-farms, etc.  

* NPCs -- adds workers, combat npcs. Complete faction-based NPC system for world-gen.  

* Structures -- the AW template system, packaged as a stand-alone. Has dynamically loaded plugins to handle interaction with other AW modules.  

* Vehicles -- Adds vehicles for transportation and warfare.

Latest and historical releases can be [found on CurseForge](https://minecraft.curseforge.com/projects/ancient-warfare-2/files).

Development unstable releases can be downloaded from Github packages.  
[Download mod builds](https://github.com/P3pp3rF1y/AncientWarfare2/packages/36069)

## Development

In order to develop on this project you will need to:  

1) Download the repository into a local folder.  

2) Locate this folder, run `gradlew setupDecompWorkspace` (or `./gradlew setupDecompWorkspace`). This sets up base minecraft and minecraftforge libraries and their dependencies.

3) Run `gradlew idea` or `gradlew eclipse` based on the IDE you use to setup project files.

At this point it should be completely setup to begin development.  
  


## License

Ancient Warfare 2.x - A mod for Minecraft
Copyright (C) 2013-2015  Shadowmage
  
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
  
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
  
You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
