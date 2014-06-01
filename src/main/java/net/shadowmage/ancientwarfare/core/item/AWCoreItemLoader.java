package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.item.Item.ToolMaterial;
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
  
  AWItems.automationHammerWood = new ItemHammer("wooden_hammer", ToolMaterial.WOOD);
  GameRegistry.registerItem(AWItems.automationHammerWood, "wooden_hammer");
  
  AWItems.automationHammerStone = new ItemHammer("stone_hammer", ToolMaterial.STONE);
  GameRegistry.registerItem(AWItems.automationHammerStone, "stone_hammer");
  
  AWItems.automationHammerIron = new ItemHammer("iron_hammer", ToolMaterial.IRON);
  GameRegistry.registerItem(AWItems.automationHammerIron, "iron_hammer");
  
  AWItems.automationHammerGold = new ItemHammer("gold_hammer", ToolMaterial.GOLD);
  GameRegistry.registerItem(AWItems.automationHammerGold, "gold_hammer");
  
  AWItems.automationHammerDiamond = new ItemHammer("diamond_hammer", ToolMaterial.EMERALD);
  GameRegistry.registerItem(AWItems.automationHammerDiamond, "diamond_hammer");
  
  AWItems.backpack = new ItemBackpack("backpack");
  GameRegistry.registerItem(AWItems.backpack, "backpack");
  }

}
