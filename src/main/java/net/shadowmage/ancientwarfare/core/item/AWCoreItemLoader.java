package net.shadowmage.ancientwarfare.core.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.shadowmage.ancientwarfare.core.api.AWItems;

public class AWCoreItemLoader
{

public static final AWCoreItemLoader INSTANCE = new AWCoreItemLoader();
private AWCoreItemLoader(){}

public void load()
  {
  AWItems.researchBook = new ItemResearchBook("research_book");
  GameRegistry.registerItem(AWItems.researchBook, "research_book");
  }

}
