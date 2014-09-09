AncientWarfare2
===============
In order to develop on this project you will need to:  
Download the repository into a local folder.  
Locate this folder, run gradle_update.bat  
This should download all dependencies and setup proper .classpath  
Import the project into eclipse from the downloaded folder/repository.  
At this point it should be completely setup to begin development.  
  
This is a rewrite of Ancient Warfare mod for Minecraft for Minecraft versions 1.7.x +  
Most features have been rewritten. Mod has been modularized -- can use individual modules independent of the others.  
  
Modules initially available will be:
* Core (mandatory for all sub-modules)  
* Automation -- adds quarries, automated farms, tree-farms, etc.  
* NPCs -- adds workers, combat npcs.  complete faction-based npc system for world-gen.  
* Structures -- the AW template system, packaged as a stand-alone.  Has dynamically loaded plugins to handle interaction with other AW modules.  
  
  
Initial Releases will not have any Vehicles module -- that will be coming later in the development cycle.
