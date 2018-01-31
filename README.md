Ancient Warfare 2
===============

In order to develop on this project you will need to:  

1) Download the repository into a local folder.  

2) Locate this folder, run `gradlew setupDecompWorkspace` (or `./gradlew setupDecompWorkspace`). This sets up base minecraft and minecraftforge libraries and their dependencies.

3) Run `gradlew idea` or `gradlew eclipse` based on the IDE you use to setup project files.

At this point it should be completely setup to begin development.  
  
This is a rewrite of Ancient Warfare mod for Minecraft for Minecraft versions 1.12.x+.

 
Modules initially available will be:

* Core (mandatory for all sub-modules)  

* Automation -- adds quarries, automated farms, tree-farms, etc.  

* NPCs -- adds workers, combat npcs. Complete faction-based NPC system for world-gen.  

* Structures -- the AW template system, packaged as a stand-alone. Has dynamically loaded plugins to handle interaction with other AW modules.  

* Vehicles -- Adds vehicles for transportation and warfare (work-in-progress).

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
