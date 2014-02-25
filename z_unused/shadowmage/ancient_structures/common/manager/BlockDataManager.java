/**
   Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
   This software is distributed under the terms of the GNU General Public License.
   Please see COPYING for precise license information.

   This file is part of Ancient Warfare.

   Ancient Warfare is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   Ancient Warfare is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */
package shadowmage.ancient_structures.common.manager;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import shadowmage.ancient_structures.common.utils.BlockInfo;

public class BlockDataManager
{

private static BlockDataManager INSTANCE;
private BlockDataManager(){}
public static BlockDataManager instance()
  {
  if(INSTANCE==null)
    {
    INSTANCE = new BlockDataManager();
    }
  return INSTANCE;
  }

public static void loadBlockList()
  {
  addBlock(0, "air");
  /************************************ BASIC SUBTYPED BLOCKS ************************************/
  addBlock(Block.stoneBrick).setIsBasicSubtype();
  addBlock(Block.cloth).setIsBasicSubtype();
  addBlock(Block.leaves).setIsBasicSubtype();
  addBlock(Block.cobblestoneWall).setIsBasicSubtype();
  
  /************************************ ADVANCED SUBTYPED BLOCKS ************************************/
  addBlock(Block.grass);  
  addBlock(Block.stoneSingleSlab);
  addBlock(Block.stoneDoubleSlab);
  addBlock(Block.woodSingleSlab);
  addBlock(Block.woodDoubleSlab);
  
  /************************************ PRIORITY ONLY AND BASIC INVENTORIED BLOCK ENTRIES ************************************/
  addBlock(Block.waterMoving).setPriority(1).setBasicInventoryItem(Item.bucketWater.itemID, 0, 1);
  addBlock(Block.waterStill).setPriority(1).setBasicInventoryItem(Item.bucketWater.itemID, 0, 1);
  addBlock(Block.lavaStill).setPriority(1).setBasicInventoryItem(Item.bucketLava.itemID, 0, 1);
  addBlock(Block.lavaMoving).setPriority(1).setBasicInventoryItem(Item.bucketLava.itemID, 0, 1);
  addBlock(Block.sand).setPriority(1);
  addBlock(Block.gravel).setPriority(1);  
  addBlock(Block.tallGrass).setPriority(1);
  addBlock(Block.deadBush).setPriority(1);
  addBlock(Block.plantRed).setPriority(1);
  addBlock(Block.plantYellow).setPriority(1);
  addBlock(Block.mushroomBrown).setPriority(1);
  addBlock(Block.mushroomRed).setPriority(1);
  addBlock(Block.redstoneWire).setPriority(1).setBasicInventoryItem(Item.redstone.itemID, 0, 1);
  addBlock(Block.crops).setPriority(1).setBasicInventoryItem(Item.seeds.itemID, 0, 4);
  addBlock(Block.signPost).setPriority(1).setBasicInventoryItem(Item.sign.itemID, 0, 1);
  addBlock(Block.cactus).setPriority(1);
  addBlock(Block.reed).setPriority(1);
  addBlock(Block.fence);
  addBlock(Block.netherFence);
  addBlock(Block.skull).setPriority(1);
  addBlock(Block.flowerPot).setPriority(1);
  addBlock(Block.carrot).setPriority(1).setBasicInventoryItem(Item.carrot.itemID, 0, 4);
  addBlock(Block.potato).setPriority(1).setBasicInventoryItem(Item.potato.itemID, 0, 4);
  addBlock(Block.mushroomCapBrown).setPriority(1);
  addBlock(Block.mushroomCapRed).setPriority(1);
  
  /************************************ ROTATABLE/METADATA BLOCKS ************************************/
  addBlock(Block.dropper).setRotatable().setMeta(0, 2, 5, 3, 4).setMeta(1, 1, 1, 1, 1).setMeta(2, 0, 0, 0, 0).setMeta(3, 10, 13, 11, 12);
  addBlock(Block.dispenser).setRotatable().setMeta(0, 2, 5, 3, 4).setMeta(1, 1, 1, 1, 1).setMeta(2, 0, 0, 0, 0).setMeta(3, 10, 13, 11, 12);
  addBlock(Block.hopperBlock).setRotatable().setMeta(0, 3, 4, 2, 5).setMeta(1, 0, 0, 0, 0).setMeta(2, 11, 12, 10, 13).setMeta(3, 8, 8, 8, 8);
  addBlock(Block.chest).setRotatable().setMeta(0, 2, 5, 3, 4);  
  addBlock(Block.chestTrapped).setRotatable().setMeta(0, 2, 5, 3, 4);
  addBlock(Block.furnaceIdle).setRotatable().setMeta(0, 2, 5, 3, 4);
  addBlock(Block.furnaceBurning).setRotatable().setMeta(0, 2, 5, 3, 4).setBasicInventoryItem(Block.furnaceIdle.blockID, 0, 1);
  addBlock(Block.ladder).setPriority(1).setRotatable().setMeta(0, 2, 5, 3, 4);
  addBlock(Block.signWall).setPriority(1).setRotatable().setMeta(0, 2, 5, 3, 4).setBasicInventoryItem(Item.sign.itemID, 0, 1);
  addBlock(Block.enderChest).setRotatable().setMeta(0, 2, 5, 3, 4);
  
  addBlock(Block.bed).setRotatable().setMeta(0, 0, 1, 2, 3).setMeta(1, 8, 9, 10, 11).setBasicInventoryItem(Item.bed.itemID, 0, 1);
  
  addBlock(Block.rail).setPriority(1).setRotatable().setMeta(0, 0, 1, 0, 1).setMeta(1, 7, 8, 9, 6).setMeta(2, 5, 3, 4, 2);
  addBlock(Block.railPowered).setPriority(1).setRotatable().setMeta(0, 5, 3, 4, 2).setMeta(1, 0, 1, 0, 1).setMeta(2, 13, 11, 12, 10).setMeta(3, 8, 9, 8, 9);
  addBlock(Block.railDetector).setPriority(1).setRotatable().setMeta(0, 5, 3, 4, 2).setMeta(1, 0, 1, 0, 1).setMeta(2, 13, 11, 12, 10).setMeta(3, 8, 9, 8, 9);
  addBlock(Block.railActivator).setPriority(1).setRotatable().setMeta(0, 5, 3, 4, 2).setMeta(1, 0, 1, 0, 1).setMeta(2, 13, 11, 12, 10).setMeta(3, 8, 9, 8, 9);
  
  addBlock(Block.pistonStickyBase).setRotatable().setMeta(0, 2, 5, 3, 4).setMeta(1, 1, 1, 1, 1).setMeta(2, 0, 0, 0, 0).setMeta(3, 10, 13, 11, 12).setMeta(4, 9, 9, 9, 9).setMeta(5, 8, 8, 8, 8);; 
  addBlock(Block.pistonBase).setRotatable().setMeta(0, 2, 5, 3, 4).setMeta(1, 1, 1, 1, 1).setMeta(2, 0, 0, 0, 0).setMeta(3, 10, 13, 11, 12).setMeta(4, 9, 9, 9, 9).setMeta(5, 8, 8, 8, 8);
  addBlock(Block.pistonExtension).setRotatable().setMeta(0, 0, 0, 0, 0).setMeta(1, 1, 1, 1, 1).setMeta(2, 2, 5, 3, 4);
  addBlock(Block.pistonMoving).setRotatable();//TODO ??
  
  addBlock(Block.lever).setPriority(1).setRotatable().setMeta(0, 5, 6, 5, 6).setMeta(1, 13, 14, 13, 14).setMeta(2, 4, 1, 3, 2).setMeta(3, 12, 9, 11, 10).setMeta(4, 7, 0, 7, 0).setMeta(5, 8, 15, 8, 15);
  addBlock(Block.redstoneRepeaterIdle).setPriority(1).setRotatable().setMeta(0, 2, 3, 0, 1).setMeta(1, 6, 7, 4, 5).setMeta(2, 10, 11, 8, 9).setMeta(3, 14, 15, 12, 13).setBasicInventoryItem(Item.redstoneRepeater.itemID, 0, 1);
  addBlock(Block.redstoneRepeaterActive).setPriority(1).setRotatable().setMeta(0, 2, 3, 0, 1).setMeta(1, 6, 7, 4, 5).setMeta(2, 10, 11, 8, 9).setMeta(3, 14, 15, 12, 13).setBasicInventoryItem(Item.redstoneRepeater.itemID, 0, 1);
  addBlock(Block.woodenButton).setRotatable().setMeta(0, 4, 1, 3, 2).setMeta(1, 12, 9, 11, 10);
  addBlock(Block.stoneButton).setRotatable().setMeta(0, 4, 1, 3, 2).setMeta(1, 12, 9, 11, 10);
  addBlock(Block.tripWireSource).setPriority(1).setRotatable().setMeta(0, 2, 3, 0, 1).setMeta(1, 6, 7, 4, 5).setMeta(2, 14, 15, 12, 13);
  addBlock(Block.tripWire).setPriority(1).setRotatable().setMeta(0, 1, 1, 1, 1).setMeta(1, 0, 0, 0, 0);
  
  addBlock(Block.torchWood).setPriority(1).setRotatable().setMeta(0, 4, 1, 3, 2).setMeta(1, 5, 5, 5, 5);  
  addBlock(Block.torchRedstoneIdle).setPriority(1).setRotatable().setMeta(0, 4, 1, 3, 2).setMeta(1, 5, 5, 5, 5);
  addBlock(Block.torchRedstoneActive).setPriority(1).setRotatable().setMeta(0, 4, 1, 3, 2).setMeta(1, 5, 5, 5, 5);  
   
  addBlock(Block.doorWood).setPriority(1).setRotatable().setMeta(0, 1, 2, 3, 0).setMeta(1, 5, 6, 7, 4).setMeta(2, 8, 8, 8, 8).setBasicInventoryItem(Item.doorWood.itemID, 0, 1);
  addBlock(Block.doorIron).setPriority(1).setRotatable().setMeta(0, 1, 2, 3, 0).setMeta(1, 5, 6, 7, 4).setMeta(2, 8, 8, 8, 8).setBasicInventoryItem(Item.doorIron.itemID, 0, 1);
  addBlock(Block.fenceGate).setRotatable().setMeta(0, 0, 1, 2, 3).setMeta(1, 4, 5, 6, 7);//HUH? rotated one block from door data?
  
  addBlock(Block.stairsWoodOak).setRotatable().setMeta(0, 2, 1, 3, 0).setMeta(1, 6, 5, 7, 4);
  addBlock(Block.stairsCobblestone).setRotatable().setMeta(0, 2, 1, 3, 0).setMeta(1, 6, 5, 7, 4);
  addBlock(Block.stairsBrick).setRotatable().setMeta(0, 2, 1, 3, 0).setMeta(1, 6, 5, 7, 4);
  addBlock(Block.stairsStoneBrick).setRotatable().setMeta(0, 2, 1, 3, 0).setMeta(1, 6, 5, 7, 4); 
  addBlock(Block.stairsSandStone).setRotatable().setMeta(0, 2, 1, 3, 0).setMeta(1, 6, 5, 7, 4);
  addBlock(Block.stairsWoodSpruce).setRotatable().setMeta(0, 2, 1, 3, 0).setMeta(1, 6, 5, 7, 4);
  addBlock(Block.stairsWoodBirch).setRotatable().setMeta(0, 2, 1, 3, 0).setMeta(1, 6, 5, 7, 4);
  addBlock(Block.stairsWoodJungle).setRotatable().setMeta(0, 2, 1, 3, 0).setMeta(1, 6, 5, 7, 4);
  addBlock(Block.stairsNetherQuartz).setRotatable().setMeta(0, 2, 1, 3, 0).setMeta(1, 6, 5, 7, 4);
  
  addBlock(Block.vine).setPriority(1).setRotatable().setMeta(0, 1, 2, 4, 8);
    
  addBlock(Block.anvil).setPriority(1).setRotatable().setMeta(0, 3, 0, 1, 2).setMeta(1, 7, 4, 5, 6).setMeta(2, 11, 8, 9, 10);
   
  addBlock(Block.cocoaPlant).setRotatable().setMeta(0, 0, 1, 2, 3).setMeta(1, 4, 5, 6, 7).setMeta(2, 8, 9, 10, 11).setMeta(3, 12, 13, 14, 15).setBasicInventoryItem(Item.dyePowder.itemID, 3, 1);
  
  addBlock(Block.blockNetherQuartz).setRotatable().setMeta(0, 0, 0, 0, 0).setMeta(1, 1, 1, 1, 1).setMeta(2, 2, 2, 2, 2).setMeta(3, 4, 3, 4, 3);
  
  addBlock(Block.redstoneComparatorIdle).setPriority(1).setRotatable().setMeta(0, 2, 3, 0, 1);
  addBlock(Block.redstoneComparatorActive).setPriority(1).setRotatable().setMeta(0, 2, 3, 0, 1).setBasicInventoryItem(Block.redstoneComparatorIdle.blockID, 0, 1);
  
  addBlock(Block.wood).setMeta(0, 0, 0, 0, 0).setRotatable().setMeta(1, 8, 4, 8, 4).setMeta(2, 1, 1, 1, 1).setMeta(3, 9, 5, 9, 5).setMeta(4, 2, 2, 2, 2).setMeta(5, 10, 6, 10, 6).setMeta(6, 3, 3, 3, 3).setMeta(7, 11, 7, 11, 7);
  /**
   * add single slabs, and double slabs for stone
   */
  int id = Block.stoneSingleSlab.blockID;
  int id2 = Block.stoneDoubleSlab.blockID;
  for(int i = 0; i< 8 ; i++)
    {
    BlockInfo.setInventoryBlock(id, i, id, i, 1);
    BlockInfo.setInventoryBlock(id, i+8, id, i, 1);
    }
  for(int i = 0; i < 8 ; i++)
    {
    BlockInfo.setInventoryBlock(id2, i, id, i, 2);
    }
  
  /**
   * do the same for wood slabs
   */
  id = Block.woodSingleSlab.blockID;
  id2= Block.woodDoubleSlab.blockID;
  for(int i = 0; i< 8 ; i++)
    {
    BlockInfo.setInventoryBlock(id, i, id, i, 1);
    BlockInfo.setInventoryBlock(id, i+8, id, i, 1);
    }
  for(int i = 0; i < 8 ; i++)
    {
    BlockInfo.setInventoryBlock(id2, i, id, i, 2);
    }
  
  id = Block.grass.blockID;
  BlockInfo.setInventoryBlock(id, 0, Block.dirt.blockID, 0, 1);  
  
  load17names();
  }

public static int getBlockPriority(int id, int meta)
  {
  if(BlockInfo.blockList[id]!=null)
    {
    return (int) BlockInfo.blockList[id].buildOrder;
    }
  return 0;
  }

public static void setMeta(Block block, int set, int a, int b, int c, int d)
  {
  setMeta(block.blockID, set, a, b, c, d);
  }

public static void setMeta(int id, int set, int a, int b, int c, int d)
  {
  if(BlockInfo.blockList[id]!=null)
    {
    BlockInfo.blockList[id].setMeta(set, a, b, c, d);
    }
  return;
  }

/**
 * convenience wrappers, passes params directly into BlockInfo.createEntryFor(.....)
 * @param block
 * @param priority
 * @return
 */
public static BlockInfo addBlock(Block block)
  {
  return BlockInfo.createEntryFor(block);
  }

public static BlockInfo addBlock(int id, String name)
  {
  return BlockInfo.createEntryFor(id, name);
  }

public static int getRotatedMeta(int id, int meta, int rotationAmt)
  {
  if(BlockInfo.blockList[id]==null)
    {
    return meta;
    }
  return BlockInfo.blockList[id].rotateRight(meta, rotationAmt);
  }

public static int getRotatedMeta(Block block, int meta, int rotationAmt)
  {
  return getRotatedMeta(block.blockID, meta, rotationAmt);
  }

public static Block getBlockByName(String name)
  {
  return blocksBy17Name.containsKey(name)? blocksBy17Name.get(name) : Block.stone;
  }

public static String getBlockName(Block block)
  {
  return namesFor17.containsKey(block) ? namesFor17.get(block) : block.getUnlocalizedName();
  }

private static HashMap<Block, String> namesFor17 = new HashMap<Block, String>();
private static HashMap<String, Block> blocksBy17Name = new HashMap<String, Block>();

private static void load17names()
  {
  add17NameMaping(Block.stone, "stone");
  add17NameMaping(Block.grass, "grass");
  add17NameMaping(Block.dirt, "dirt");
  add17NameMaping(Block.cobblestone, "cobblestone");
  add17NameMaping(Block.planks, "planks");
  add17NameMaping(Block.sapling, "sapling");
  add17NameMaping(Block.bedrock, "bedrock");
  add17NameMaping(Block.waterMoving, "flowing_water");
  add17NameMaping(Block.waterStill, "water");
  add17NameMaping(Block.lavaMoving, "flowing_lava");
  add17NameMaping(Block.lavaStill, "lava");
  add17NameMaping(Block.sand, "sand");
  add17NameMaping(Block.gravel, "gravel");
  add17NameMaping(Block.oreGold, "gold_ore");
  add17NameMaping(Block.oreIron, "iron_ore");
  add17NameMaping(Block.oreCoal, "coal_ore");
  add17NameMaping(Block.wood, "log");
  add17NameMaping(Block.leaves, "leaves");
  add17NameMaping(Block.sponge, "sponge");
  add17NameMaping(Block.glass, "glass");
  add17NameMaping(Block.oreLapis, "lapis_ore");
  add17NameMaping(Block.blockLapis, "lapis_block");
  add17NameMaping(Block.dispenser, "dispenser");
  add17NameMaping(Block.sandStone, "sandstone");
  add17NameMaping(Block.music, "noteblock");
  add17NameMaping(Block.bed, "bed");
  add17NameMaping(Block.railPowered, "golden_rail");
  add17NameMaping(Block.railDetector, "detector_rail");
  add17NameMaping(Block.pistonStickyBase, "sticky_piston");
  add17NameMaping(Block.web, "web");
  add17NameMaping(Block.tallGrass, "tallgrass");
  add17NameMaping(Block.deadBush, "deadbush");
  add17NameMaping(Block.pistonBase, "piston");
  add17NameMaping(Block.pistonExtension, "piston_head");
  add17NameMaping(Block.cloth, "wool");
  add17NameMaping(Block.pistonMoving, "piston_extension");
  add17NameMaping(Block.plantYellow, "yellow_flower");
  add17NameMaping(Block.plantRed, "red_flower");
  add17NameMaping(Block.mushroomBrown, "brown_mushroom");
  add17NameMaping(Block.mushroomRed, "red_mushroom");
  add17NameMaping(Block.blockGold, "gold_block");
  add17NameMaping(Block.blockIron, "iron_block");
  add17NameMaping(Block.stoneDoubleSlab, "double_stone_slab");
  add17NameMaping(Block.stoneSingleSlab, "stone_slab");
  add17NameMaping(Block.brick, "brick");
  add17NameMaping(Block.tnt, "tnt");
  add17NameMaping(Block.bookShelf, "bookshelf");
  add17NameMaping(Block.cobblestoneMossy, "mossy_cobblestone");
  add17NameMaping(Block.obsidian, "obsidian");
  add17NameMaping(Block.torchWood, "torch");
  add17NameMaping(Block.fire, "fire");
  add17NameMaping(Block.mobSpawner, "mob_spawner");
  add17NameMaping(Block.stairsWoodOak, "oak_stairs");
  add17NameMaping(Block.chest, "chest");
  add17NameMaping(Block.redstoneWire, "redstone_wire");
  add17NameMaping(Block.oreDiamond, "diamond_ore");
  add17NameMaping(Block.blockDiamond, "diamond_block");
  add17NameMaping(Block.workbench, "crafting_table");
  add17NameMaping(Block.crops, "wheat");
  add17NameMaping(Block.tilledField, "farmland");
  add17NameMaping(Block.furnaceIdle, "furnace");
  add17NameMaping(Block.furnaceBurning, "lit_furnace");
  add17NameMaping(Block.signPost, "standing_sign");
  add17NameMaping(Block.doorWood, "wooden_door");
  add17NameMaping(Block.ladder, "ladder");
  add17NameMaping(Block.rail, "rail");
  add17NameMaping(Block.stairsCobblestone, "stone_stairs");
  add17NameMaping(Block.signWall, "wall_sign");
  add17NameMaping(Block.lever, "lever");  
  add17NameMaping(Block.pressurePlateStone, "stone_pressure_plate");
  add17NameMaping(Block.doorIron, "iron_door");  
  add17NameMaping(Block.pressurePlatePlanks, "wooden_pressure_plate");
  add17NameMaping(Block.oreRedstone, "redstone_ore");
  add17NameMaping(Block.oreRedstoneGlowing, "lit_redstone_ore");
  add17NameMaping(Block.torchRedstoneIdle, "unlit_redstone_torch");
  add17NameMaping(Block.torchRedstoneActive, "redstone_torch");
  add17NameMaping(Block.stoneButton, "stone_button");
  add17NameMaping(Block.snow, "snow_layer");
  add17NameMaping(Block.ice, "ice");
  add17NameMaping(Block.blockSnow, "snow");
  add17NameMaping(Block.cactus, "cactus");
  add17NameMaping(Block.blockClay, "clay");
  add17NameMaping(Block.reed, "reeds");
  add17NameMaping(Block.jukebox, "jukebox");
  add17NameMaping(Block.fence, "fence");
  add17NameMaping(Block.pumpkin, "pumpkin");
  add17NameMaping(Block.netherrack, "netherrack");
  add17NameMaping(Block.slowSand, "soul_sand");
  add17NameMaping(Block.glowStone, "glowstone");
  add17NameMaping(Block.portal, "portal");
  add17NameMaping(Block.pumpkinLantern, "lit_pumpkin");
  add17NameMaping(Block.cake, "cake");
  add17NameMaping(Block.redstoneRepeaterIdle, "unpowered_repeater");
  add17NameMaping(Block.redstoneRepeaterActive, "powered_repeater");
  //"stained_glass replaces locked chest??
  add17NameMaping(Block.trapdoor, "trapdoor");
  add17NameMaping(Block.silverfish, "monster_egg");
  add17NameMaping(Block.stoneBrick, "stonebrick");
  add17NameMaping(Block.mushroomCapBrown, "brown_mushroom_block");
  add17NameMaping(Block.mushroomCapRed, "red_mushroom_block");
  add17NameMaping(Block.fenceIron, "iron_bars");
  add17NameMaping(Block.thinGlass, "glass_pane");
  add17NameMaping(Block.melon, "melon_block");
  add17NameMaping(Block.pumpkinStem, "pumpkin_stem");
  add17NameMaping(Block.melonStem, "melon_stem");
  add17NameMaping(Block.vine, "vine");
  add17NameMaping(Block.fenceGate, "fence_gate");
  add17NameMaping(Block.stairsBrick, "brick_stairs");
  add17NameMaping(Block.stairsStoneBrick, "stone_brick_stairs");
  add17NameMaping(Block.mycelium, "mycelium");  
  add17NameMaping(Block.waterlily, "waterlily");
  add17NameMaping(Block.netherBrick, "nether_brick");
  add17NameMaping(Block.netherFence, "nether_brick_fence");
  add17NameMaping(Block.stairsNetherBrick, "nether_brick_stairs");
  add17NameMaping(Block.netherStalk, "nether_wart");
  add17NameMaping(Block.enchantmentTable, "enchanting_table");
  add17NameMaping(Block.brewingStand, "brewing_stand");
  add17NameMaping(Block.cauldron, "cauldron");
  add17NameMaping(Block.endPortal, "end_portal");
  add17NameMaping(Block.endPortalFrame, "end_portal_frame");
  add17NameMaping(Block.whiteStone, "end_stone");
  add17NameMaping(Block.dragonEgg, "dragon_egg");
  add17NameMaping(Block.redstoneLampIdle, "redstone_lamp");
  add17NameMaping(Block.redstoneLampActive, "lit_redstone_lamp");
  add17NameMaping(Block.woodDoubleSlab, "double_wooden_slab");
  add17NameMaping(Block.woodSingleSlab, "wooden_slab");
  add17NameMaping(Block.cocoaPlant, "cocoa");
  add17NameMaping(Block.stairsSandStone, "sandstone_stairs");
  add17NameMaping(Block.oreEmerald, "emerald_ore");
  add17NameMaping(Block.enderChest, "ender_chest");
  add17NameMaping(Block.tripWireSource, "tripwire_hook");
  add17NameMaping(Block.tripWire, "tripwire");
  add17NameMaping(Block.blockEmerald, "emerald_block");
  add17NameMaping(Block.stairsWoodSpruce, "spruce_stairs");
  add17NameMaping(Block.stairsWoodBirch, "birch_stairs");
  add17NameMaping(Block.stairsWoodJungle, "jungle_stairs");
  add17NameMaping(Block.commandBlock, "command_block");
  add17NameMaping(Block.beacon, "beacon");
  add17NameMaping(Block.cobblestoneWall, "cobblestone_wall");
  add17NameMaping(Block.flowerPot, "flower_pot");
  add17NameMaping(Block.carrot, "carrots");
  add17NameMaping(Block.potato, "potatoes");
  add17NameMaping(Block.woodenButton, "wooden_button");
  add17NameMaping(Block.skull, "skull");
  add17NameMaping(Block.anvil, "anvil");
  add17NameMaping(Block.chestTrapped, "trapped_chest");
  add17NameMaping(Block.pressurePlateGold, "light_weighted_pressure_plate");
  add17NameMaping(Block.pressurePlateIron, "heavy_weighted_pressure_plate");
  add17NameMaping(Block.redstoneComparatorIdle, "unpowered_comparator");
  add17NameMaping(Block.redstoneComparatorActive, "powered_comparator");
  add17NameMaping(Block.daylightSensor, "daylight_detector");
  add17NameMaping(Block.blockRedstone, "redstone_block");
  add17NameMaping(Block.oreNetherQuartz, "quartz_ore");
  add17NameMaping(Block.hopperBlock, "hopper");
  add17NameMaping(Block.blockNetherQuartz, "quartz_block");
  add17NameMaping(Block.stairsNetherQuartz, "quartz_stairs");
  add17NameMaping(Block.railActivator, "activator_rail");
  add17NameMaping(Block.dropper, "dropper");
  add17NameMaping(Block.stainedClay, "stained_hardened_clay");
  add17NameMaping(Block.hay, "hay_block");
  add17NameMaping(Block.carpet, "carpet");
  add17NameMaping(Block.hardenedClay, "hardened_clay");
  add17NameMaping(Block.coalBlock, "coal_block");
  }

private static void add17NameMaping(Block block, String name)
  {
  namesFor17.put(block, name);
  blocksBy17Name.put(name, block);
  }

}
