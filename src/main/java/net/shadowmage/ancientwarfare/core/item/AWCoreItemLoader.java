package net.shadowmage.ancientwarfare.core.item;

import net.shadowmage.ancientwarfare.core.api.AWItems;
import cpw.mods.fml.common.registry.GameRegistry;

public class AWCoreItemLoader
{

public static final AWCoreItemLoader INSTANCE = new AWCoreItemLoader();
private AWCoreItemLoader(){}

public void load()
  {
  AWItems.researchBook = new ItemResearchBook("research_book");
  GameRegistry.registerItem(AWItems.researchBook, "research_book");
  
  AWItems.researchNote = new ItemResearchNotes("research_note");
  GameRegistry.registerItem(AWItems.researchNote, "research_note");
  }

}
