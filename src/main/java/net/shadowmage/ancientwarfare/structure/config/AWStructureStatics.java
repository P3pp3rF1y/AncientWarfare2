package net.shadowmage.ancientwarfare.structure.config;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.shadowmage.ancientwarfare.core.config.ModConfiguration;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class AWStructureStatics extends ModConfiguration {

	public static Set<String> lootTableExclusions;

	public AWStructureStatics(String mod) {
		super(mod);
	}

	public static boolean processScannerCommands = false;
	public static String templateExtension = "aws";
	public static String townTemplateExtension = "awt";
	public static boolean enableWorldGen = true;
	public static boolean enableTownGeneration = true;
	public static boolean enableStructureGeneration = true;
	public static boolean loadDefaultPack = true;
	public static int clusterValueSearchRange = 16;
	public static int duplicateStructureSearchRange = 40;
	public static int maxClusterValue = 500;
	public static float randomGenerationChance = 0.075f;
	public static int spawnProtectionRange = 12;
	public static int townClosestDistance = 40;
	public static float townGenerationChance = 0.125f;
	public static Set<String> excludedSpawnerEntities = new HashSet<>();
	private static HashSet<String> skippableWorldGenBlocks = new HashSet<>();
	private static HashSet<String> worldGenTargetBlocks = new HashSet<>();
	private static HashSet<String> scannerSkippedBlocks = new HashSet<>();

	private static final String worldGenCategory = "a_world-gen_settings";
	private static final String villageGenCategory = "b_village-gen_settings";
	private static final String excludedEntitiesCategory = "c_excluded_spawner_entities";
	private static final String worldGenBlocks = "d_world_gen_skippable_blocks";
	private static final String targetBlocks = "e_world_gen_target_blocks";
	private static final String scanSkippedBlocks = "f_scanner_skipped_blocks";
	private static final String excludedLootTables = "g_excluded_loot_tables";


	@Override
	public void initializeCategories() {
		this.config.addCustomCategoryComment(worldGenCategory, "Settings that effect all world-structure-generation.");
		this.config.addCustomCategoryComment(villageGenCategory, "Settings that effect the generation of vanilla villages.\nCurrently there are no village-generation options, and no structures will generate in villages.");
		this.config.addCustomCategoryComment(excludedEntitiesCategory, "Entities that will not show up in the Mob Spawner Placer entity selection list.\nAdd any mobs here that will crash if spawned via the vanilla mob-spawner (usually complex NBT-defined entities).");
		this.config.addCustomCategoryComment(worldGenBlocks, "Blocks that should be skipped/ignored during world gen -- should list all plant blocks/logs/foliage");
		this.config.addCustomCategoryComment(targetBlocks, "List of target blocks that structures and towns can spawn on in addition to materials that are automatically whitelisted.");
		this.config.addCustomCategoryComment(scanSkippedBlocks, "List of blocks that the structure scanner will completely ignore.\nWhenever these blocks are encountered the template will instead fill that block position with a hard-air rule.\nAdd any blocks to this list that may cause crashes when scanned or duplicated.\nVanilla blocks should not need to be added, but some mod-blocks may.\nBlock names must be specified by fully-qualified name (e.g. \"minecraft:chests/stronghold_corridor\")");
		this.config.addCustomCategoryComment(excludedLootTables, "List of loot tables that should be excluded from loot chest placer GUI.\nLoot table names must be specified by fully-qualified name (e.g. \"minecraft:stone\")");
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
		enableWorldGen = config.get(serverOptions, "enable_world_generation", enableWorldGen, "Default=" + enableWorldGen + "\n" + "Enable or disable world generation entirely. If disabled, all other options will have no effect.").getBoolean(enableWorldGen);

		townClosestDistance = config.get(worldGenCategory, "town_min_distance", townClosestDistance, "Default=" + townClosestDistance + "\n" + "Minimum distance between towns.  This should be set to a value quite a bit larger than the largest town" + "that you have configured for generation.  E.G.  Max town size=16, this value should be >= 40.").getInt(townClosestDistance);
		townGenerationChance = (float) config.get(worldGenCategory, "town_generation_chance", townGenerationChance, "Default=" + townGenerationChance + "\n" + "Accepts values between 0 and 1.0.  Decimal percent chance to -attempt- town generation for any given chunk.  Higher settings may result in" + "more towns being generated, but may come with a performance hit during new chunk generation.  Lower values WILL result in fewer towns, and" + "-may- improve performance during chunk generation.").getDouble(townGenerationChance);

		initializeDefaultSkippableBlocks();
		initializeDefaultSkippedEntities();
		initializeDefaultTargetBlocks();
		initializeScannerSkippedBlocks();

		lootTableExclusions = new HashSet<>(Arrays.asList(config.get(excludedLootTables, "excluded_loot_tables", new String[] {"primitivemobs:entities/chameleon",
				"minecraft:entities/ocelot",
				"minecraft:entities/silverfish",
				"minecraft:entities/llama",
				"twilightforest:entities/death_tome_hurt",
				"twilightforest:entities/yeti",
				"twilightforest:entities/mini_ghast",
				"twilightforest:entities/snow_guardian",
				"minecraft:entities/sheep/brown",
				"minecraft:entities/vex",
				"minecraft:entities/elder_guardian",
				"minecolonies:entityarcherbarbariandrops",
				"minecraft:entities/creeper",
				"primitivemobs:entities/mimic",
				"primitivemobs:entities/lily_lurker",
				"minecraft:entities/sheep/red",
				"twilightforest:entities/questing_ram_rewards",
				"twilightforest:entities/fire_beetle",
				"twilightforest:entities/redcap",
				"minecraft:entities/horse",
				"minecraft:entities/slime",
				"twilightforest:entities/bunny",
				"minecraft:entities/ghast",
				"minecraft:entities/zombie",
				"exoticbirds:birds/woodpecker",
				"minecraft:entities/sheep/silver",
				"twilightforest:entities/ice_crystal",
				"minecraft:entities/mushroom_cow",
				"minecolonies:entitychiefbarbariandrops",
				"exoticbirds:birds/toucan",
				"minecraft:entities/sheep/white",
				"exoticbirds:birds/hummingbird",
				"exoticbirds:birds/bluejay",
				"exoticbirds:birds/roadrunner",
				"minecraft:entities/sheep/cyan",
				"minecraft:entities/zombie_pigman",
				"minecraft:entities/cow",
				"minecraft:entities/pig",
				"exoticbirds:birds/heron",
				"exoticbirds:birds/peahen",
				"exoticbirds:birds/peacock",
				"exoticbirds:birds/cardinal",
				"exoticbirds:birds/duck",
				"exoticbirds:birds/ostrich",
				"minecraft:entities/stray",
				"exoticbirds:birds/gouldianfinch",
				"minecraft:entities/sheep/magenta",
				"exoticbirds:birds/cassowary",
				"primitivemobs:entities/blazing_juggernaut",
				"minecraft:gameplay/fishing/fish",
				"twilightforest:entities/winter_wolf",
				"minecraft:gameplay/fishing/junk",
				"minecraft:entities/donkey",
				"twilightforest:entities/tower_termite",
				"twilightforest:entities/goblin_knight",
				"exoticbirds:birds/kiwi",
				"ebwizardry:entities/evil_wizard",
				"exoticbirds:dungeon_phoenix",
				"minecraft:entities/villager",
				"minecraft:entities/spider",
				"twilightforest:entities/ice_exploder",
				"exoticbirds:birds/seagull",
				"minecraft:entities/zombie_horse",
				"minecraft:entities/sheep/yellow",
				"minecraft:entities/sheep/black",
				"primitivemobs:chests/mimic_trap",
				"twilightforest:entities/hydra",
				"exoticbirds:birds/pigeon",
				"twilightforest:entities/minoshroom",
				"minecraft:entities/magma_cube",
				"minecraft:entities/sheep/pink",
				"twilightforest:entities/death_tome",
				"minecraft:entities/mule",
				"familiarfauna:entities/butterfly",
				"twilightforest:entities/armored_giant",
				"minecraft:entities/endermite",
				"minecraft:entities/bat",
				"minecraft:gameplay/fishing",
				"familiarfauna:entities/turkey",
				"silentgems:ender_slime",
				"primitivemobs:entities/festive_creeper",
				"exoticbirds:birds/kingfisher",
				"minecraft:entities/evocation_illager",
				"primitivemobs:entities/special/filch_lizard_steal",
				"minecraft:entities/witch",
				"minecraft:entities/skeleton_horse",
				"minecraft:entities/zombie_villager",
				"twilightforest:entities/ice_shooter",
				"twilightforest:entities/block_goblin",
				"exoticbirds:birds/kookaburra",
				"minecraft:entities/guardian",
				"primitivemobs:entities/special/haunted_tool",
				"twilightforest:entities/wraith",
				"familiarfauna:entities/snail",
				"minecraft:entities/blaze",
				"minecraft:entities/sheep",
				"minecraft:entities/sheep/orange",
				"exoticbirds:birds/owl",
				"twilightforest:entities/snow_queen",
				"twilightforest:entities/minotaur",
				"twilightforest:entities/yeti_alpha",
				"exoticbirds:birds/crane",
				"minecraft:entities/sheep/lime",
				"twilightforest:entities/naga",
				"exoticbirds:birds/swan",
				"minecraft:empty",
				"exoticbirds:birds/parrot",
				"exoticbirds:birds/flamingo",
				"primitivemobs:entities/rocket_creeper",
				"familiarfauna:entities/deer",
				"twilightforest:entities/raven",
				"minecraft:entities/chicken",
				"twilightforest:entities/giant_miner",
				"minecolonies:entitybarbariandrops",
				"exoticbirds:birds/vulture",
				"twilightforest:entities/skeleton_druid",
				"exoticbirds:birds/penguin",
				"familiarfauna:entities/pixie",
				"minecraft:entities/sheep/light_blue",
				"minecraft:entities/wolf",
				"minecraft:entities/skeleton",
				"primitivemobs:entities/support_creeper",
				"minecraft:entities/enderman",
				"primitivemobs:entities/dodo",
				"minecraft:entities/squid",
				"minecraft:entities/shulker",
				"twilightforest:entities/deer",
				"twilightforest:entities/troll",
				"exoticbirds:birds/pelican",
				"minecraft:entities/cave_spider",
				"minecraft:entities/sheep/green",
				"twilightforest:entities/tower_golem",
				"minecraft:entities/wither_skeleton",
				"minecraft:entities/snowman",
				"minecraft:entities/sheep/blue",
				"familiarfauna:entities/dragonfly",
				"minecraft:entities/rabbit",
				"twilightforest:entities/helmet_crab",
				"twilightforest:entities/bird",
				"twilightforest:entities/slime_beetle",
				"twilightforest:entities/maze_slime",
				"minecraft:entities/polar_bear",
				"minecraft:entities/ender_dragon",
				"twilightforest:entities/kobold",
				"exoticbirds:birds/phoenix",
				"minecraft:entities/parrot",
				"minecraft:entities/sheep/gray",
				"twilightforest:entities/lich",
				"exoticbirds:birds/lyrebird",
				"exoticbirds:birds/booby",
				"minecraft:entities/vindication_illager",
				"exoticbirds:birds/magpie",
				"minecraft:entities/husk",
				"minecraft:entities/sheep/purple",
				"minecraft:entities/giant",
				"primitivemobs:entities/special/filch_lizard_spawn",
				"minecraft:entities/iron_golem"
		}).getStringList()));
	}

	private void initializeScannerSkippedBlocks() {
		String[] defaultSkippableBlocks = new String[] {"AncientWarfareStructure:gate_proxy",
				//skip gate proxy blocks by default... possibly some others that need skipping as well
		};
		defaultSkippableBlocks = config.getStringList("scanner_skipped_blocks", scanSkippedBlocks, defaultSkippableBlocks, "Blocks TO be skipped by structure scanner");
		Collections.addAll(scannerSkippedBlocks, defaultSkippableBlocks);
	}

	private void initializeDefaultSkippableBlocks() {
		String[] defaultSkippableBlocks = new String[] {"betterunderground:blockdecorations",
				"betterunderground:blockfossils",
				"betterunderground:blockfossils",
				"betterunderground:blocksandstonestalactite",
				"betterunderground:blocksandstonestalactite",
				"betterunderground:blockstonestalactite",
				"betterunderground:blockstonestalactite",
				"betterunderground:mossydirt",
				"betterunderground:mossydirt",
				"biomesoplenty:coral",
				"biomesoplenty:gem_block",
				"biomesoplenty:gem_ore",
				"biomesoplenty:hot_spring_water",
				"biomesoplenty:seaweed",
				"chancecubes:chance_cube",
				"coralreef:coral",
				"coralreef:reef",
				"dungeonmobs:bladetrap",
				"dungeontactics:ore_enddiamond",
				"dungeontactics:ore_endlapis",
				"dungeontactics:ore_stonequartz",
				"ebwizardry:crystal_ore",
				"ebwizardry:petrified_stone",
				"floricraft:leaves_christmas",
				"floricraft:leaves_christmas_dynamic",
				"floricraft:leaves_christmas_dynamic_unlit",
				"floricraft:leaves_christmas_unlit",
				"floricraft:leaves_floric_type0",
				"floricraft:ornament_christmas",
				"floricraft:sapling_christmas",
				"floricraft:sapling_floric_type0",
				"forestry:apiary",
				"forestry:leaves",
				"forestry:leaves.decorative.0",
				"forestry:leaves.decorative.1",
				"forestry:leaves.decorative.2",
				"forestry:leaves.default.0",
				"forestry:leaves.default.1",
				"forestry:leaves.default.2",
				"forestry:leaves.default.3",
				"forestry:leaves.default.4",
				"forestry:leaves.default.5",
				"forestry:leaves.default.6",
				"forestry:leaves.default.7",
				"forestry:leaves.default.8",
				"forestry:logs.0",
				"forestry:logs.1",
				"forestry:logs.2",
				"forestry:logs.3",
				"forestry:logs.4",
				"forestry:logs.5",
				"forestry:logs.6",
				"forestry:logs.7",
				"forestry:mushroom",
				"forestry:stump",
				"gravestone:gsgravestone",
				"gravestone-extended:gstrap",
				"grimoireofgaia:web_temp",
				"harvestcraft:aridgarden",
				"harvestcraft:frostgarden",
				"harvestcraft:shadedgarden",
				"harvestcraft:soggygarden",
				"harvestcraft:tropicalgarden",
				"harvestcraft:windygarden",
				"iceandfire:fire_lily",
				"iceandfire:frost_lily",
				"minecraft:lava",
				"minecraft:snow_layer",
				"minecraft:sponge",
				"minecraft:web",
				"mocreatures:mocleaves",
				"mocreatures:moclog",
				"mocreatures:moctallgrass",
				"silentgems:chaosnode",
				"silentgems:essenceore",
				"silentgems:gemblockdark",
				"silentgems:gemglassdark",
				"silentgems:gemlamp",
				"silentgems:gemore",
				"silentgems:gemoredark",
				"silentgems:gemorelight",
				"silentgems:multi_gem_ore_classic",
				"silentgems:multi_gem_ore_dark",
				"silentgems:multi_gem_ore_light",
				"silentgems:teleporter",
				"wizardry:torikki_grass",
				"wizardry:wisdom_leaves",
				"wizardry:wisdom_wood_log",
				"zawa:arctic_moss",
				"zawa:bamboo",
				"zawa:beeplant",
				"zawa:bellflowers",
				"zawa:bladderwrack",
				"zawa:brain_coral",
				"zawa:bromeliad",
				"zawa:cinnamon_fern",
				"zawa:elegance_coral",
				"zawa:fan_palm_bottom",
				"zawa:fan_palm_top",
				"zawa:fire_coral",
				"zawa:golden_barrel_cactus",
				"zawa:gracilaria",
				"zawa:kelp",
				"zawa:large_sea_anemone",
				"zawa:large_sea_urchin",
				"zawa:lettuce_coral",
				"zawa:mixed_coral_plates",
				"zawa:mixed_coral_rocks",
				"zawa:mixed_stone",
				"zawa:mixed_stone_mossy",
				"zawa:ostritch_fern",
				"zawa:peach_flower",
				"zawa:peach_flower",
				"zawa:river_stone",
				"zawa:sea_grass",
				"zawa:sea_urchin_tropical",
				"zawa:small_sea_anemone",
				"zawa:small_sea_urchin",
				"zawa:staghorn_coral",
				"zawa:stonecrop",
				"zawa:wild_celery",
				"zoocraftdiscoveries:arcticpine_log",
				"zoocraftdiscoveries:cookie_bush",
				"zoocraftdiscoveries:herbs_basil",
				"zoocraftdiscoveries:herbs_bay",
				"zoocraftdiscoveries:herbs_chive",
				"zoocraftdiscoveries:herbs_coriander",
				"zoocraftdiscoveries:herbs_dill",
				"zoocraftdiscoveries:herbs_lemongrass",
				"zoocraftdiscoveries:herbs_mint",
				"zoocraftdiscoveries:herbs_oregano",
				"zoocraftdiscoveries:herbs_parsley",
				"zoocraftdiscoveries:herbs_rosemary",
				"zoocraftdiscoveries:herbs_saffron",
				"zoocraftdiscoveries:herbs_sage",
				"zoocraftdiscoveries:herbs_tarragon",
				"zoocraftdiscoveries:herbs_thyme"};

		defaultSkippableBlocks = config.get(worldGenBlocks, "skippable_blocks", defaultSkippableBlocks).getStringList();
		Collections.addAll(skippableWorldGenBlocks, defaultSkippableBlocks);
	}

	public static void logSkippableBlocksCoveredByMaterial() {
		skippableWorldGenBlocks.stream().filter(b -> getBlock(b).isPresent() && isSkippableMaterial(getBlock(b).get().getDefaultState().getMaterial()))
				.forEach(b -> AncientWarfareStructure.LOG.info("Block {} defined as skippable is redundant as its material is already skipped by default", b));
	}

	private static Optional<Block> getBlock(String registryName) {
		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(registryName));
		return block == Blocks.AIR ? Optional.empty() : Optional.ofNullable(block);
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

	private void initializeDefaultTargetBlocks() {
		String[] defaultTargetBlocks = new String[] {"minecraft:snow",
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
		defaultTargetBlocks = config.get("target_blocks", targetBlocks, defaultTargetBlocks, "List of blocks that are valid").getStringList();
		Collections.addAll(worldGenTargetBlocks, defaultTargetBlocks);
	}

	public static boolean isValidTargetBlock(IBlockState state) {
		//noinspection ConstantConditions
		return isValidTargetMaterial(state.getMaterial()) || worldGenTargetBlocks.contains(state.getBlock().getRegistryName().toString());
	}

	public static boolean isValidTargetMaterial(Material material) {
		return material == Material.GRASS || material == Material.GROUND || material == Material.ROCK || material == Material.SNOW || material == Material.ICE
				|| material == Material.PACKED_ICE || material == Material.SAND || material == Material.WATER;
	}

	public static boolean isSkippable(IBlockState state) {
		//noinspection ConstantConditions
		return isSkippableMaterial(state.getMaterial()) || skippableWorldGenBlocks.contains(state.getBlock().getRegistryName().toString());
	}

	public static boolean isSkippableMaterial(Material material) {
		return material == Material.AIR || material == Material.PLANTS || material == Material.VINE || material == Material.LEAVES || material == Material.WOOD
				|| material == Material.GOURD || material == Material.CACTUS;
	}

	public static boolean shouldSkipScan(Block block) {
		return scannerSkippedBlocks.contains(Block.REGISTRY.getNameForObject(block).toString());
	} //TODO are there blocks that would have registry name set to null (C&B perhaps)?

	public static boolean withinProtectionRange(double dist) {
		return dist < spawnProtectionRange * spawnProtectionRange * 16 * 16;
	}
}
