package net.shadowmage.ancientwarfare.structure.town;

import java.util.HashMap;

import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.town.TownTemplate.TownStructureEntry;

public class TownGenerator
{

int remainingGenerationValue;
TownTemplate template;
private HashMap<String, TownGeneratedEntry> generatedStructureMap = new HashMap<String, TownGeneratedEntry>();

public TownGenerator(TownTemplate template)
  {
  this.template = template;
  this.remainingGenerationValue = template.maxValue;
  }

public void generateAt(World world, int x, int y, int z)
  {
  
  }


public static final class TownGeneratedEntry
{
int numGenerated;
TownStructureEntry generatedEntry;
}

}
