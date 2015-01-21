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

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.config.ModConfiguration;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class AWStructureStatics extends ModConfiguration {


    public AWStructureStatics(Configuration config) {
        super(config);
    }

    public static String templateExtension = "aws";
    public static String townTemplateExtension = "awt";
    public static boolean enableVillageGen = true;
    public static boolean enableTownGeneration = true;
    public static boolean enableStructureGeneration = true;
    public static boolean loadDefaultPack = true;
    private static boolean exportBlockNames = false;
    public static int clusterValueSearchRange = 16;
    public static int duplicateStructureSearchRange = 40;
    public static int maxClusterValue = 500;
    public static float randomGenerationChance = 0.075f;
    public static int spawnProtectionRange = 12;
    public static int structureImageWidth = 512;
    public static int structureImageHeight = 288;
    public static int townClosestDistance = 40;
    public static float townGenerationChance = 0.125f;
    public static Set<String> excludedSpawnerEntities = new HashSet<String>();
    private static HashSet<String> skippableWorldGenBlocks = new HashSet<String>();
    private static HashSet<String> worldGenTargetBlocks = new HashSet<String>();
    private static HashSet<String> scannerSkippedBlocks = new HashSet<String>();
    private static HashSet<String> townValidTargetBlocks = new HashSet<String>();

    private static String worldGenCategory = "a_world-gen_settings";
    private static String villageGenCategory = "b_village-gen_settings";
    private static String excludedEntitiesCategory = "c_excluded_spawner_entities";
    private static String worldGenBlocks = "d_world_gen_skippable_blocks";
    private static String targetBlocks = "e_world_gen_target_blocks";
    private static String scanSkippedBlocks = "f_scanner_skipped_blocks";
    private static String townValidTargetBlocksCategory = "g_town_target_blocks";

    @Override
    public void initializeCategories() {
        this.config.addCustomCategoryComment(worldGenCategory, "Settings that effect all world-structure-generation.");
        this.config.addCustomCategoryComment(villageGenCategory, "Settings that effect the generation of vanilla villages.\nCurrently there are no village-generation options, and no structures will generate in villages.");
        this.config.addCustomCategoryComment(excludedEntitiesCategory, "Entities that will not show up in the Mob Spawner Placer entity selection list.\nAdd any mobs here that will crash if spawned via the vanilla mob-spawner (usually complex NBT-defined entities).");
        this.config.addCustomCategoryComment(worldGenBlocks, "Blocks that should be skipped/ignored during world gen -- should list all plant blocks/logs/foliage");
        this.config.addCustomCategoryComment(targetBlocks, "List of target blocks to add to the target-block selection GUI.\nVanilla block names should be listed as the 1.7 registered name. \nMod blocks should be listed as their registered name");
        this.config.addCustomCategoryComment(scanSkippedBlocks, "List of blocks that the structure scanner will completely ignore.\nWhenever these blocks are encountered the template will instead fill that block position with a hard-air rule.\nAdd any blocks to this list that may cause crashes when scanned or duplicated.\nVanilla blocks should not need to be added, but some mod-blocks may.\nBlock names must be specified by fully-qualified name (e.g. \"minecraft:stone\")");
        this.config.addCustomCategoryComment(townValidTargetBlocksCategory, "List of blocks that are valid target blocks for town creation.\nAny solid block found that is not on this list will prevent a town from spawning in a given chunk");
    }

    @Override
    protected void initializeValues() {
        templateExtension = config.get(worldGenCategory, "template_extension", "aws", "Default=" + templateExtension + "\n" +
                "The template extension used when looking for and exporting templates.\n" +
                "Only files matching this extension will be examined.").getString();
        enableVillageGen = config.get(worldGenCategory, "enable_village_feature_generation", enableVillageGen, "Default=" + enableVillageGen + "\n" +
                "Enable or disable generation of additional village features.").getBoolean(enableVillageGen);
        enableStructureGeneration = config.get(worldGenCategory, "enable_structure_generation", enableStructureGeneration, "Default=" + enableStructureGeneration + "\n" +
                "Enable or disable structure generation entirely.").getBoolean(enableStructureGeneration);
        enableTownGeneration = config.get(worldGenCategory, "enable_town_generation", enableTownGeneration, "Default=" + enableTownGeneration + "\n" +
                "Enable or disable custom town generation entirely.").getBoolean(enableTownGeneration);
        loadDefaultPack = config.get(worldGenCategory, "load_default_structure_pack", loadDefaultPack, "If true the default structure pack will be loaded and enabled for world-gen.").getBoolean(loadDefaultPack);
        duplicateStructureSearchRange = config.get(worldGenCategory, "validation_duplicate_search_radius", duplicateStructureSearchRange, "Default=" + duplicateStructureSearchRange + "\n" +
                "The minimum radius in chunks to be searched for duplicate structures.\n" +
                "This setting should generally not need to be adjusted unless you have templates with extremely\n" +
                "large 'minDuplicateDistance' values\n" +
                "Extremely large values may introduce extra lag during generation.  Lower values may reduce lag during generation,\n" +
                "at the cost of some accuracy in the min duplicate distance tests.").getInt(duplicateStructureSearchRange);
        clusterValueSearchRange = config.get(worldGenCategory, "validation_cluster_value_search_radius", clusterValueSearchRange, "Default=" + clusterValueSearchRange + "\n" +
                "The minimum radius in chunks to be searched for structures when tallying cluster value in an area.\n" +
                "This setting should be adjusted along with maxClusterValue and the clusterValue in templates to encourage\n" +
                "or discourage specific structures to generate near eachother.\n" +
                "Extremely large values may introduce extra lag during generation.  Lower values may reduce lag during generation,\n" +
                "at the cost of some accuracy in the cluster value tests.").getInt(clusterValueSearchRange);
        maxClusterValue = config.get(worldGenCategory, "max_cluster_value", maxClusterValue, "Default=" + maxClusterValue + "\n" +
                "The maximum allowed cluster value that may be present inside of 'validation_chunk_radius'.\n" +
                "").getInt(maxClusterValue);
        randomGenerationChance = (float) config.get(worldGenCategory, "random_generation_chance", randomGenerationChance, "Default=" + randomGenerationChance + "\n" +
                "Accepts values between 0 and 1.\n" +
                "Determines the chance that a structure will attempt to be generated in any given chunk.\n" +
                "Number is specified as a percentage -- e.g. 0.75 == 75% chance to attempt generation.\n" +
                "Higher values will result in more attempts to generate structures.  Actual number\n" +
                "generated will depend upon your specific templates and their validation settings.\n" +
                "Values of 0 or lower will result in no structures generating.  Values higher than 1\n" +
                "will result in a generation attempt in every chunk.").getDouble(randomGenerationChance);
        spawnProtectionRange = config.get(worldGenCategory, "spawn_protection_chunk_radius", spawnProtectionRange, "Default=" + spawnProtectionRange + "\n" +
                "Determines the area around the central spawn coordinate that will be excluded from random structure generation.\n" +
                "Larger values will see a larger area around spawn that is devoid of structures.").getInt(spawnProtectionRange);
        exportBlockNames = config.get(AWCoreStatics.serverOptions, "export_block_name_list", exportBlockNames, "Default=false\n" +
                "If true, will export a list of all registered block names on startup.\n" +
                "Will toggle itself back to false after exporting the list a single time.\n" +
                "Block names be used to populate skippable and target blocks lists.\n" +
                "If false, no action will be taken.").getBoolean(exportBlockNames);

        townClosestDistance = config.get(worldGenCategory, "town_min_distance", townClosestDistance, "Default=" + townClosestDistance + "\n" +
                "Minimum distance between towns.  This should be set to a value quite a bit larger than the largest town" +
                "that you have configured for generation.  E.G.  Max town size=16, this value should be >= 40.").getInt(townClosestDistance);
        townGenerationChance = (float) config.get(worldGenCategory, "town_generation_chance", townGenerationChance, "Default=" + townGenerationChance + "\n" +
                "Accepts values between 0 and 1.0.  Decimal percent chance to -attempt- town generation for any given chunk.  Higher settings may result in" +
                "more towns being generated, but may come with a peformance hit during new chunk generation.  Lower values WILL result in fewer towns, and" +
                "-may- improve performance during chunk generation.").getDouble(townGenerationChance);

        initializeDefaultSkippableBlocks();
        initializeDefaultSkippedEntities();
        initializeDefaultAdditionalTargetBlocks();
        initializeDefaultTownTargetBlocks();
        initializeScannerSkippedBlocks();
        this.config.save();
    }

    private void initializeScannerSkippedBlocks() {
        String[] defaultSkippableBlocks = new String[]
                {
                        "AncientWarfareStructure:gate_proxy",//skip gate proxy blocks by default... possibly some others that need skipping as well
                };
        defaultSkippableBlocks = config.getStringList("scanner_skipped_blocks", scanSkippedBlocks, defaultSkippableBlocks, "Blocks to be skipped by structure scanner");
        for (String b : defaultSkippableBlocks) {
            scannerSkippedBlocks.add(b);
        }
    }

    private void initializeDefaultSkippableBlocks() {
        String[] defaultSkippableBlocks = new String[]
                {
                        "minecraft:air",
                        "minecraft:sapling",
                        "minecraft:log",
                        "minecraft:leaves",
                        "minecraft:sponge",
                        "minecraft:web",
                        "minecraft:tallgrass",
                        "minecraft:deadbush",
                        "minecraft:yellow_flower",
                        "minecraft:red_flower",
                        "minecraft:brown_mushroom",
                        "minecraft:red_mushroom",
                        "minecraft:snow_layer",
                        "minecraft:ice",
                        "minecraft:snow",
                        "minecraft:cactus",
                        "minecraft:reeds",
                        "minecraft:pumpkin",
                        "minecraft:brown_mushroom_block",
                        "minecraft:red_mushroom_block",
                        "minecraft:melon_block",
                        "minecraft:pumpkin_stem",
                        "minecraft:melon_stem",
                        "minecraft:vine",
                        "minecraft:waterlily",
                        "minecraft:cocoa",
                        "minecraft:leaves2",
                        "minecraft:log2",
                        "minecraft:packed_ice",
                        "minecraft:double_plant",
                        "BiomesOPlenty:plants",
                        "BiomesOPlenty:flowers",
                        "BiomesOPlenty:flowers2",
                        "BiomesOPlenty:stoneFormations",
                        "BiomesOPlenty:mushrooms",
                        "BiomesOPlenty:willow",
                        "BiomesOPlenty:ivy",
                        "BiomesOPlenty:treeMoss",
                        "BiomesOPlenty:flowerVine",
                        "BiomesOPlenty:wisteria",
                        "BiomesOPlenty:foliage",
                        "BiomesOPlenty:turnip",
                        "BiomesOPlenty:coral1",
                        "BiomesOPlenty:coral2",
                        "BiomesOPlenty:hardIce",
                        "BiomesOPlenty:appleLeaves",
                        "BiomesOPlenty:persimmonLeaves",
                        "BiomesOPlenty:moss",
                        "BiomesOPlenty:bamboo",
                        "BiomesOPlenty:mudBricks",
                        "BiomesOPlenty:originGrass",
                        "BiomesOPlenty:longGrass",
                        "BiomesOPlenty:overgrownNetherrack",
                        "BiomesOPlenty:bopGrass",
                        "BiomesOPlenty:newBopGrass",
                        "BiomesOPlenty:newBopDirt",
                        "BiomesOPlenty:logs1",
                        "BiomesOPlenty:logs2",
                        "BiomesOPlenty:logs3",
                        "BiomesOPlenty:logs4",
                        "BiomesOPlenty:leaves1",
                        "BiomesOPlenty:leaves2",
                        "BiomesOPlenty:leaves3",
                        "BiomesOPlenty:leaves4",
                        "BiomesOPlenty:petals",
                        "BiomesOPlenty:saplings",
                        "BiomesOPlenty:colorizedSaplings",
                        "BiomesOPlenty:hive",
                        "BiomesOPlenty:honeyBlock",
                        "BiomesOPlenty:bones",
                        "BiomesOPlenty:grave",
                        "BiomesOPlenty:colorizedLeaves1",
                        "BiomesOPlenty:colorizedLeaves2",
                        "BiomesOPlenty:poison",
                        "BiomesOPlenty:hell_blood",
                        "BiomesOPlenty:honey",
                        "BiomesOPlenty:ash",
                        "BiomesOPlenty:gemOre",
                        "coralmod:Coral1",
                        "coralmod:Coral2",
                        "coralmod:Coral3",
                        "coralmod:Coral4",
                        "coralmod:Coral5",
                        "Highlands:hl_cocoa",
                        "Highlands:Fir Sapling",
                        "Highlands:tile.hl_acaciaSapling",
                        "Highlands:tile.hl_poplarSapling",
                        "Highlands:tile.hl_redwoodSapling",
                        "Highlands:tile.hl_canopySapling",
                        "Highlands:tile.hl_greatOakSapling",
                        "Highlands:tile.hl_beechSapling",
                        "Highlands:tile.hl_evgBushSapling",
                        "Highlands:tile.hl_decBushSapling",
                        "Highlands:tile.hl_palmSapling",
                        "Highlands:tile.hl_deadSapling",
                        "Highlands:tile.hl_ironwoodSapling",
                        "Highlands:tile.hl_mangroveSapling",
                        "Highlands:tile.hl_ashSapling",
                        "Highlands:tile.hl_autumnOrangeSapling",
                        "Highlands:tile.hl_autumnYellowSapling",
                        "Highlands:tile.hl_japaneseMapleSapling",
                        "Highlands:tile.hl_firWood",
                        "Highlands:tile.hl_acaciaWood",
                        "Highlands:tile.hl_poplarWood",
                        "Highlands:tile.hl_redwoodWood",
                        "Highlands:tile.hl_canopyWood",
                        "Highlands:tile.hl_mangroveWood",
                        "Highlands:tile.hl_ashWood",
                        "Highlands:tile.hl_palmWood",
                        "Highlands:tile.hl_ironwoodWood",
                        "Highlands:tile.hl_japaneseMapleWood",
                        "Highlands:tile.hl_firLeaves",
                        "Highlands:tile.hl_acaciaLeaves",
                        "Highlands:tile.hl_poplarLeaves",
                        "Highlands:tile.hl_redwoodLeaves",
                        "Highlands:tile.hl_canopyLeaves",
                        "Highlands:tile.hl_ironwoodLeaves",
                        "Highlands:tile.hl_mangroveLeaves",
                        "Highlands:tile.hl_ashLeaves",
                        "Highlands:tile.hl_palmLeaves",
                        "Highlands:tile.hl_autumnOrangeLeaves",
                        "Highlands:tile.hl_autumnYellowLeaves",
                        "Highlands:tile.hl_japaneseMapleLeaves",
                        "Highlands:tile.hl_blueFlower",
                        "Highlands:tile.hl_leafyFern",
                        "Highlands:tile.hl_whiteFlower",
                        "Highlands:tile.hl_cattail",
                        "Highlands:tile.hl_lavender",
                        "Highlands:tile.hl_raspberryBush",
                        "Highlands:tile.hl_blueberryBush",
                        "Highlands:tile.hl_thornbush",
                        "Highlands:tile.hl_cotton",
                        "mam:mam_dendroidsapling",
                        "mam:mam_fairymushroom",
                        "mam:mam_mamdendroidspawner",
                        "bonecraft:Fossil",
                        "harvestcraft:beehive",
                        "harvestcraft:apiary",
                        "harvestcraft:berrygarden",
                        "harvestcraft:desertgarden",
                        "harvestcraft:grassgarden",
                        "harvestcraft:gourdgarden",
                        "harvestcraft:groundgarden",
                        "harvestcraft:herbgarden",
                        "harvestcraft:leafygarden",
                        "harvestcraft:mushroomgarden",
                        "harvestcraft:stalkgarden",
                        "harvestcraft:textilegarden",
                        "harvestcraft:tropicalgarden",
                        "harvestcraft:watergarden",
                        "harvestcraft:pamApple",
                        "harvestcraft:pamappleSapling",
                        "harvestcraft:pamAlmond",
                        "harvestcraft:pamalmondSapling",
                        "harvestcraft:pamApricot",
                        "harvestcraft:pamapricotSapling",
                        "harvestcraft:pamAvocado",
                        "harvestcraft:pamavocadoSapling",
                        "harvestcraft:pamBanana",
                        "harvestcraft:pambananaSapling",
                        "harvestcraft:pamCashew",
                        "harvestcraft:pamcashewSapling",
                        "harvestcraft:pamCherry",
                        "harvestcraft:pamcherrySapling",
                        "harvestcraft:pamChestnut",
                        "harvestcraft:pamchestnutSapling",
                        "harvestcraft:pamCinnamon",
                        "harvestcraft:pamcinnamonSapling",
                        "harvestcraft:pamCoconut",
                        "harvestcraft:pamcoconutSapling",
                        "harvestcraft:pamDate",
                        "harvestcraft:pamdateSapling",
                        "harvestcraft:pamDragonfruit",
                        "harvestcraft:pamdragonfruitSapling",
                        "harvestcraft:pamDurian",
                        "harvestcraft:pamdurianSapling",
                        "harvestcraft:pamFig",
                        "harvestcraft:pamfigSapling",
                        "harvestcraft:pamGrapefruit",
                        "harvestcraft:pamgrapefruitSapling",
                        "harvestcraft:pamLemon",
                        "harvestcraft:pamlemonSapling",
                        "harvestcraft:pamLime",
                        "harvestcraft:pamlimeSapling",
                        "harvestcraft:pamMaple",
                        "harvestcraft:pammapleSapling",
                        "harvestcraft:pamMango",
                        "harvestcraft:pammangoSapling",
                        "harvestcraft:pamNutmeg",
                        "harvestcraft:pamnutmegSapling",
                        "harvestcraft:pamOlive",
                        "harvestcraft:pamoliveSapling",
                        "harvestcraft:pamOrange",
                        "harvestcraft:pamorangeSapling",
                        "harvestcraft:pamPapaya",
                        "harvestcraft:pampapayaSapling",
                        "harvestcraft:pamPaperbark",
                        "harvestcraft:pampaperbarkSapling",
                        "harvestcraft:pamPeach",
                        "harvestcraft:pampeachSapling",
                        "harvestcraft:pamPear",
                        "harvestcraft:pampearSapling",
                        "harvestcraft:pamPecan",
                        "harvestcraft:pampecanSapling",
                        "harvestcraft:pamPeppercorn",
                        "harvestcraft:pampeppercornSapling",
                        "harvestcraft:pamPersimmon",
                        "harvestcraft:pampersimmonSapling",
                        "harvestcraft:pamPistachio",
                        "harvestcraft:pampistachioSapling",
                        "harvestcraft:pamPlum",
                        "harvestcraft:pamplumSapling",
                        "harvestcraft:pamPomegranate",
                        "harvestcraft:pampomegranateSapling",
                        "harvestcraft:pamStarfruit",
                        "harvestcraft:pamstarfruitSapling",
                        "harvestcraft:pamVanillabean",
                        "harvestcraft:pamvanillabeanSapling",
                        "harvestcraft:pamWalnut",
                        "harvestcraft:pamwalnutSapling",
                        "harvestcraft:pamblackberryCrop",
                        "harvestcraft:pamblueberryCrop",
                        "harvestcraft:pamcandleberryCrop",
                        "harvestcraft:pamraspberryCrop",
                        "harvestcraft:pamstrawberryCrop",
                        "harvestcraft:pamcactusfruitCrop",
                        "harvestcraft:pamasparagusCrop",
                        "harvestcraft:pambarleyCrop",
                        "harvestcraft:pamoatsCrop",
                        "harvestcraft:pamryeCrop",
                        "harvestcraft:pamcornCrop",
                        "harvestcraft:pambambooshootCrop",
                        "harvestcraft:pamcantaloupeCrop",
                        "harvestcraft:pamcucumberCrop",
                        "harvestcraft:pamwintersquashCrop",
                        "harvestcraft:pamzucchiniCrop",
                        "harvestcraft:pambeetCrop",
                        "harvestcraft:pamonionCrop",
                        "harvestcraft:pamparsnipCrop",
                        "harvestcraft:pampeanutCrop",
                        "harvestcraft:pamradishCrop",
                        "harvestcraft:pamrutabagaCrop",
                        "harvestcraft:pamsweetpotatoCrop",
                        "harvestcraft:pamturnipCrop",
                        "harvestcraft:pamrhubarbCrop",
                        "harvestcraft:pamceleryCrop",
                        "harvestcraft:pamgarlicCrop",
                        "harvestcraft:pamgingerCrop",
                        "harvestcraft:pamspiceleafCrop",
                        "harvestcraft:pamtealeafCrop",
                        "harvestcraft:pamcoffeebeanCrop",
                        "harvestcraft:pammustardseedsCrop",
                        "harvestcraft:pambroccoliCrop",
                        "harvestcraft:pamcauliflowerCrop",
                        "harvestcraft:pamleekCrop",
                        "harvestcraft:pamlettuceCrop",
                        "harvestcraft:pamscallionCrop",
                        "harvestcraft:pamartichokeCrop",
                        "harvestcraft:pambrusselsproutCrop",
                        "harvestcraft:pamcabbageCrop",
                        "harvestcraft:pamwhitemushroomCrop",
                        "harvestcraft:pambeanCrop",
                        "harvestcraft:pamsoybeanCrop",
                        "harvestcraft:pambellpepperCrop",
                        "harvestcraft:pamchilipepperCrop",
                        "harvestcraft:pameggplantCrop",
                        "harvestcraft:pamokraCrop",
                        "harvestcraft:pampeasCrop",
                        "harvestcraft:pamtomatoCrop",
                        "harvestcraft:pamcottonCrop",
                        "harvestcraft:pampineappleCrop",
                        "harvestcraft:pamgrapeCrop",
                        "harvestcraft:pamkiwiCrop",
                        "harvestcraft:pamcranberryCrop",
                        "harvestcraft:pamriceCrop",
                        "harvestcraft:pamseaweedCrop",
                        "ProjectZulu|Core:aloevera",
                        "ProjectZulu|Core:watereddirt",
                        "ProjectZulu|Core:tumbleweed",
                        "ProjectZulu|Core:jasper",
                        "ProjectZulu|Core:palmtreelog",
                        "ProjectZulu|Core:palmtreeleaves",
                        "ProjectZulu|Core:palmtreesapling",
                        "ProjectZulu|Core:coconut",
                        "ProjectZulu|Core:quicksand",
                        "ProjectZulu|Core:nightbloom",
                        "ProjectZulu|Core:creeperblossom",
                        "ProjectZulu|Core:spikes",
                        "TwilightForest:tile.TFLog",
                        "TwilightForest:tile.TFRoots",
                        "TwilightForest:tile.TFLeaves",
                        "TwilightForest:tile.TFFirefly",
                        "TwilightForest:tile.TFCicada",
                        "TwilightForest:tile.TFPlant",
                        "TwilightForest:tile.TFSapling",
                        "TwilightForest:tile.TFMoonworm",
                        "TwilightForest:tile.TFMagicLog",
                        "TwilightForest:tile.TFMagicLeaves",
                        "TwilightForest:tile.TFMagicLogSpecial",
                        "TwilightForest:tile.TFThorns",
                        "TwilightForest:tile.TFBurntThorns",
                        "TwilightForest:tile.TFThornRose",
                        "TwilightForest:tile.TFLeaves3",
                        "TwilightForest:tile.TFDeadrock",
                        "TwilightForest:tile.DarkLeaves",
                        "wildcaves3:StoneStalactite",
                        "wildcaves3:SandstoneSalactite",
                        "wildcaves3:Decorations",
                        "wildcaves3:Flora",
                        "wildcaves3:FossilBlock",
                        "witchery:belladonna",
                        "witchery:mandrake",
                        "witchery:artichoke",
                        "witchery:snowbell",
                        "witchery:wormwood",
                        "witchery:mindrake",
                        "witchery:witchsapling",
                        "witchery:witchlog",
                        "witchery:witchleaves",
                        "witchery:voidbramble",
                        "witchery:bramble",
                        "witchery:glintweed",
                        "witchery:spanishmoss",
                        "witchery:leapinglily",
                        "witchery:plantmine",
                        "witchery:embermoss",
                        "witchery:crittersnare",
                        "witchery:grassper",
                        "witchery:bloodrose",
                        "witchery:somniancotton",
                        "witchery:demonheart",
                        "witchery:witchwood",
                        "Metallurgy:base.ore",
                        "Metallurgy:ender.ore",
                        "Metallurgy:fantasy.ore",
                        "Metallurgy:nether.ore",
                        "Metallurgy:precious.ore",
                        "Metallurgy:utility.ore",
                        "MineFactoryReloaded:tile.mfr.rubberwood.leaves",
                        "MineFactoryReloaded:tile.mfr.rubberwood.log"
                };

        defaultSkippableBlocks = config.get(worldGenBlocks, "skippable_blocks", defaultSkippableBlocks).getStringList();
        for (String st : defaultSkippableBlocks) {
            skippableWorldGenBlocks.add(st);
        }
    }

    private void initializeDefaultSkippedEntities() {
        String[] defaultExcludedEntities = new String[]
                {
                        "AncientWarfare.entity.gate",
                        "AncientWarfare.entity.missile",
                        "AncientWarfare.entity.npc",
                        "AncientWarfare.entity.vehicle",
                        "AncientWarfareStructure.aw_gate",
                        "Arrow",
                        "awger_Hoy.EntityHoy",
                        "awer_Punt.EntityPunt",
                        "awger_SmallBoat.EntityBoatChest",
                        "awger_SmallBoat.EntityBoatPart",
                        "awger_SmallBoat.EntitySmallBoat",
                        "awger_Whitehall.EntityWhitehall",
                        "BiblioCraft.SeatEntity",
                        "BiomesOPlenty.Dart",
                        "BiomesOPlenty.MudBall",
                        "BiomesOPlenty.PoisonDart",
                        "BladeTrap",
                        "Boat",
                        "Cannon",
                        "DemonMobs.DemonicBlast",
                        "DemonMobs.DemonicSpark",
                        "DemonMobs.Doomfireball",
                        "DemonMobs.Hellfireball",
                        "DesertMobs.Mudshot",
                        "DesertMobs.ThrowingScythe",
                        "EnderCrystal",
                        "EnderDragon",
                        "extrabiomes.scarecrow",
                        "EyeOfEnderSignal",
                        "EyeRay",
                        "FallingSand",
                        "Fireball",
                        "FireworksRocketEntity",
                        "goblin.Arcaneball",
                        "goblin.Bomb",
                        "goblin.ETNTPrimed",
                        "goblin.GArcanebal",
                        "goblin.Lightball",
                        "goblin.MTNTPrimed",
                        "goblin.orbB",
                        "goblin.orbG",
                        "goblin.orbR",
                        "goblin.orbY",
                        "Item",
                        "ItemFrame",
                        "JungleMobs.ConcapedeSegment",
                        "KoadPirates.Cannon Ball",
                        "KoadPirates.Net",
                        "KoadPirates.Shot",
                        "KoadPirates.Tether",
                        "LeashKnot",
                        "LOTR.Barrel",
                        "LOTR.Crossbow Bolt",
                        "LOTR.Gandalf Fireball",
                        "LOTR.Gollum",
                        "LOTR.Marsh Wraith Ball",
                        "LOTR.Mystery Web",
                        "LOTR.Orc Bomb",
                        "LOTR.Pebble",
                        "LOTR.Plate",
                        "LOTR.Portal",
                        "LOTR.Smoke Ring",
                        "LOTR.Spear",
                        "LOTR.Throwing Axe",
                        "LOTR.Thrown Rock",
                        "LOTR.Trader Respawn",
                        "LOTR.Wargskin Rug",
                        "MagicMissile",
                        "mam.firebreath",
                        "mam.goldarrow",
                        "mam.kitsune",
                        "mam.music",
                        "mam.stick",
                        "Metallurgy3Base.LargeTNTEntity",
                        "Metallurgy3Base.MinersTNTEntity",
                        "MinecartChest",
                        "MinecartCommandBlock",
                        "MinecartFurnace",
                        "MinecartHopper",
                        "MinecartRideable",
                        "MinecartSpawner",
                        "MinecartTNT",
                        "minecolonies.arrow",
                        "minecolonies.baker",
                        "minecolonies.blacksmith",
                        "minecolonies.builder",
                        "minecolonies.citizen",
                        "minecolonies.deliveryman",
                        "minecolonies.farmer",
                        "minecolonies.huntersdog",
                        "minecolonies.miner",
                        "minecolonies.pointer",
                        "minecolonies.soldier",
                        "minecolonies.stonemason",
                        "minecolonies.stonemason",
                        "MinecraftFurnace",
                        "Mob",
                        "MoCreatures.Egg",
                        "MoCreatures.FishBowl",
                        "MoCreatures.KittyBed",
                        "MoCreatures.LitterBox",
                        "MoCreatures.MoCPlatform",
                        "MoCreatures.PetScorpion",
                        "Monster",
                        "Painting",
                        "Paleocraft.Bladeking68",
                        "Petrified",
                        "PrimedTnt",
                        "RakshasaImage",
                        "RopesPlus.ConfusingArrow",
                        "RopesPlus.DirtArrow",
                        "RopesPlus.ExplodingArrow",
                        "RopesPlus.FireArrow",
                        "RopesPlus.FreeFormRope",
                        "RopesPlus.FrostArrow",
                        "RopesPlus.GrapplingHook",
                        "RopesPlus.PenetratingArrow",
                        "RopesPlus.RedstonetorchArrow",
                        "RopesPlus.RopeArrow",
                        "RopesPlus.SeedArrow",
                        "RopesPlus.SlimeArrow",
                        "RopesPlus.TorchArrow",
                        "RopesPlus.WarpArrow",
                        "SmallFireball",
                        "Snowball",
                        "SonicBoom",
                        "SwampMobs.PoisonRay",
                        "SwampMobs.PoisonRayEnd",
                        "SwampMobs.VenomShot",
                        "Test",
                        "ThrownEnderpearl",
                        "ThrownExpBottle",
                        "ThrownPotion",
                        "TwilightForest.tffallingice",
                        "TwilightForest.tflichbolt",
                        "TwilightForest.tflichbomb",
                        "TwilightForest.tfmoonwormshot",
                        "TwilightForest.tfthrownaxe",
                        "TwilightForest.tfthrownice",
                        "TwilightForest.tfthrownpick",
                        "TwilightForest.tfcharmeffect",
                        "TwilightForest.tfhydramortar",
                        "TwilightForest.tfnaturebolt",
                        "TwilightForest.tfslimeblob",
                        "TwilightForest.tftomebolt",
                        "TwilightForest.tftwilightwandbolt",
                        "weaponmod.bolt",
                        "weaponmod.boomerang",
                        "weaponmod.bullet",
                        "weaponmod.cannon",
                        "weaponmod.cannonball",
                        "weaponmod.dart",
                        "weaponmod.dummy",
                        "weaponmod.dynamite",
                        "weaponmod.flail",
                        "weaponmod.javelin",
                        "weaponmod.knife",
                        "weaponmod.shot",
                        "weaponmod.spear",
                        "witchery.bolt",
                        "witchery.brew",
                        "witchery.broom",
                        "witchery.corpse",
                        "witchery.eye",
                        "witchery.familiar",
                        "witchery.goblingulg",
                        "witchery.illusionCreeper",
                        "witchery.illusionSpider",
                        "witchery.illusionZombie",
                        "witchery.mandrake",
                        "witchery.mandrake",
                        "witchery.owl",
                        "witchery.soulfire",
                        "witchery.spellEffect",
                        "witchery.spirit",
                        "WitherBoss",
                        "WitherSkull",
                        "XPOrb",
                };
        defaultExcludedEntities = config.get(excludedEntitiesCategory, "excluded_entities", defaultExcludedEntities).getStringList();

        for (String st : defaultExcludedEntities) {
            excludedSpawnerEntities.add(st);
        }

    }

    private void initializeDefaultTownTargetBlocks() {
        String[] targetBlocks = new String[]
                {
                        "minecraft:snow",
                        "minecraft:snow_layer",
                        "minecraft:ice",
                        "minecraft:water",
                        "minecraft:clay",
                        "minecraft:mycelium",
                        "minecraft:stone",
                        "minecraft:grass",
                        "minecraft:dirt",
                        "minecraft:sand",
                        "minecraft:gravel",
                        "minecraft:sand",
                        "minecraft:sandstone",
                        "BiomesOPlenty:mud",
                        "BiomesOPlenty:driedDirt",
                        "BiomesOPlenty:rocks",
                        "BiomesOPlenty:ash",
                        "BiomesOPlenty:ashStone",
                        "BiomesOPlenty:hardSand",
                        "BiomesOPlenty:hardDirt",
                        "BiomesOPlenty:biomeBlock",
                        "BiomesOPlenty:crystal",
                        "BiomesOPlenty:gemOre",
                        "BiomesOPlenty:cragRock",
                        "mam:mam_mamgravel",
                        "mam:mam_depthquartz",
                        "mam:mam_depthquartzchiseled",
                        "mam:mam_depthquartzlines",
                        "mam:mam_depthcrystalblock",
                };
        targetBlocks = config.get("town_target_blocks", townValidTargetBlocksCategory, targetBlocks, "List of blocks that are valid").getStringList();
        for (String st : targetBlocks) {
            townValidTargetBlocks.add(st);
        }
    }

    private void initializeDefaultAdditionalTargetBlocks() {
        /**
         * TODO add initial default values for target blocks to this list...
         */
        String[] targetBlocks = new String[]
                {
                        "minecraft:snow",
                        "minecraft:snow_layer",
                        "minecraft:ice",
                        "minecraft:water",
                        "minecraft:clay",
                        "minecraft:mycelium",
                        "minecraft:stone",
                        "minecraft:grass",
                        "minecraft:dirt",
                        "minecraft:sand",
                        "minecraft:gravel",
                        "minecraft:cobblestone",
                        "minecraft:mossy_cobblestone",
                        "minecraft:sandstone",
                        "BiomesOPlenty:mud",
                        "BiomesOPlenty:driedDirt",
                        "BiomesOPlenty:rocks",
                        "BiomesOPlenty:ash",
                        "BiomesOPlenty:ashStone",
                        "BiomesOPlenty:hardSand",
                        "BiomesOPlenty:hardDirt",
                        "BiomesOPlenty:biomeBlock",
                        "BiomesOPlenty:crystal",
                        "BiomesOPlenty:gemOre",
                        "BiomesOPlenty:cragRock",
                        "mam:mam_mamgravel",
                        "mam:mam_depthquartz",
                        "mam:mam_depthquartzchiseled",
                        "mam:mam_depthquartzlines",
                        "mam:mam_depthcrystalblock",

                };
        targetBlocks = config.get(AWStructureStatics.targetBlocks, "target_blocks", targetBlocks).getStringList();
        for (String st : targetBlocks) {
            worldGenTargetBlocks.add(st);
        }
    }

    public static boolean isValidTownTargetBlock(Block block) {
        return block == null || block == Blocks.air ? false : townValidTargetBlocks.contains(BlockDataManager.instance().getNameForBlock(block));
    }

    public static boolean skippableBlocksContains(Block block) {
        return block == null ? true : block == Blocks.air ? true : skippableWorldGenBlocks.contains(BlockDataManager.instance().getNameForBlock(block));
    }

    public static Set<String> getUserDefinedTargetBlocks() {
        return worldGenTargetBlocks;
    }

    public static String getBiomeName(BiomeGenBase biome) {
        return (biome == null || biome.biomeName == null) ? "null" : biome.biomeName.toLowerCase();
    }

    public static boolean shouldSkipScan(Block block) {
        return scannerSkippedBlocks.contains(Block.blockRegistry.getNameForObject(block));
    }

    public void loadPostInitValues() {
        if (exportBlockNames) {
            config.get(AWCoreStatics.serverOptions, "export_block_name_list", false).set(false);
            exportBlockNames = false;
            doBlockNameDump();
        }
    }

    private void doBlockNameDump() {
        File file = new File(AWCoreStatics.configPathForFiles);
        file.mkdirs();
        file = new File(file, "block_names.txt");
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            for (Object key : Block.blockRegistry.getKeys()) {
                fw.write(String.valueOf(key) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
