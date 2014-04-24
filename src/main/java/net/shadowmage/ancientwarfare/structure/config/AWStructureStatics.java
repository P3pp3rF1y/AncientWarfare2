/**
   Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
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
package net.shadowmage.ancientwarfare.structure.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.config.ModConfiguration;

public class AWStructureStatics extends ModConfiguration
{

public AWStructureStatics(Configuration config)
  {
  super(config);
  }

public static String templateExtension = "aws";
public static boolean enableVillageGen = true;
public static boolean enableStructureGeneration = true;
public static int chunkSearchRadius = 16;
public static int maxClusterValue = 500;
public static int randomChance = 75;
public static int randomRange = 1000;
public static int spawnProtectionRange = 12;
public static int structureImageWidth = 512;
public static int structureImageHeight = 288;
public static Set<String> excludedSpawnerEntities = new HashSet<String>();
private static HashSet<String> skippableWorldGenBlocks = new HashSet<String>();
private static HashSet<String> worldGenTargetBlocks = new HashSet<String>();
private static HashMap<Class, String> biomeAliasByClass = new HashMap<Class, String>();
private static HashMap<String, Class> biomeAliasByName = new HashMap<String, Class>();

private static String worldGenCategory = "a_world-gen_settings";
private static String villageGenCategory = "b_village-gen_settings";
private static String excludedEntitiesCategory = "c_excluded_spawner_entities";
private static String worldGenBlocks = "d_world_gen_skippable_blocks";
private static String targetBlocks = "e_world_gen_target_blocks";
private static String biomeMap = "f_biome_aliases";

@Override
public void initializeCategories()
  {
  this.config.addCustomCategoryComment(worldGenCategory, "Settings that effect all world-structure-generation.");
  this.config.addCustomCategoryComment(villageGenCategory, "Settings that effect the generation of vanilla villages.\nCurrently there are no village-generation options, and no structures will generate in villages.");
  this.config.addCustomCategoryComment(excludedEntitiesCategory, "Entities that will not show up in the Mob Spawner Placer entity selection list.\nAdd any mobs here that will crash if spawned via the vanilla mob-spawner (usually complex NBT-defined entities).");
  this.config.addCustomCategoryComment(worldGenBlocks, "Blocks that should be skipped/ignored during world gen -- should list all plant blocks/logs/foliage");
  this.config.addCustomCategoryComment(targetBlocks, "List of target blocks to add to the target-block selection GUI.\nVanilla block names should be listed as the 1.7 registered name. \nMod blocks should be listed as 'tile.'+registeredBlockName");
  this.config.addCustomCategoryComment(biomeMap, "Custom-mapped biome names to be used in templates.\nBiomes should be specified by their fully-qualifed class-name.\nThis alias list must be shared if you wish to share your templates that use these custom aliases.");
  }

@Override
public void initializeValues()
  {
  templateExtension = config.get(worldGenCategory, "template_extension", "aws").getString();
  enableVillageGen = config.get(worldGenCategory, "enable_village_generation", enableVillageGen).getBoolean(enableVillageGen);
  enableStructureGeneration = config.get(worldGenCategory, "enable_structure_generation", enableStructureGeneration).getBoolean(enableStructureGeneration);
  chunkSearchRadius = config.get(worldGenCategory, "validation_chunk_radius", chunkSearchRadius).getInt(chunkSearchRadius);
  maxClusterValue = config.get(worldGenCategory, "max_cluster_value", maxClusterValue).getInt(maxClusterValue);
  randomChance = config.get(worldGenCategory, "random_chance", randomChance).getInt(randomChance);
  randomRange = config.get(worldGenCategory, "random_range", randomRange).getInt(randomRange);
  spawnProtectionRange = config.get(worldGenCategory, "spawn_protection_chunk_radius", spawnProtectionRange).getInt(spawnProtectionRange);

  shouldExport = config.get(worldGenCategory, "export_defaults", shouldExport, "If true, will re-export the included structure templates.\nShould be re-set after every update that adds or changes templates.").getBoolean(shouldExport);

  String[] defaultExcludedEntities = new String[]
        {
            "EnderCrystal",
            "EnderDragon",
            "EyeOfEnderSignal",
            "FallingSand",
            "Fireball",
            "FireworksRocketEntity",
            "Item",
            "ItemFrame",
            "LeashKnot",
            "Mob",
            "Monster",
            "Painting",
            "PrimedTnt",
            "SmallFireball",
            "Snowball",
            "ThrownEnderpearl",
            "ThrownExpBottle",
            "ThrownPotion",
            "WitherBoss",
            "WitherSkull",
            "XPOrb",
            "AncientWarfare.entity.npc",
            "AncientWarfare.entity.missile",
            "AncientWarfare.entity.vehicle",
            "AncientWarfare.entity.gate",
            "MinecraftFurnace",
            "MinecartSpawner",
            "MinecartHopper",
            "MinecartFurnace",
            "MinecartRideable",
            "MinecartChest",
            "MinecartTNT",
            "Boat",
            "LOTR.Marsh Wraith Ball",
            "LOTR.Orc Bomb",
            "LOTR.Spear",
            "LOTR.Barrel",
            "LOTR.Portal",
            "LOTR.Wargskin Rug",
            "LOTR.Crossbow Bolt",
            "LOTR.Throwing Axe",
            "LOTR.Thrown Rock",
            "LOTR.Plate",
            "LOTR.Mystery Web",
            "LOTR.Gandalf Fireball",
            "LOTR.Pebble",
            "LOTR.Smoke Ring",
            "LOTR.Gollum",
            "LOTR.Trader Respawn",
            "Metallurgy3Base.LargeTNTEntity",
            "Metallurgy3Base.MinersTNTEntity",
            "awger_Whitehall.EntityWhitehall",
            "awger_SmallBoat.EntityBoatChest",
            "awger_SmallBoat.EntityBoatPart",
            "awger_SmallBoat.EntitySmallBoat",
            "awger_Hoy.EntityHoy",
            "awger_Punt.EntityPunt",
            "BiomesOPlenty.PoisonDart",
            "BiomesOPlenty.MudBall",
            "BiomesOPlenty.Dart",
            "KoadPirates.Tether",
            "KoadPirates.Net",
            "KoadPirates.Shot",
            "KoadPirates.Cannon Ball",
            "weaponmod.flail",
            "weaponmod.boomerang",
            "weaponmod.dart",
            "weaponmod.bolt",
            "weaponmod.dynamite",
            "weaponmod.javelin",
            "weaponmod.knife",
            "weaponmod.spear",
            "weaponmod.dummy",
            "Arrow",
            "Cannon",
            "SonicBoom",
            "witchery.spellEffect",
            "witchery.corpse",
            "witchery.brew",
            "witchery.broom",
            "witchery.mandrake",
            "witchery.familiar",
            "witchery.owl",
            "witchery.eye",
            "JungleMobs.ConcapedeSegment",
            "DesertMobs.Mudshot",
            "DesertMobs.ThrowingScythe",
            "DemonMobs.Doomfireball",
            "DemonMobs.DemonicBlast",
            "DemonMobs.DemonicSpark",
            "DemonMobs.Hellfireball",
            "SwampMobs.VenomShot",
            "SwampMobs.PoisonRayEnd",
            "SwampMobs.PoisonRay",
            "RopesPlus.Frost Arrow303",
            "RopesPlus.Rope Arrow303",
            "RopesPlus.Penetrating Arrow303",
            "RopesPlus.Slime Arrow303",
            "RopesPlus.Arrow303",
            "RopesPlus.Redstonetorch Arrow303",
            "RopesPlus.Fire Arrow303",
            "RopesPlus.Exploding Arrow303",
            "RopesPlus.GrapplingHook",
            "RopesPlus.Confusing Arrow303",
            "RopesPlus.Warp Arrow303",
            "RopesPlus.Torch Arrow303",
            "RopesPlus.Seed Arrow303",
            "RopesPlus.Dirt Arrow303",
            "RopesPlus.FreeFormRope",
            "Goblins_mod.GArcaneball",
            "Goblins_mod.MTNTPrimed",
            "Goblins_mod.Bomb",
            "Goblins_mod.orbR",
            "Goblins_mod.Lightball",
            "Goblins_mod.ETNTPrimed",
            "Goblins_mod.GArcanebal",
            "Goblins_mod.Arcaneball",
            "Goblins_mod.orbY",
            "Goblins_mod.orbB",
            "Goblins_mod.orbG",
            "MoCreatures.Egg",
            "MoCreatures.MoCPlatform",
            "MoCreatures.LitterBox",
            "MoCreatures.FishBowl",
            "MoCreatures.KittyBed",
            "MoCreatures.PetScorpion",
            "BiblioCraft.SeatEntity",
            "minecolonies.pointer",
            "minecolonies.arrow",
            "minecolonies.citizen",
            "minecolonies.stonemason",
            "minecolonies.huntersdog",
            "minecolonies.stonemason",
            "minecolonies.farmer",
            "minecolonies.blacksmith",
            "minecolonies.builder",
            "minecolonies.miner",
            "minecolonies.baker",
            "minecolonies.deliveryman",
            "minecolonies.soldier",
            "extrabiomes.scarecrow",
            "Paleocraft.Bladeking68",
            "Test",
            "Petrified",
            "BladeTrap",
            "EyeRay",
            "MagicMissile",
            "RakshasaImage"
        };  
  defaultExcludedEntities = config.get(excludedEntitiesCategory, "excluded_entities", defaultExcludedEntities).getStringList();

  for(String st : defaultExcludedEntities)
    {
    excludedSpawnerEntities.add(st);
    }

  String[] defaultSkippableBlocks = new String[]    
        {
            "cactus",
            "vine",
            "tallgrass",
            "log",
            "red_flower",
            "yellow_flower",
            "deadbush",
            "leaves",
            "snow_layer",
            "snow",
            "web",
            "cocoa",
            "tile.Coral4",
            "tile.Coral3",
            "tile.Coral2",
            "tile.Coral1",
            "tile.CoralPlant",
            "tile.SeaWeed",
            "tile.SeaWeed2",
            "tile.SeaWeed3",
            "tile.SpongePlant",
            "tile.CoralBlock",
            "tile.shell",
            "tile.shell2",
            "tile.shell3",
            "tile.shell4",
            "tile.blockOyster",
            "tile.blockQuicksand",
            "tile.bop.longGrass",
            "tile.bop.ash",
            "tile.bop.puddle",
            "tile.bop.wood1",
            "tile.bop.wood2",
            "tile.bop.wood3",
            "tile.bop.leaves4",
            "tile.bop.leaves1",
            "tile.bop.leaves2",
            "tile.bop.leaves3",
            "tile.bop.leaves4",
            "tile.bop.leavesColorized1",
            "tile.bop.leavesColorized2",
            "tile.bop.leavesFruit",
            "tile.bop.leavesFruit2",
            "tile.bop.petals",
            "tile.bop.flowers",
            "tile.bop.flowers2",
            "tile.bop.foliage",
            "tile.bop.willow",
            "tile.bop.ivy",
            "tile.bop.plants",
            "tile.bop.bamboo",
            "tile.bop.moss",
            "tile.bop.treeMoss",
            "tile.bop.mushrooms",
            "tile.bop.coral",
            "tile.bop.hive",
            "tile.lotr:fruitWood",
            "tile.lotr:fruitLeaves",
            "tile.lotr:mordorMoss",
            "tile.lotr:mordorThorn",
            "tile.lotr:leaves2",
            "tile.lotr:wood2",
            "tile.lotr:deadMarshPlant",
            "tile.lotr:clover",
            "tile.lotr:bluebell",
            "tile.lotr:quagmire",
            "tile.bladetrap",
            "tile.lavarock",
            "tile.MoCLog",
            "tile.MoCLeaves",
            "tile.MoCTallGrass",
            "tile.aloevera",
            "tile.watereddirt",
            "tile.tumbleweed",
            "tile.palmtreelog",
            "tile.palmtreeleaves",
            "tile.quicksand",
            "tile.nightbloom",
            "tile.creeperblossom",
            "tile.lotr:leaves",
            "tile.lotr:wood",
            "tile.witchLeaves",
            "tile.witchLog",
            "tile.spanishMoss",
            "tile.plantMine",
            "tile.leapingLily",
            "tile.emberMoss",
            "tile.extrabiomes.leaves",
            "tile.extrabiomes.cattail",
            "tile.extrabiomes.flower",
            "tile.extrabiomes.tallgrass",
            "tile.extrabiomes.leafpile",
            "tile.extrabiomes.log",
            "tile.extrabiomes.log.quarter",
            "tile.hl_acaciaWood",
            "tile.hl_canopyWood",
            "tile.hl_firWood",
            "tile.hl_poplarWood",
            "tile.hl_redwoodWood",
            "tile.hl_palmWood",
            "tile.hl_ironwoodWood",
            "tile.hl_mangroveWood",
            "tile.hl_ashWood",
            "tile.hl_acaciaLeaves",
            "tile.hl_canopyLeaves",
            "tile.hl_firLeaves",
            "tile.hl_poplarLeaves",
            "tile.hl_redwoodLeaves",
            "tile.hl_palmLeaves",
            "tile.hl_ironwoodLeaves",
            "tile.hl_mangroveLeaves",
            "tile.hl_ashLeaves",
            "tile.hl_autumnYellowLeaves",
            "tile.hl_autumnOrangeLeaves",
            "tile.hl_blueFlower",
            "tile.hl_leafyFern",
            "tile.hl_whiteFlower",
            "tile.hl_cattail",
            "tile.hl_lavender",
            "tile.hl_raspberryBush",
            "tile.hl_blueberryBush",
            "tile.hl_thornbush",
            "tile.hl_cotton",
            "tile.fossilsBlock",
            "tile.floraBlock",
            "tile.decorationsBlock",
            "tile.stoneStalactiteBlock",
            "tile.sandstoneStalactiteBlock",
            "tile.TFLog",
            "tile.TFLeaves",
            "tile.TFPlant",
            "tile.TFRoots",
            "tile.TFMagicLog",
            "tile.TFMagicLeaves",
            "tile.TFMoonworm",
            "tile.TFMagicLogSpecial",
            "tile.TFTowerStone",
            "tile.PamWeeeFlowers:vine_white",
            "tile.PamWeeeFlowers:vine_orange",
            "tile.PamWeeeFlowers:vine_magenta",
            "tile.PamWeeeFlowers:vine_lightblue",
            "tile.PamWeeeFlowers:vine_yellow",
            "tile.PamWeeeFlowers:vine_lime",
            "tile.PamWeeeFlowers:vine_pink",
            "tile.PamWeeeFlowers:vine_darkgrey",
            "tile.PamWeeeFlowers:vine_lightgrey",
            "tile.PamWeeeFlowers:vine_cyan",
            "tile.PamWeeeFlowers:vine_purple",
            "tile.PamWeeeFlowers:vine_blue",
            "tile.PamWeeeFlowers:vine_brown",
            "tile.PamWeeeFlowers:vine_green",
            "tile.PamWeeeFlowers:vine_red",
            "tile.PamWeeeFlowers:vine_black",
            "tile.flowerCrop",
            "tile.PamHarvestCraft:blueberrycrop_2",
            "tile.PamHarvestCraft:strawberrycrop_2",
            "tile.PamHarvestCraft:seaweedcrop_2",
            "tile.PamHarvestCraft:rhubarbcrop_2",
            "tile.PamHarvestCraft:rutabagacrop_2",
            "tile.PamHarvestCraft:whitemushroomcrop_2",
            "tile.PamHarvestCraft:candleberrycrop_2",
            "tile.PamHarvestCraft:blackberrycrop_2",
            "tile.PamHarvestCraft:raspberrycrop_2",
            "tile.PamHarvestCraft:cranberrycrop_2",
            "tile.PamHarvestCraft:kiwicrop_2",
            "tile.PamHarvestCraft:sunflowercrop_2",
            "tile.PamHarvestCraft:spiceleafcrop_2",
            "tile.PamHarvestCraft:cottoncrop_2",
            "tile.PamHarvestCraft:grapecrop_2"
        };

  defaultSkippableBlocks = config.get(worldGenBlocks, "skippable_blocks", defaultSkippableBlocks).getStringList();
  for(String st : defaultSkippableBlocks)
    {
    skippableWorldGenBlocks.add(st);
    } 

  /**
   * TODO add initial default values for target blocks to this list...
   */
  String[] targetBlocks = new String[]
        {
            "tile.bop.wood1"
        };
  targetBlocks = config.get(AWStructureStatics.targetBlocks, "target_blocks", targetBlocks).getStringList();
  for(String st : targetBlocks)
    {
    worldGenTargetBlocks.add(st);
    }

  ConfigCategory biomeAliasCategory = config.getCategory(biomeMap);
  String fqcn;
  Class foundClass;
  String alias;
  for(Entry<String, Property> entry : biomeAliasCategory.entrySet())
    {
    fqcn = entry.getKey();
    alias = entry.getValue().getString();
    try
      {
      foundClass = Class.forName(fqcn);
      if(foundClass!=null)
        {        
        AWLog.logDebug("mapping alias for class: "+foundClass+"  alias: "+alias);
        biomeAliasByClass.put(foundClass, alias);
        biomeAliasByName.put(alias, foundClass);
        }
      } 
    catch (ClassNotFoundException e)
      {
      e.printStackTrace();
      }
    } 
  this.config.save();
  }

public static boolean skippableBlocksContains(String blockName)
  {
  String tileName = blockName.startsWith("tile.") ? blockName : "tile."+blockName;
  String noTileName = blockName.startsWith("tile.") ? blockName.substring(5): blockName;
  return skippableWorldGenBlocks.contains(tileName) || skippableWorldGenBlocks.contains(noTileName);
  }

public static Set<String> getUserDefinedTargetBlocks()
  {
  return worldGenTargetBlocks;
  }

public static String getBiomeName(BiomeGenBase biome)
  {
  if(biomeAliasByClass.containsKey(biome.getClass()))
    {
    return biomeAliasByClass.get(biome.getClass());
    }
  return biome.biomeName.toLowerCase();
  }

public static BiomeGenBase getBiomeByName(String name)
  {
  if(biomeAliasByName.containsKey(name))
    {
    Class clz = biomeAliasByName.get(name);
    for(BiomeGenBase b : BiomeGenBase.getBiomeGenArray())
      {
      if(b==null){continue;}
      if(clz.equals(b.getClass()))
        {
        return b;
        }
      }
    }
  else
    {
    for(BiomeGenBase b : BiomeGenBase.getBiomeGenArray())
      {
      if(b==null){continue;}
      if(b.biomeName.equals(name))
        {
        return b;
        }
      }
    }
  return null;
  }
}
