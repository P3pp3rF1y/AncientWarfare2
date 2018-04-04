/*
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
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.ModConfiguration;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class AWStructureStatics extends ModConfiguration {

    public AWStructureStatics(String mod) {
        super(mod);
    }

    public static String templateExtension = "aws";
    public static String townTemplateExtension = "awt";
    public static boolean enableWorldGen = true;
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
    public static Set<String> excludedSpawnerEntities = new HashSet<>();
    private static HashSet<String> skippableWorldGenBlocks = new HashSet<>();
    private static HashSet<String> worldGenTargetBlocks = new HashSet<>();
    private static HashSet<String> scannerSkippedBlocks = new HashSet<>();
    private static HashSet<String> townValidTargetBlocks = new HashSet<>();

    private static final String worldGenCategory = "a_world-gen_settings";
    private static final String villageGenCategory = "b_village-gen_settings";
    private static final String excludedEntitiesCategory = "c_excluded_spawner_entities";
    private static final String worldGenBlocks = "d_world_gen_skippable_blocks";
    private static final String targetBlocks = "e_world_gen_target_blocks";
    private static final String scanSkippedBlocks = "f_scanner_skipped_blocks";
    private static final String townValidTargetBlocksCategory = "g_town_target_blocks";

    private static final Field BIOME_NAME_FIELD = ReflectionHelper.findField(Biome.class, "field_76791_y", "biomeName");

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
        templateExtension = config.get(worldGenCategory, "template_extension", "aws", "Default=" + templateExtension + "\n" + "The template extension used when looking for and exporting templates.\n" + "Only files matching this extension will be examined.").getString();
        enableStructureGeneration = config.get(worldGenCategory, "enable_structure_generation", enableStructureGeneration, "Default=" + enableStructureGeneration + "\n" + "Enable or disable structure (not town) generation.").getBoolean(enableStructureGeneration);
        enableTownGeneration = config.get(worldGenCategory, "enable_town_generation", enableTownGeneration, "Default=" + enableTownGeneration + "\n" + "Enable or disable custom town generation e.g. walls and additional buildings.").getBoolean(enableTownGeneration);
        loadDefaultPack = config.get(worldGenCategory, "load_default_structure_pack", loadDefaultPack, "If true the default structure pack will be loaded and enabled for world-gen.").getBoolean(loadDefaultPack);
        duplicateStructureSearchRange = config.get(worldGenCategory, "validation_duplicate_search_radius", duplicateStructureSearchRange, "Default=" + duplicateStructureSearchRange + "\n" + "The minimum radius in chunks to be searched for duplicate structures.\n" + "This setting should generally not need to be adjusted unless you have templates with extremely\n" + "large 'minDuplicateDistance' values\n" + "Extremely large values may introduce extra lag during generation.  Lower values may reduce lag during generation,\n" + "at the cost of some accuracy in the min duplicate distance tests.").getInt(duplicateStructureSearchRange);
        clusterValueSearchRange = config.get(worldGenCategory, "validation_cluster_value_search_radius", clusterValueSearchRange, "Default=" + clusterValueSearchRange + "\n" + "The minimum radius in chunks to be searched for structures when tallying cluster value in an area.\n" + "This setting should be adjusted along with maxClusterValue and the clusterValue in templates to encourage\n" + "or discourage specific structures to generate near eachother.\n" + "Extremely large values may introduce extra lag during generation.  Lower values may reduce lag during generation,\n" + "at the cost of some accuracy in the cluster value tests.").getInt(clusterValueSearchRange);
        maxClusterValue = config.get(worldGenCategory, "max_cluster_value", maxClusterValue, "Default=" + maxClusterValue + "\n" + "The maximum allowed cluster value that may be present inside of 'validation_chunk_radius'.\n" + "").getInt(maxClusterValue);
        randomGenerationChance = (float) config.get(worldGenCategory, "random_generation_chance", randomGenerationChance, "Default=" + randomGenerationChance + "\n" + "Accepts values between 0 and 1.\n" + "Determines the chance that a structure will attempt to be generated in any given chunk.\n" + "Number is specified as a percentage -- e.g. 0.75 == 75% chance to attempt generation.\n" + "Higher values will result in more attempts to generate structures.  Actual number\n" + "generated will depend upon your specific templates and their validation settings.\n" + "Values of 0 or lower will result in no structures generating.  Values higher than 1\n" + "will result in a generation attempt in every chunk.").getDouble(randomGenerationChance);
        spawnProtectionRange = config.get(worldGenCategory, "spawn_protection_chunk_radius", spawnProtectionRange, "Default=" + spawnProtectionRange + "\n" + "Determines the area around the central spawn coordinate that will be excluded from random structure generation.\n" + "Larger values will see a larger area around spawn that is devoid of structures.").getInt(spawnProtectionRange);
        exportBlockNames = config.getBoolean("export_block_name_list", serverOptions, exportBlockNames, "If true, will export a list of all registered block names on startup.\n" + "Will toggle itself back to false after exporting the list a single time.\n" + "Block names be used to populate skippable and target blocks lists.\n" + "If false, no action will be taken.");
        enableWorldGen = config.get(serverOptions, "enable_world_generation", enableWorldGen, "Default=" + enableWorldGen + "\n" + "Enable or disable world generation entirely. If disabled, all other options will have no effect.").getBoolean(enableWorldGen);

        townClosestDistance = config.get(worldGenCategory, "town_min_distance", townClosestDistance, "Default=" + townClosestDistance + "\n" + "Minimum distance between towns.  This should be set to a value quite a bit larger than the largest town" + "that you have configured for generation.  E.G.  Max town size=16, this value should be >= 40.").getInt(townClosestDistance);
        townGenerationChance = (float) config.get(worldGenCategory, "town_generation_chance", townGenerationChance, "Default=" + townGenerationChance + "\n" + "Accepts values between 0 and 1.0.  Decimal percent chance to -attempt- town generation for any given chunk.  Higher settings may result in" + "more towns being generated, but may come with a performance hit during new chunk generation.  Lower values WILL result in fewer towns, and" + "-may- improve performance during chunk generation.").getDouble(townGenerationChance);

        initializeDefaultSkippableBlocks();
        initializeDefaultSkippedEntities();
        initializeDefaultAdditionalTargetBlocks();
        initializeDefaultTownTargetBlocks();
        initializeScannerSkippedBlocks();
    }

    private void initializeScannerSkippedBlocks() {
        String[] defaultSkippableBlocks = new String[] {"AncientWarfareStructure:gate_proxy",
                //skip gate proxy blocks by default... possibly some others that need skipping as well
        };
        defaultSkippableBlocks = config.getStringList("scanner_skipped_blocks", scanSkippedBlocks, defaultSkippableBlocks, "Blocks TO be skipped by structure scanner");
        Collections.addAll(scannerSkippedBlocks, defaultSkippableBlocks);
    }

    private void initializeDefaultSkippableBlocks() {
        String[] defaultSkippableBlocks = new String[] {"BiomesOPlenty:appleLeaves",
                "BiomesOPlenty:ash",
                "BiomesOPlenty:bamboo",
                "BiomesOPlenty:bones",
                "BiomesOPlenty:colorizedLeaves1",
                "BiomesOPlenty:colorizedLeaves2",
                "BiomesOPlenty:colorizedSaplings",
                "BiomesOPlenty:coral1",
                "BiomesOPlenty:coral2",
                "BiomesOPlenty:crystal",
                "BiomesOPlenty:flowers",
                "BiomesOPlenty:flowers2",
                "BiomesOPlenty:flowerVine",
                "BiomesOPlenty:foliage",
                "BiomesOPlenty:gemOre",
                "BiomesOPlenty:grave",
                "BiomesOPlenty:hardIce",
                "BiomesOPlenty:hardSand",
                "BiomesOPlenty:hell_blood",
                "BiomesOPlenty:hive",
                "BiomesOPlenty:honey",
                "BiomesOPlenty:honeyBlock",
                "BiomesOPlenty:ivy",
                "BiomesOPlenty:leaves1",
                "BiomesOPlenty:leaves2",
                "BiomesOPlenty:leaves3",
                "BiomesOPlenty:leaves4",
                "BiomesOPlenty:logs1",
                "BiomesOPlenty:logs2",
                "BiomesOPlenty:logs3",
                "BiomesOPlenty:logs4",
                "BiomesOPlenty:longGrass",
                "BiomesOPlenty:moss",
                "BiomesOPlenty:mud",
                "BiomesOPlenty:mudBricks",
                "BiomesOPlenty:mushrooms",
                "BiomesOPlenty:overgrownNetherrack",
                "BiomesOPlenty:palmStairs",
                "BiomesOPlenty:persimmonLeaves",
                "BiomesOPlenty:petals",
                "BiomesOPlenty:plants",
                "BiomesOPlenty:poison",
                "BiomesOPlenty:rocks",
                "BiomesOPlenty:saplings",
                "BiomesOPlenty:stoneFormations",
                "BiomesOPlenty:treeMoss",
                "BiomesOPlenty:turnip",
                "BiomesOPlenty:willow",
                "BiomesOPlenty:wisteria",
                "bonecraft:Fossil",
                "coralmod:Coral1",
                "coralmod:Coral2",
                "coralmod:Coral3",
                "coralmod:Coral4",
                "coralmod:Coral5",
                "ExtrabiomesXL:cornerlog_baldcypress",
                "ExtrabiomesXL:cornerlog_fir",
                "ExtrabiomesXL:cornerlog_oak",
                "ExtrabiomesXL:cornerlog_rainboweucalyptus",
                "ExtrabiomesXL:cornerlog_redwood",
                "ExtrabiomesXL:double_slabRedRock",
                "ExtrabiomesXL:double_woodslab",
                "ExtrabiomesXL:double_woodslab2",
                "ExtrabiomesXL:flower1",
                "ExtrabiomesXL:flower2",
                "ExtrabiomesXL:flower3",
                "ExtrabiomesXL:grass",
                "ExtrabiomesXL:leaf_pile",
                "ExtrabiomesXL:leaves_1",
                "ExtrabiomesXL:leaves_2",
                "ExtrabiomesXL:leaves_3",
                "ExtrabiomesXL:leaves_4",
                "ExtrabiomesXL:log_elbow_baldcypress",
                "ExtrabiomesXL:log_elbow_rainbow_eucalyptus",
                "ExtrabiomesXL:log1",
                "ExtrabiomesXL:log2",
                "ExtrabiomesXL:mini_log_1",
                "ExtrabiomesXL:plants4",
                "ExtrabiomesXL:saplings_1",
                "ExtrabiomesXL:saplings_2",
                "ExtrabiomesXL:terrain_blocks1",
                "ExtrabiomesXL:terrain_blocks2",
                "ExtrabiomesXL:tile.extrabiomes.crop.strawberry",
                "ExtrabiomesXL:vines",
                "ExtrabiomesXL:waterplant1",
                "extvil:DecoSand",
                "extvil:SmoothSand",
                "GraveStone:GSAltar",
                "GraveStone:GSBoneBlock",
                "GraveStone:GSBoneSlab",
                "GraveStone:GSBoneStairs",
                "GraveStone:GSCandle",
                "GraveStone:GSGraveStone",
                "GraveStone:GSHauntedChest",
                "GraveStone:GSMemorial",
                "GraveStone:GSSkullCandle",
                "GraveStone:GSSpawner",
                "GraveStone:GSTrap",
                "harvestcraft:apiary",
                "harvestcraft:beehive",
                "harvestcraft:berrygarden",
                "harvestcraft:desertgarden",
                "harvestcraft:gourdgarden",
                "harvestcraft:grassgarden",
                "harvestcraft:groundgarden",
                "harvestcraft:herbgarden",
                "harvestcraft:leafygarden",
                "harvestcraft:market",
                "harvestcraft:mushroomgarden",
                "harvestcraft:pamAlmond",
                "harvestcraft:pamalmondSapling",
                "harvestcraft:pamApple",
                "harvestcraft:pamappleSapling",
                "harvestcraft:pamApricot",
                "harvestcraft:pamapricotSapling",
                "harvestcraft:pamartichokeCrop",
                "harvestcraft:pamasparagusCrop",
                "harvestcraft:pamAvocado",
                "harvestcraft:pamavocadoSapling",
                "harvestcraft:pambambooshootCrop",
                "harvestcraft:pamBanana",
                "harvestcraft:pambananaSapling",
                "harvestcraft:pambarleyCrop",
                "harvestcraft:pambeanCrop",
                "harvestcraft:pambeetCrop",
                "harvestcraft:pambellpepperCrop",
                "harvestcraft:pamblackberryCrop",
                "harvestcraft:pamblueberryCrop",
                "harvestcraft:pambroccoliCrop",
                "harvestcraft:pambrusselsproutCrop",
                "harvestcraft:pamcabbageCrop",
                "harvestcraft:pamcactusfruitCrop",
                "harvestcraft:pamcandleberryCrop",
                "harvestcraft:pamcantaloupeCrop",
                "harvestcraft:pamCashew",
                "harvestcraft:pamcashewSapling",
                "harvestcraft:pamcauliflowerCrop",
                "harvestcraft:pamceleryCrop",
                "harvestcraft:pamCherry",
                "harvestcraft:pamcherrySapling",
                "harvestcraft:pamChestnut",
                "harvestcraft:pamchestnutSapling",
                "harvestcraft:pamchilipepperCrop",
                "harvestcraft:pamCinnamon",
                "harvestcraft:pamcinnamonSapling",
                "harvestcraft:pamCoconut",
                "harvestcraft:pamcoconutSapling",
                "harvestcraft:pamcoffeebeanCrop",
                "harvestcraft:pamcornCrop",
                "harvestcraft:pamcottonCrop",
                "harvestcraft:pamcranberryCrop",
                "harvestcraft:pamcucumberCrop",
                "harvestcraft:pamDate",
                "harvestcraft:pamdateSapling",
                "harvestcraft:pamDragonfruit",
                "harvestcraft:pamdragonfruitSapling",
                "harvestcraft:pamDurian",
                "harvestcraft:pamdurianSapling",
                "harvestcraft:pameggplantCrop",
                "harvestcraft:pamFig",
                "harvestcraft:pamfigSapling",
                "harvestcraft:pamgarlicCrop",
                "harvestcraft:pamgingerCrop",
                "harvestcraft:pamgrapeCrop",
                "harvestcraft:pamGrapefruit",
                "harvestcraft:pamgrapefruitSapling",
                "harvestcraft:pamkiwiCrop",
                "harvestcraft:pamleekCrop",
                "harvestcraft:pamLemon",
                "harvestcraft:pamlemonSapling",
                "harvestcraft:pamlettuceCrop",
                "harvestcraft:pamLime",
                "harvestcraft:pamlimeSapling",
                "harvestcraft:pamMaple",
                "harvestcraft:pammapleSapling",
                "harvestcraft:pamMango",
                "harvestcraft:pammangoSapling",
                "harvestcraft:pammustardseedsCrop",
                "harvestcraft:pamNutmeg",
                "harvestcraft:pamnutmegSapling",
                "harvestcraft:pamoatsCrop",
                "harvestcraft:pamokraCrop",
                "harvestcraft:pamOlive",
                "harvestcraft:pamoliveSapling",
                "harvestcraft:pamonionCrop",
                "harvestcraft:pamOrange",
                "harvestcraft:pamorangeSapling",
                "harvestcraft:pamPapaya",
                "harvestcraft:pampapayaSapling",
                "harvestcraft:pamPaperbark",
                "harvestcraft:pampaperbarkSapling",
                "harvestcraft:pamparsnipCrop",
                "harvestcraft:pamPeach",
                "harvestcraft:pampeachSapling",
                "harvestcraft:pampeanutCrop",
                "harvestcraft:pamPear",
                "harvestcraft:pampearSapling",
                "harvestcraft:pampeasCrop",
                "harvestcraft:pamPecan",
                "harvestcraft:pampecanSapling",
                "harvestcraft:pamPeppercorn",
                "harvestcraft:pampeppercornSapling",
                "harvestcraft:pamPersimmon",
                "harvestcraft:pampersimmonSapling",
                "harvestcraft:pampineappleCrop",
                "harvestcraft:pamPistachio",
                "harvestcraft:pampistachioSapling",
                "harvestcraft:pamPlum",
                "harvestcraft:pamplumSapling",
                "harvestcraft:pamPomegranate",
                "harvestcraft:pampomegranateSapling",
                "harvestcraft:pamradishCrop",
                "harvestcraft:pamraspberryCrop",
                "harvestcraft:pamrhubarbCrop",
                "harvestcraft:pamriceCrop",
                "harvestcraft:pamrutabagaCrop",
                "harvestcraft:pamryeCrop",
                "harvestcraft:pamscallionCrop",
                "harvestcraft:pamseaweedCrop",
                "harvestcraft:pamsoybeanCrop",
                "harvestcraft:pamspiceleafCrop",
                "harvestcraft:pamStarfruit",
                "harvestcraft:pamstarfruitSapling",
                "harvestcraft:pamstrawberryCrop",
                "harvestcraft:pamsweetpotatoCrop",
                "harvestcraft:pamtealeafCrop",
                "harvestcraft:pamtomatoCrop",
                "harvestcraft:pamturnipCrop",
                "harvestcraft:pamVanillabean",
                "harvestcraft:pamvanillabeanSapling",
                "harvestcraft:pamWalnut",
                "harvestcraft:pamwalnutSapling",
                "harvestcraft:pamwhitemushroomCrop",
                "harvestcraft:pamwintersquashCrop",
                "harvestcraft:pamzucchiniCrop",
                "harvestcraft:stalkgarden",
                "harvestcraft:textilegarden",
                "harvestcraft:tropicalgarden",
                "harvestcraft:watergarden",
                "Highlands:Fir Sapling",
                "Highlands:hl_cocoa",
                "Highlands:tile.hl_acaciaLeaves",
                "Highlands:tile.hl_acaciaSapling",
                "Highlands:tile.hl_acaciaWood",
                "Highlands:tile.hl_ashLeaves",
                "Highlands:tile.hl_ashSapling",
                "Highlands:tile.hl_ashWood",
                "Highlands:tile.hl_autumnOrangeLeaves",
                "Highlands:tile.hl_autumnOrangeSapling",
                "Highlands:tile.hl_autumnYellowLeaves",
                "Highlands:tile.hl_autumnYellowSapling",
                "Highlands:tile.hl_beechSapling",
                "Highlands:tile.hl_blueberryBush",
                "Highlands:tile.hl_blueFlower",
                "Highlands:tile.hl_canopyLeaves",
                "Highlands:tile.hl_canopySapling",
                "Highlands:tile.hl_canopyWood",
                "Highlands:tile.hl_cattail",
                "Highlands:tile.hl_cotton",
                "Highlands:tile.hl_deadSapling",
                "Highlands:tile.hl_decBushSapling",
                "Highlands:tile.hl_evgBushSapling",
                "Highlands:tile.hl_firLeaves",
                "Highlands:tile.hl_firWood",
                "Highlands:tile.hl_greatOakSapling",
                "Highlands:tile.hl_ironwoodLeaves",
                "Highlands:tile.hl_ironwoodSapling",
                "Highlands:tile.hl_ironwoodWood",
                "Highlands:tile.hl_japaneseMapleLeaves",
                "Highlands:tile.hl_japaneseMapleSapling",
                "Highlands:tile.hl_japaneseMapleWood",
                "Highlands:tile.hl_lavender",
                "Highlands:tile.hl_leafyFern",
                "Highlands:tile.hl_mangroveLeaves",
                "Highlands:tile.hl_mangroveSapling",
                "Highlands:tile.hl_mangroveWood",
                "Highlands:tile.hl_palmLeaves",
                "Highlands:tile.hl_palmSapling",
                "Highlands:tile.hl_palmWood",
                "Highlands:tile.hl_poplarLeaves",
                "Highlands:tile.hl_poplarSapling",
                "Highlands:tile.hl_poplarWood",
                "Highlands:tile.hl_raspberryBush",
                "Highlands:tile.hl_redwoodLeaves",
                "Highlands:tile.hl_redwoodSapling",
                "Highlands:tile.hl_redwoodWood",
                "Highlands:tile.hl_thornbush",
                "Highlands:tile.hl_whiteFlower",
                "lotr:tile.lotr:berryBush",
                "lotr:tile.lotr:bluebell",
                "lotr:tile.lotr:clover",
                "lotr:tile.lotr:deadMarshPlant",
                "lotr:tile.lotr:doubleFlower",
                "lotr:tile.lotr:fallenLeaves",
                "lotr:tile.lotr:fallenLeavesLOTR",
                "lotr:tile.lotr:fallenLeavesLOTR2",
                "lotr:tile.lotr:fangornPlant",
                "lotr:tile.lotr:fangornRiverweed",
                "lotr:tile.lotr:flaxPlant",
                "lotr:tile.lotr:fruitLeaves",
                "lotr:tile.lotr:fruitSapling",
                "lotr:tile.lotr:fruitWood",
                "lotr:tile.lotr:haradFlower",
                "lotr:tile.lotr:leaves",
                "lotr:tile.lotr:leaves2",
                "lotr:tile.lotr:leaves3",
                "lotr:tile.lotr:leaves4",
                "lotr:tile.lotr:lettuce",
                "lotr:tile.lotr:mordorMoss",
                "lotr:tile.lotr:mordorThorn",
                "lotr:tile.lotr:morgulShroom",
                "lotr:tile.lotr:pipeweedPlant",
                "lotr:tile.lotr:rottenLog",
                "lotr:tile.lotr:sapling",
                "lotr:tile.lotr:sapling2",
                "lotr:tile.lotr:sapling3",
                "lotr:tile.lotr:sapling4",
                "lotr:tile.lotr:shireHeather",
                "lotr:tile.lotr:simbelmyne",
                "lotr:tile.lotr:stalactite",
                "lotr:tile.lotr:tallGrass",
                "lotr:tile.lotr:termiteMound",
                "lotr:tile.lotr:wood",
                "lotr:tile.lotr:wood2",
                "lotr:tile.lotr:wood3",
                "lotr:tile.lotr:wood4",
                "mam:mam_dendroidsapling",
                "mam:mam_fairymushroom",
                "mam:mam_mamdendroidspawner",
                "Metallurgy:base.ore",
                "Metallurgy:ender.ore",
                "Metallurgy:fantasy.ore",
                "Metallurgy:nether.ore",
                "Metallurgy:precious.ore",
                "Metallurgy:utility.ore",
                "minecraft:air",
                "minecraft:brown_mushroom",
                "minecraft:brown_mushroom_block",
                "minecraft:cactus",
                "minecraft:clay",
                "minecraft:cobblestone",
                "minecraft:cocoa",
                "minecraft:deadbush",
                "minecraft:double_plant",
                "minecraft:flowing_lava",
                "minecraft:flowing_water",
                "minecraft:ice",
                "minecraft:leaves",
                "minecraft:leaves2",
                "minecraft:log",
                "minecraft:log2",
                "minecraft:melon_block",
                "minecraft:melon_stem",
                "minecraft:packed_ice",
                "minecraft:pumpkin",
                "minecraft:pumpkin_stem",
                "minecraft:red_flower",
                "minecraft:red_mushroom",
                "minecraft:red_mushroom_block",
                "minecraft:reeds",
                "minecraft:sapling",
                "minecraft:skull",
                "minecraft:snow",
                "minecraft:snow_layer",
                "minecraft:sponge",
                "minecraft:tallgrass",
                "minecraft:vine",
                "minecraft:waterlily",
                "minecraft:web",
                "minecraft:yellow_flower",
                "MineFactoryReloaded:tile.mfr.rubberwood.leaves",
                "MineFactoryReloaded:tile.mfr.rubberwood.log",
                "MoCreatures:MoCLeaves",
                "MoCreatures:MoCLog",
                "MoCreatures:MoCTallGrass",
                "ProjectZulu|Core:aloevera",
                "ProjectZulu|Core:coconut",
                "ProjectZulu|Core:creeperblossom",
                "ProjectZulu|Core:mobskulls",
                "ProjectZulu|Core:nightbloom",
                "ProjectZulu|Core:palmtreeleaves",
                "ProjectZulu|Core:palmtreelog",
                "ProjectZulu|Core:palmtreesapling",
                "ProjectZulu|Core:quicksand",
                "ProjectZulu|Core:spikes",
                "ProjectZulu|Core:tumbleweed",
                "ProjectZulu|Core:watereddirt",
                "TwilightForest:tile.DarkLeaves",
                "TwilightForest:tile.TFBurntThorns",
                "TwilightForest:tile.TFCicada",
                "TwilightForest:tile.TFDeadrock",
                "TwilightForest:tile.TFFirefly",
                "TwilightForest:tile.TFLeaves",
                "TwilightForest:tile.TFLeaves3",
                "TwilightForest:tile.TFLog",
                "TwilightForest:tile.TFMagicLeaves",
                "TwilightForest:tile.TFMagicLog",
                "TwilightForest:tile.TFMagicLogSpecial",
                "TwilightForest:tile.TFMoonworm",
                "TwilightForest:tile.TFPlant",
                "TwilightForest:tile.TFRoots",
                "TwilightForest:tile.TFSapling",
                "TwilightForest:tile.TFThorns",
                "TwilightForest:tile.TFThornRose",
                "wildcaves3:Decorations",
                "wildcaves3:Flora",
                "wildcaves3:FossilBlock",
                "wildcaves3:SandstoneSalactite",
                "wildcaves3:StoneStalactite",
                "witchery:artichoke",
                "witchery:belladonna",
                "witchery:bloodrose",
                "witchery:bramble",
                "witchery:brazier",
                "witchery:crittersnare",
                "witchery:embermoss",
                "witchery:glintweed",
                "witchery:leapinglily",
                "witchery:mandrake",
                "witchery:mindrake",
                "witchery:plantmine",
                "witchery:snowbell",
                "witchery:somniancotton",
                "witchery:spanishmoss",
                "witchery:stockade",
                "witchery:voidbramble",
                "witchery:witchleaves",
                "witchery:witchlog",
                "witchery:witchsapling",
                "witchery:wormwood"};

        defaultSkippableBlocks = config.get(worldGenBlocks, "skippable_blocks", defaultSkippableBlocks).getStringList();
        Collections.addAll(skippableWorldGenBlocks, defaultSkippableBlocks);
    }

    private void initializeDefaultSkippedEntities() {
        String[] defaultExcludedEntities = new String[] {"ancientwarfarestructure:aw_gate",
                "Arrow",
                "awger_Hoy.EntityHoy",
                "awer_Punt.EntityPunt",
                "awger_SmallBoat.EntityBoatChest",
                "awger_SmallBoat.EntityBoatPart",
                "awger_SmallBoat.EntitySmallBoat",
                "awger_Whitehall.EntityWhitehall",
                "BiblioCraft.SeatEntity",
                "BiomesOPlenty.dart",
                "BiomesOPlenty.mudball",
                "BiomesOPlenty.PoisonDart",
                "BladeTrap",
                "Boat",
                "Cannon",
                "cfm.MountableBlock",
                "chocolateQuest.Beam",
                "chocolateQuest.ChocoProjectile",
                "chocolateQuest.CQ_npc",
                "chocolateQuest.dummy",
                "chocolateQuest.EntityPart",
                "chocolateQuest.EntityPartRidable",
                "chocolateQuest.EntityPartSlime",
                "chocolateQuest.Hookshoot",
                "CustomNpcChairMount",
                "DemonMobs.DemonicBlast",
                "DemonMobs.DemonicSpark",
                "DemonMobs.Doomfireball",
                "DemonMobs.Hellfireball",
                "DesertMobs.Mudshot",
                "DesertMobs.ThrowingScythe",
                "EnderCrystal",
                "EnderDragon",
                "ExtrabiomesXL.scarecrow",
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
                "hoy.Hoy",
                "Item",
                "ItemFrame",
                "JungleMobs.ConcapedeSegment",
                "KoadPirates.Cannon Ball",
                "KoadPirates.Net",
                "KoadPirates.Shot",
                "KoadPirates.Tether",
                "LeashKnot",
                "lom_DirtPile",
                "lotr.Banner",
                "lotr.Barrel",
                "lotr.Conker",
                "lotr.CrossbowBolt",
                "lotr.GandalfFireball",
                "lotr.InvasionSpawner",
                "lotr.LOTRTNT",
                "lotr.MysteryWeb",
                "lotr.OrcBomb",
                "lotr.Pebble",
                "lotr.Plate",
                "lotr.Portal",
                "lotr.SmokeRing",
                "lotr.Spear",
                "lotr.ThrowingAxe",
                "lotr.ThrownRock",
                "lotr.ThrownTermite",
                "lotr.TraderRespawn",
                "lotr.WallBanner",
                "lotr.WargRug",
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
                "Mob",
                "MoCreatures.Egg",
                "MoCreatures.FishBowl",
                "MoCreatures.KittyBed",
                "MoCreatures.LitterBox",
                "MoCreatures.MoCPlatform",
                "MoCreatures.PetScorpion",
                "MoCreatures.TRock",
                "Monster",
                "npccrystal",
                "NpcDragon",
                "npcdwarffemale",
                "npcdwarfmale",
                "npcelffemale",
                "npcelfmale",
                "npcenderchibi",
                "npcEnderman",
                "npcfurryfemale",
                "npcfurrymale",
                "npcGolem",
                "npchumanfemale",
                "npchumanmale",
                "npcnagafemale",
                "npcnagamale",
                "npcorcfemale",
                "npcorcmale",
                "npcpony",
                "npcskeleton",
                "NpcSlime",
                "npcvillager",
                "npczombiefemale",
                "npczombiemale",
                "Painting",
                "Paleocraft.Bladeking68",
                "Petrified",
                "PrimedTnt",
                "primitivemobs.Ball",
                "primitivemobs.Lily",
                "primitivemobs.SpawnEgg",
                "ProjectZulu|Core.CreeperBlossomPrimed",
                "ProjectZulu|Core.DuckEgg",
                "ProjectZulu|Core.Follower",
                "ProjectZulu|Core.Lizard Spit",
                "ProjectZulu|Core.OstrichEgg",
                "ProjectZulu|Core.ThrowingRock",
                "punt.Punt",
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
                "smallboats.EntityBoatChest",
                "smallboats.EntityBoatGun",
                "smallboats.EntityBoatPart",
                "smallboats.EntitySmallBoat",
                "SmallFireball",
                "Snowball",
                "SonicBoom",
                "SwampMobs.PoisonRay",
                "SwampMobs.PoisonRayEnd",
                "SwampMobs.VenomShot",
                "throwableitem",
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
                "witchery.owl",
                "witchery.soulfire",
                "witchery.spellEffect",
                "witchery.spirit",
                "WitherBoss",
                "WitherSkull",
                "XPOrb",};
        defaultExcludedEntities = config.get(excludedEntitiesCategory, "excluded_entities", defaultExcludedEntities).getStringList();

        Collections.addAll(excludedSpawnerEntities, defaultExcludedEntities);
    }

    private void initializeDefaultTownTargetBlocks() {
        String[] targetBlocks = new String[] {"minecraft:snow",
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
                "mam:mam_depthcrystalblock",};
        targetBlocks = config.get("town_target_blocks", townValidTargetBlocksCategory, targetBlocks, "List of blocks that are valid").getStringList();
        Collections.addAll(townValidTargetBlocks, targetBlocks);
    }

    private void initializeDefaultAdditionalTargetBlocks() {
        /*
         * TODO add initial default values for target blocks to this list...
         */
        String[] targetBlocks = new String[] {"minecraft:snow",
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
        Collections.addAll(worldGenTargetBlocks, targetBlocks);
    }

    public static boolean isValidTownTargetBlock(Block block) {
        return !(block == null || block == Blocks.AIR) && townValidTargetBlocks.contains(BlockDataManager.INSTANCE.getNameForBlock(block));
    }

    public static boolean skippableBlocksContains(Block block) {
        return block == null || block == Blocks.AIR || skippableWorldGenBlocks.contains(BlockDataManager.INSTANCE.getNameForBlock(block));
    }

    public static Set<String> getUserDefinedTargetBlocks() {
        return worldGenTargetBlocks;
    }

    public static String getBiomeName(Biome biome) {
        if (biome == null) {
            return "null";
        }
        String biomeName;
        try {
            biomeName = (String) BIOME_NAME_FIELD.get(biome);
        }
        catch (IllegalAccessException e) {
            AncientWarfareCore.log.error(e);
            biomeName = null;
        }
        return biomeName == null ? "null" : biomeName.toLowerCase(Locale.ENGLISH);
    }

    public static boolean shouldSkipScan(Block block) {
        return scannerSkippedBlocks.contains(Block.REGISTRY.getNameForObject(block).toString());
    } //TODO are there blocks that would have registry name set to null (C&B perhaps)?

    public static boolean withinProtectionRange(double dist) {
        return dist < spawnProtectionRange * spawnProtectionRange * 16 * 16;
    }

    public void loadPostInitValues() {
        if (exportBlockNames) {
            config.get(serverOptions, "export_block_name_list", false).set(false);
            exportBlockNames = false;
            doBlockNameDump();
        }
    }

    private void doBlockNameDump() {
        File file = new File(configPathForFiles);
        file.mkdirs();
        file = new File(file, "block_names.txt");
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            for (Object key : Block.REGISTRY.getKeys()) {
                fw.write(String.valueOf(key) + "\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (fw != null) {
                try {
                    fw.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
