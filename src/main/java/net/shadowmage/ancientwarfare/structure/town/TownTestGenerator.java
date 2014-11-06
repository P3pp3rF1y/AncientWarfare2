package net.shadowmage.ancientwarfare.structure.town;

import net.shadowmage.ancientwarfare.structure.town.TownTemplate.TownStructureEntry;

public class TownTestGenerator
{

public static TownTemplate testTemplate;

public static void load()
  {
  testTemplate = new TownTemplate("testTown");  
  testTemplate.getStructureEntries().add(new TownStructureEntry("wgt1", 1, 1, 100, 10));
  testTemplate.getStructureEntries().add(new TownStructureEntry("wgt2", 1, 1, 100, 10));
  testTemplate.getStructureEntries().add(new TownStructureEntry("wgt3", 1, 1, 100, 10));
  testTemplate.setMinSize(90);
  testTemplate.setMaxSize(128);
  testTemplate.setMaxValue(1000);
  testTemplate.setWallStyle(1);
  testTemplate.setWallSize(2);
  testTemplate.setWallHeight(4);
  }

}
