package net.shadowmage.ancientwarfare.structure.town;

import net.shadowmage.ancientwarfare.structure.town.TownTemplate.TownStructureEntry;
import net.shadowmage.ancientwarfare.structure.town.TownTemplate.TownWallEntry;

public class TownTestGenerator
{

public static TownTemplate testTemplate;

public static void load()
  {
  testTemplate = new TownTemplate();  
  testTemplate.setTownTypeName("testTown");
  testTemplate.getStructureEntries().add(new TownStructureEntry("village_church", 1, 3));
  testTemplate.getStructureEntries().add(new TownStructureEntry("village_garden_large", 2, 4));
  testTemplate.getStructureEntries().add(new TownStructureEntry("village_garden_small", 2, 4));
  testTemplate.getStructureEntries().add(new TownStructureEntry("village_house_garden", 5, 8));
  testTemplate.getStructureEntries().add(new TownStructureEntry("village_house", 5, 8));
  testTemplate.getStructureEntries().add(new TownStructureEntry("village_hut1", 5, 8));
  testTemplate.getStructureEntries().add(new TownStructureEntry("village_hut2", 5, 8));
  testTemplate.getStructureEntries().add(new TownStructureEntry("village_library", 5, 8));
  testTemplate.setTownHallEntry(new TownStructureEntry("town_hall1", 1, 1));
  testTemplate.setMinSize(90);
  testTemplate.setMaxSize(128);
  testTemplate.setWallStyle(1);
  testTemplate.setWallSize(7);
  testTemplate.addWall(new TownWallEntry("wall_corner1", "corner", 0, 10));
  testTemplate.addWall(new TownWallEntry("wall_straight1", "wall", 1, 10));
  testTemplate.setTownBlockSize(30);
  testTemplate.setTownPlotSize(3);
  }

}
