package net.shadowmage.ancientwarfare.structure.town;

import net.shadowmage.ancientwarfare.structure.town.TownTemplate.TownStructureEntry;

public class TownTestGenerator
{

public static TownTemplate testTemplate;

public static void load()
  {
  testTemplate = new TownTemplate("testTown");  
  testTemplate.getStructureEntries().add(new TownStructureEntry("village_church", 1, 1));
  testTemplate.getStructureEntries().add(new TownStructureEntry("village_garden_large", 1, 2));
  testTemplate.getStructureEntries().add(new TownStructureEntry("village_garden_small", 1, 2));
  testTemplate.getStructureEntries().add(new TownStructureEntry("village_house_garden", 1, 2));
  testTemplate.getStructureEntries().add(new TownStructureEntry("village_house", 3, 3));
  testTemplate.getStructureEntries().add(new TownStructureEntry("village_hut1", 2, 5));
  testTemplate.getStructureEntries().add(new TownStructureEntry("village_hut2", 2, 5));
  testTemplate.getStructureEntries().add(new TownStructureEntry("village_library", 1, 2));
  testTemplate.setTownHallEntry(new TownStructureEntry("town_hall1", 1, 1));
  testTemplate.setMinSize(90);
  testTemplate.setMaxSize(128);
  testTemplate.setMaxValue(1000);
  testTemplate.setWallStyle(1);
  testTemplate.setWallSize(7);
  testTemplate.addWall(0, "wall_straight1", 10);
  testTemplate.addCornerWall("wall_corner1", 10);
  testTemplate.setTownBlockSize(32);
  testTemplate.setTownPlotSize(10);
  }

}
