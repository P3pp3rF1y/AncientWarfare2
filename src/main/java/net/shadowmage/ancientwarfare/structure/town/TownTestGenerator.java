package net.shadowmage.ancientwarfare.structure.town;

import net.shadowmage.ancientwarfare.structure.town.TownTemplate.TownStructureEntry;

public class TownTestGenerator
{

public static TownTemplate testTemplate;

public static void load()
  {
  testTemplate = new TownTemplate("testTown");
  testTemplate.getStructureEntries().add(new TownStructureEntry("struct1", 1, 1, 100, 10));
  testTemplate.getStructureEntries().add(new TownStructureEntry("struct2", 1, 1, 100, 10));
  testTemplate.getStructureEntries().add(new TownStructureEntry("struct3", 1, 1, 100, 10));
  testTemplate.getStructureEntries().add(new TownStructureEntry("struct4", 1, 1, 100, 10));
  }

}
