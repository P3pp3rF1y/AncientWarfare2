package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins;

import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.shadowmage.ancientwarfare.core.init.AWCoreBlocks;
import net.shadowmage.ancientwarfare.structure.api.StructureContentPlugin;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import net.shadowmage.ancientwarfare.structure.template.StructurePluginManager;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleBanner;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleBed;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleBlockDoors;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleBlockInventory;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleBlockSign;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleBlockTile;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleCoffin;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleFlag;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleFlowerPot;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleFluid;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleShulkerBox;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleStructureBuilder;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleTotemPart;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleVanillaSkull;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleVanillaSpawner;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleVine;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.entityrules.TemplateRuleGates;

public class StructurePluginVanillaHandler implements StructureContentPlugin {
	@Override
	public void addHandledBlocks(StructurePluginManager manager) {
		manager.registerBlockHandler(TemplateRuleBlockDoors.PLUGIN_NAME, Blocks.IRON_DOOR, TemplateRuleBlockDoors::new, TemplateRuleBlockDoors::new);
		manager.registerBlockHandler(TemplateRuleBlockDoors.PLUGIN_NAME, Blocks.SPRUCE_DOOR, TemplateRuleBlockDoors::new, TemplateRuleBlockDoors::new);
		manager.registerBlockHandler(TemplateRuleBlockDoors.PLUGIN_NAME, Blocks.OAK_DOOR, TemplateRuleBlockDoors::new, TemplateRuleBlockDoors::new);
		manager.registerBlockHandler(TemplateRuleBlockDoors.PLUGIN_NAME, Blocks.JUNGLE_DOOR, TemplateRuleBlockDoors::new, TemplateRuleBlockDoors::new);
		manager.registerBlockHandler(TemplateRuleBlockDoors.PLUGIN_NAME, Blocks.BIRCH_DOOR, TemplateRuleBlockDoors::new, TemplateRuleBlockDoors::new);
		manager.registerBlockHandler(TemplateRuleBlockDoors.PLUGIN_NAME, Blocks.ACACIA_DOOR, TemplateRuleBlockDoors::new, TemplateRuleBlockDoors::new);
		manager.registerBlockHandler(TemplateRuleBlockDoors.PLUGIN_NAME, Blocks.DARK_OAK_DOOR, TemplateRuleBlockDoors::new, TemplateRuleBlockDoors::new);
		manager.registerBlockHandler(TemplateRuleVine.PLUGIN_NAME, Blocks.VINE, TemplateRuleVine::new, TemplateRuleVine::new);
		manager.registerBlockHandler(TemplateRuleBlockSign.PLUGIN_NAME, Blocks.WALL_SIGN, TemplateRuleBlockSign::new, TemplateRuleBlockSign::new);
		manager.registerBlockHandler(TemplateRuleBlockSign.PLUGIN_NAME, Blocks.STANDING_SIGN, TemplateRuleBlockSign::new, TemplateRuleBlockSign::new);
		manager.registerBlockHandler(TemplateRuleVanillaSpawner.PLUGIN_NAME, Blocks.MOB_SPAWNER, TemplateRuleVanillaSpawner::new, TemplateRuleVanillaSpawner::new);
		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, Blocks.COMMAND_BLOCK, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, Blocks.BEACON, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, Blocks.COMMAND_BLOCK, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, Blocks.CHAIN_COMMAND_BLOCK, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, Blocks.REPEATING_COMMAND_BLOCK, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
		manager.registerBlockHandler(TemplateRuleBlockInventory.PLUGIN_NAME, Blocks.DISPENSER,
				(world, pos, state, turns) -> new TemplateRuleBlockInventory(world, pos, state, turns, new EnumFacing[] {null}, true),
				TemplateRuleBlockInventory::new);
		manager.registerBlockHandler(TemplateRuleBlockInventory.PLUGIN_NAME, Blocks.CHEST,
				(world, pos, state, turns) -> new TemplateRuleBlockInventory(world, pos, state, turns, new EnumFacing[] {null}, true),
				TemplateRuleBlockInventory::new);
		manager.registerBlockHandler(TemplateRuleBlockInventory.PLUGIN_NAME, Blocks.DROPPER,
				(world, pos, state, turns) -> new TemplateRuleBlockInventory(world, pos, state, turns, new EnumFacing[] {null}, true),
				TemplateRuleBlockInventory::new);
		manager.registerBlockHandler(TemplateRuleBlockInventory.PLUGIN_NAME, Blocks.HOPPER,
				(world, pos, state, turns) -> new TemplateRuleBlockInventory(world, pos, state, turns, new EnumFacing[] {null}, true),
				TemplateRuleBlockInventory::new);
		manager.registerBlockHandler(TemplateRuleBlockInventory.PLUGIN_NAME, Blocks.TRAPPED_CHEST,
				(world, pos, state, turns) -> new TemplateRuleBlockInventory(world, pos, state, turns, new EnumFacing[] {null}, true),
				TemplateRuleBlockInventory::new);
		manager.registerBlockHandler(TemplateRuleFlowerPot.PLUGIN_NAME, Blocks.FLOWER_POT, TemplateRuleFlowerPot::new, TemplateRuleFlowerPot::new);
		manager.registerBlockHandler(TemplateRuleBed.PLUGIN_NAME, Blocks.BED, TemplateRuleBed::new, TemplateRuleBed::new);
		manager.registerBlockHandler(TemplateRuleVanillaSkull.PLUGIN_NAME, Blocks.SKULL, TemplateRuleVanillaSkull::new, TemplateRuleVanillaSkull::new);
		manager.registerBlockHandler(TemplateRuleFluid.PLUGIN_NAME, Blocks.WATER, TemplateRuleFluid::new, TemplateRuleFluid::new);
		manager.registerBlockHandler(TemplateRuleFluid.PLUGIN_NAME, Blocks.LAVA, TemplateRuleFluid::new, TemplateRuleFluid::new);
		manager.registerBlockHandler(TemplateRuleBanner.PLUGIN_NAME, Blocks.STANDING_BANNER, TemplateRuleBanner::new, TemplateRuleBanner::new);
		manager.registerBlockHandler(TemplateRuleBanner.PLUGIN_NAME, Blocks.WALL_BANNER, TemplateRuleBanner::new, TemplateRuleBanner::new);
		manager.registerBlockHandler(TemplateRuleShulkerBox.PLUGIN_NAME, Blocks.BLACK_SHULKER_BOX, TemplateRuleShulkerBox::new, TemplateRuleShulkerBox::new);
		manager.registerBlockHandler(TemplateRuleShulkerBox.PLUGIN_NAME, Blocks.BLUE_SHULKER_BOX, TemplateRuleShulkerBox::new, TemplateRuleShulkerBox::new);
		manager.registerBlockHandler(TemplateRuleShulkerBox.PLUGIN_NAME, Blocks.BROWN_SHULKER_BOX, TemplateRuleShulkerBox::new, TemplateRuleShulkerBox::new);
		manager.registerBlockHandler(TemplateRuleShulkerBox.PLUGIN_NAME, Blocks.SILVER_SHULKER_BOX, TemplateRuleShulkerBox::new, TemplateRuleShulkerBox::new);
		manager.registerBlockHandler(TemplateRuleShulkerBox.PLUGIN_NAME, Blocks.CYAN_SHULKER_BOX, TemplateRuleShulkerBox::new, TemplateRuleShulkerBox::new);
		manager.registerBlockHandler(TemplateRuleShulkerBox.PLUGIN_NAME, Blocks.GRAY_SHULKER_BOX, TemplateRuleShulkerBox::new, TemplateRuleShulkerBox::new);
		manager.registerBlockHandler(TemplateRuleShulkerBox.PLUGIN_NAME, Blocks.GREEN_SHULKER_BOX, TemplateRuleShulkerBox::new, TemplateRuleShulkerBox::new);
		manager.registerBlockHandler(TemplateRuleShulkerBox.PLUGIN_NAME, Blocks.LIGHT_BLUE_SHULKER_BOX, TemplateRuleShulkerBox::new, TemplateRuleShulkerBox::new);
		manager.registerBlockHandler(TemplateRuleShulkerBox.PLUGIN_NAME, Blocks.LIME_SHULKER_BOX, TemplateRuleShulkerBox::new, TemplateRuleShulkerBox::new);
		manager.registerBlockHandler(TemplateRuleShulkerBox.PLUGIN_NAME, Blocks.MAGENTA_SHULKER_BOX, TemplateRuleShulkerBox::new, TemplateRuleShulkerBox::new);
		manager.registerBlockHandler(TemplateRuleShulkerBox.PLUGIN_NAME, Blocks.ORANGE_SHULKER_BOX, TemplateRuleShulkerBox::new, TemplateRuleShulkerBox::new);
		manager.registerBlockHandler(TemplateRuleShulkerBox.PLUGIN_NAME, Blocks.PINK_SHULKER_BOX, TemplateRuleShulkerBox::new, TemplateRuleShulkerBox::new);
		manager.registerBlockHandler(TemplateRuleShulkerBox.PLUGIN_NAME, Blocks.PURPLE_SHULKER_BOX, TemplateRuleShulkerBox::new, TemplateRuleShulkerBox::new);
		manager.registerBlockHandler(TemplateRuleShulkerBox.PLUGIN_NAME, Blocks.RED_SHULKER_BOX, TemplateRuleShulkerBox::new, TemplateRuleShulkerBox::new);
		manager.registerBlockHandler(TemplateRuleShulkerBox.PLUGIN_NAME, Blocks.WHITE_SHULKER_BOX, TemplateRuleShulkerBox::new, TemplateRuleShulkerBox::new);
		manager.registerBlockHandler(TemplateRuleShulkerBox.PLUGIN_NAME, Blocks.YELLOW_SHULKER_BOX, TemplateRuleShulkerBox::new, TemplateRuleShulkerBox::new);

		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, AWStructureBlocks.ADVANCED_SPAWNER, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
		manager.registerBlockHandler(TemplateRuleTotemPart.PLUGIN_NAME, AWStructureBlocks.TOTEM_PART, TemplateRuleTotemPart::new, TemplateRuleTotemPart::new);
		manager.registerBlockHandler(TemplateRuleCoffin.PLUGIN_NAME, AWStructureBlocks.WOODEN_COFFIN, TemplateRuleCoffin::new, TemplateRuleCoffin::new);
		manager.registerBlockHandler(TemplateRuleCoffin.PLUGIN_NAME, AWStructureBlocks.STONE_COFFIN, TemplateRuleCoffin::new, TemplateRuleCoffin::new);
		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, AWCoreBlocks.ENGINEERING_STATION, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, AWCoreBlocks.RESEARCH_STATION, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, AWStructureBlocks.DRAFTING_STATION, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
		manager.registerBlockHandler(TemplateRuleStructureBuilder.PLUGIN_NAME, AWStructureBlocks.STRUCTURE_BUILDER_TICKED, TemplateRuleStructureBuilder::new, TemplateRuleStructureBuilder::new);
		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, AWStructureBlocks.SOUND_BLOCK, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, AWStructureBlocks.ADVANCED_LOOT_CHEST, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
		manager.registerBlockHandler(TemplateRuleFlag.PLUGIN_NAME, AWStructureBlocks.PROTECTION_FLAG, TemplateRuleFlag::new, TemplateRuleFlag::new);
		manager.registerBlockHandler(TemplateRuleFlag.PLUGIN_NAME, AWStructureBlocks.DECORATIVE_FLAG, TemplateRuleFlag::new, TemplateRuleFlag::new);
	}

	@Override
	public void addHandledEntities(StructurePluginManager manager) {
		manager.registerEntityHandler(TemplateRuleGates.PLUGIN_NAME, EntityGate.class, TemplateRuleGates::new, TemplateRuleGates::new);
	}
}
