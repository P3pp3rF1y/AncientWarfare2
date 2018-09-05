package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins;

import net.minecraft.block.Block;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Blocks;
import net.shadowmage.ancientwarfare.core.init.AWCoreBlocks;
import net.shadowmage.ancientwarfare.structure.api.StructureContentPlugin;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import net.shadowmage.ancientwarfare.structure.template.StructurePluginManager;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleBlockDoors;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleBlockInventory;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleBlockSign;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleBlockTile;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleFlowerPot;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleVanillaBlocks;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleVanillaSkull;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.entityrules.TemplateRuleEntityHanging;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.entityrules.TemplateRuleEntityLogic;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.entityrules.TemplateRuleGates;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.entityrules.TemplateRuleVanillaEntity;

import java.util.HashSet;

public class StructurePluginVanillaHandler implements StructureContentPlugin {
	@Override
	public void addHandledBlocks(StructurePluginManager manager) {
		HashSet<Block> specialHandledBlocks = new HashSet<>();
		specialHandledBlocks.add(Blocks.IRON_DOOR);
		specialHandledBlocks.add(Blocks.SPRUCE_DOOR);
		specialHandledBlocks.add(Blocks.OAK_DOOR);
		specialHandledBlocks.add(Blocks.JUNGLE_DOOR);
		specialHandledBlocks.add(Blocks.BIRCH_DOOR);
		specialHandledBlocks.add(Blocks.ACACIA_DOOR);
		specialHandledBlocks.add(Blocks.DARK_OAK_DOOR);
		specialHandledBlocks.add(Blocks.STANDING_SIGN);
		specialHandledBlocks.add(Blocks.WALL_SIGN);
		specialHandledBlocks.add(Blocks.MOB_SPAWNER);
		specialHandledBlocks.add(Blocks.COMMAND_BLOCK);
		specialHandledBlocks.add(Blocks.BREWING_STAND);
		specialHandledBlocks.add(Blocks.BEACON);
		specialHandledBlocks.add(Blocks.DISPENSER);
		specialHandledBlocks.add(Blocks.LIT_FURNACE);
		specialHandledBlocks.add(Blocks.FURNACE);
		specialHandledBlocks.add(Blocks.CHEST);
		specialHandledBlocks.add(Blocks.DROPPER);
		specialHandledBlocks.add(Blocks.HOPPER);
		specialHandledBlocks.add(Blocks.TRAPPED_CHEST);
		specialHandledBlocks.add(Blocks.FLOWER_POT);
		specialHandledBlocks.add(Blocks.SKULL);

		for (Block block : Block.REGISTRY) {
			//noinspection ConstantConditions
			if (block.getRegistryName().getResourceDomain().equals("minecraft") && !specialHandledBlocks.contains(block)) {
				manager.registerBlockHandler(TemplateRuleVanillaBlocks.PLUGIN_NAME, block, TemplateRuleVanillaBlocks::new, TemplateRuleVanillaBlocks::new);
			}
		}

		manager.registerBlockHandler(TemplateRuleBlockDoors.PLUGIN_NAME, Blocks.IRON_DOOR, TemplateRuleBlockDoors::new, TemplateRuleBlockDoors::new);
		manager.registerBlockHandler(TemplateRuleBlockDoors.PLUGIN_NAME, Blocks.SPRUCE_DOOR, TemplateRuleBlockDoors::new, TemplateRuleBlockDoors::new);
		manager.registerBlockHandler(TemplateRuleBlockDoors.PLUGIN_NAME, Blocks.OAK_DOOR, TemplateRuleBlockDoors::new, TemplateRuleBlockDoors::new);
		manager.registerBlockHandler(TemplateRuleBlockDoors.PLUGIN_NAME, Blocks.JUNGLE_DOOR, TemplateRuleBlockDoors::new, TemplateRuleBlockDoors::new);
		manager.registerBlockHandler(TemplateRuleBlockDoors.PLUGIN_NAME, Blocks.BIRCH_DOOR, TemplateRuleBlockDoors::new, TemplateRuleBlockDoors::new);
		manager.registerBlockHandler(TemplateRuleBlockDoors.PLUGIN_NAME, Blocks.ACACIA_DOOR, TemplateRuleBlockDoors::new, TemplateRuleBlockDoors::new);
		manager.registerBlockHandler(TemplateRuleBlockDoors.PLUGIN_NAME, Blocks.DARK_OAK_DOOR, TemplateRuleBlockDoors::new, TemplateRuleBlockDoors::new);
		manager.registerBlockHandler(TemplateRuleBlockSign.PLUGIN_NAME, Blocks.WALL_SIGN, TemplateRuleBlockSign::new, TemplateRuleBlockSign::new);
		manager.registerBlockHandler(TemplateRuleBlockSign.PLUGIN_NAME, Blocks.STANDING_SIGN, TemplateRuleBlockSign::new, TemplateRuleBlockSign::new);
		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, Blocks.MOB_SPAWNER, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, Blocks.COMMAND_BLOCK, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, Blocks.BREWING_STAND, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, Blocks.BEACON, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, Blocks.LIT_FURNACE, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, Blocks.FURNACE, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, Blocks.BEACON, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
		manager.registerBlockHandler(TemplateRuleBlockInventory.PLUGIN_NAME, Blocks.DISPENSER, TemplateRuleBlockInventory::new, TemplateRuleBlockInventory::new);
		manager.registerBlockHandler(TemplateRuleBlockInventory.PLUGIN_NAME, Blocks.CHEST, TemplateRuleBlockInventory::new, TemplateRuleBlockInventory::new);
		manager.registerBlockHandler(TemplateRuleBlockInventory.PLUGIN_NAME, Blocks.DROPPER, TemplateRuleBlockInventory::new, TemplateRuleBlockInventory::new);
		manager.registerBlockHandler(TemplateRuleBlockInventory.PLUGIN_NAME, Blocks.HOPPER, TemplateRuleBlockInventory::new, TemplateRuleBlockInventory::new);
		manager.registerBlockHandler(TemplateRuleBlockInventory.PLUGIN_NAME, Blocks.TRAPPED_CHEST, TemplateRuleBlockInventory::new, TemplateRuleBlockInventory::new);
		manager.registerBlockHandler(TemplateRuleFlowerPot.PLUGIN_NAME, Blocks.FLOWER_POT, TemplateRuleFlowerPot::new, TemplateRuleFlowerPot::new);
		manager.registerBlockHandler(TemplateRuleVanillaSkull.PLUGIN_NAME, Blocks.SKULL, TemplateRuleVanillaSkull::new, TemplateRuleVanillaSkull::new);

		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, AWStructureBlocks.ADVANCED_SPAWNER, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, AWCoreBlocks.ENGINEERING_STATION, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, AWCoreBlocks.RESEARCH_STATION, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, AWStructureBlocks.DRAFTING_STATION, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, AWStructureBlocks.STRUCTURE_BUILDER_TICKED, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, AWStructureBlocks.SOUND_BLOCK, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, AWStructureBlocks.ADVANCED_LOOT_CHEST, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
	}

	@Override
	public void addHandledEntities(StructurePluginManager manager) {
		manager.registerEntityHandler(TemplateRuleVanillaEntity.PLUGIN_NAME, EntityPig.class, TemplateRuleVanillaEntity::new, TemplateRuleVanillaEntity::new);
		manager.registerEntityHandler(TemplateRuleVanillaEntity.PLUGIN_NAME, EntitySheep.class, TemplateRuleVanillaEntity::new, TemplateRuleVanillaEntity::new);
		manager.registerEntityHandler(TemplateRuleVanillaEntity.PLUGIN_NAME, EntityCow.class, TemplateRuleVanillaEntity::new, TemplateRuleVanillaEntity::new);
		manager.registerEntityHandler(TemplateRuleVanillaEntity.PLUGIN_NAME, EntityChicken.class, TemplateRuleVanillaEntity::new, TemplateRuleVanillaEntity::new);
		manager.registerEntityHandler(TemplateRuleVanillaEntity.PLUGIN_NAME, EntityBoat.class, TemplateRuleVanillaEntity::new, TemplateRuleVanillaEntity::new);
		manager.registerEntityHandler(TemplateRuleVanillaEntity.PLUGIN_NAME, EntityIronGolem.class, TemplateRuleVanillaEntity::new, TemplateRuleVanillaEntity::new);
		manager.registerEntityHandler(TemplateRuleVanillaEntity.PLUGIN_NAME, EntityWolf.class, TemplateRuleVanillaEntity::new, TemplateRuleVanillaEntity::new);
		manager.registerEntityHandler(TemplateRuleVanillaEntity.PLUGIN_NAME, EntityOcelot.class, TemplateRuleVanillaEntity::new, TemplateRuleVanillaEntity::new);
		manager.registerEntityHandler(TemplateRuleVanillaEntity.PLUGIN_NAME, EntityWither.class, TemplateRuleVanillaEntity::new, TemplateRuleVanillaEntity::new);
		manager.registerEntityHandler(TemplateRuleVanillaEntity.PLUGIN_NAME, EntitySnowman.class, TemplateRuleVanillaEntity::new, TemplateRuleVanillaEntity::new);

		manager.registerEntityHandler(TemplateRuleEntityHanging.PLUGIN_NAME, EntityPainting.class, TemplateRuleEntityHanging::new, TemplateRuleEntityHanging::new);
		manager.registerEntityHandler(TemplateRuleEntityHanging.PLUGIN_NAME, EntityItemFrame.class, TemplateRuleEntityHanging::new, TemplateRuleEntityHanging::new);

		manager.registerEntityHandler(TemplateRuleEntityLogic.PLUGIN_NAME, EntityHorse.class, TemplateRuleEntityLogic::new, TemplateRuleEntityLogic::new);
		manager.registerEntityHandler(TemplateRuleEntityLogic.PLUGIN_NAME, EntityVillager.class, TemplateRuleEntityLogic::new, TemplateRuleEntityLogic::new);
		manager.registerEntityHandler(TemplateRuleEntityLogic.PLUGIN_NAME, EntityMinecartHopper.class, TemplateRuleEntityLogic::new, TemplateRuleEntityLogic::new);
		manager.registerEntityHandler(TemplateRuleEntityLogic.PLUGIN_NAME, EntityMinecartChest.class, TemplateRuleEntityLogic::new, TemplateRuleEntityLogic::new);
		manager.registerEntityHandler(TemplateRuleEntityLogic.PLUGIN_NAME, EntityMinecartEmpty.class, TemplateRuleEntityLogic::new, TemplateRuleEntityLogic::new);
		manager.registerEntityHandler(TemplateRuleEntityLogic.PLUGIN_NAME, EntityMinecartFurnace.class, TemplateRuleEntityLogic::new, TemplateRuleEntityLogic::new);
		manager.registerEntityHandler(TemplateRuleEntityLogic.PLUGIN_NAME, EntityMinecartTNT.class, TemplateRuleEntityLogic::new, TemplateRuleEntityLogic::new);

		manager.registerEntityHandler(TemplateRuleGates.PLUGIN_NAME, EntityGate.class, TemplateRuleGates::new, TemplateRuleGates::new);
	}
}
