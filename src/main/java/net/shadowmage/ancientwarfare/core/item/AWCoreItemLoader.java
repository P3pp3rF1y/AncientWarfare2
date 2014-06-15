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
  
  AWItems.backpack = new ItemBackpack("backpack");
  GameRegistry.registerItem(AWItems.backpack, "backpack");
  
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
  
  AWItems.quillWood = new ItemQuill("wooden_quill", ToolMaterial.WOOD);
  GameRegistry.registerItem(AWItems.quillWood, "wooden_quill");  
  AWItems.quillStone = new ItemQuill("stone_quill", ToolMaterial.STONE);
  GameRegistry.registerItem(AWItems.quillStone, "stone_quill");  
  AWItems.quillIron = new ItemQuill("iron_quill", ToolMaterial.IRON);
  GameRegistry.registerItem(AWItems.quillIron, "iron_quill");  
  AWItems.quillGold = new ItemQuill("gold_quill", ToolMaterial.GOLD);
  GameRegistry.registerItem(AWItems.quillGold, "gold_quill");  
  AWItems.quillDiamond = new ItemQuill("diamond_quill", ToolMaterial.EMERALD);
  GameRegistry.registerItem(AWItems.quillDiamond, "diamond_quill");
  
  AWItems.componentItem = new ItemComponent("component");
  GameRegistry.registerItem(AWItems.componentItem, "component");
  
  AWItems.steel_ingot = new ItemSteelIngot("steel_ingot");
  GameRegistry.registerItem(AWItems.steel_ingot, "steel_ingot");
  }

}
