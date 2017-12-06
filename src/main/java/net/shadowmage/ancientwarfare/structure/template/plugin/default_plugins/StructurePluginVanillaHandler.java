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
package net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins;

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
import net.shadowmage.ancientwarfare.core.api.AWBlocks;
import net.shadowmage.ancientwarfare.structure.api.IStructurePluginManager;
import net.shadowmage.ancientwarfare.structure.api.StructureContentPlugin;
import net.shadowmage.ancientwarfare.structure.block.AWStructuresBlocks;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules.TemplateRuleBlockDoors;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules.TemplateRuleBlockInventory;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules.TemplateRuleBlockLogic;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules.TemplateRuleBlockSign;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules.TemplateRuleFlowerPot;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules.TemplateRuleVanillaBlocks;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.entity_rules.TemplateRuleEntityHanging;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.entity_rules.TemplateRuleEntityLogic;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.entity_rules.TemplateRuleGates;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.entity_rules.TemplateRuleVanillaEntity;

import java.util.HashSet;

public class StructurePluginVanillaHandler implements StructureContentPlugin {

    public StructurePluginVanillaHandler() {

    }

    @Override
    public void addHandledBlocks(IStructurePluginManager manager) {
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
        specialHandledBlocks.add(Blocks.BEACON);
        specialHandledBlocks.add(Blocks.TRAPPED_CHEST);
        specialHandledBlocks.add(Blocks.FLOWER_POT);
        specialHandledBlocks.add(Blocks.SKULL);

        for (Block block : Block.REGISTRY) {
            if (block != null && block.getRegistryName().getResourceDomain().equals("minecraft") && !specialHandledBlocks.contains(block)) {
                manager.registerBlockHandler("vanillaBlocks", block, TemplateRuleVanillaBlocks.class);
            }
        }

        manager.registerBlockHandler("vanillaDoors", Blocks.IRON_DOOR, TemplateRuleBlockDoors.class);
        manager.registerBlockHandler("vanillaDoors", Blocks.SPRUCE_DOOR, TemplateRuleBlockDoors.class);
        manager.registerBlockHandler("vanillaDoors", Blocks.OAK_DOOR, TemplateRuleBlockDoors.class);
        manager.registerBlockHandler("vanillaDoors", Blocks.JUNGLE_DOOR, TemplateRuleBlockDoors.class);
        manager.registerBlockHandler("vanillaDoors", Blocks.BIRCH_DOOR, TemplateRuleBlockDoors.class);
        manager.registerBlockHandler("vanillaDoors", Blocks.ACACIA_DOOR, TemplateRuleBlockDoors.class);
        manager.registerBlockHandler("vanillaDoors", Blocks.DARK_OAK_DOOR, TemplateRuleBlockDoors.class);
        manager.registerBlockHandler("vanillaSign", Blocks.WALL_SIGN, TemplateRuleBlockSign.class);
        manager.registerBlockHandler("vanillaSign", Blocks.STANDING_SIGN, TemplateRuleBlockSign.class);
        manager.registerBlockHandler("vanillaLogic", Blocks.MOB_SPAWNER, TemplateRuleBlockLogic.class);
        manager.registerBlockHandler("vanillaLogic", Blocks.COMMAND_BLOCK, TemplateRuleBlockLogic.class);
        manager.registerBlockHandler("vanillaLogic", Blocks.BREWING_STAND, TemplateRuleBlockLogic.class);
        manager.registerBlockHandler("vanillaLogic", Blocks.BEACON, TemplateRuleBlockLogic.class);
        manager.registerBlockHandler("vanillaLogic", Blocks.LIT_FURNACE, TemplateRuleBlockLogic.class);
        manager.registerBlockHandler("vanillaLogic", Blocks.FURNACE, TemplateRuleBlockLogic.class);
        manager.registerBlockHandler("vanillaLogic", Blocks.BEACON, TemplateRuleBlockLogic.class);
        manager.registerBlockHandler("vanillaInventory", Blocks.DISPENSER, TemplateRuleBlockInventory.class);
        manager.registerBlockHandler("vanillaInventory", Blocks.CHEST, TemplateRuleBlockInventory.class);
        manager.registerBlockHandler("vanillaInventory", Blocks.DROPPER, TemplateRuleBlockInventory.class);
        manager.registerBlockHandler("vanillaInventory", Blocks.HOPPER, TemplateRuleBlockInventory.class);
        manager.registerBlockHandler("vanillaInventory", Blocks.TRAPPED_CHEST, TemplateRuleBlockInventory.class);
        manager.registerBlockHandler("vanillaFlowerPot", Blocks.FLOWER_POT, TemplateRuleFlowerPot.class);

        manager.registerBlockHandler("awAdvancedSpawner", AWStructuresBlocks.advancedSpawner, TemplateRuleBlockLogic.class);
        manager.registerBlockHandler("awCoreLogic", AWBlocks.engineeringStation, TemplateRuleBlockLogic.class);
        manager.registerBlockHandler("awCoreLogic", AWBlocks.researchStation, TemplateRuleBlockLogic.class);
        manager.registerBlockHandler("awStructureLogic", AWStructuresBlocks.draftingStation, TemplateRuleBlockLogic.class);
        manager.registerBlockHandler("awStructureLogic", AWStructuresBlocks.builderBlock, TemplateRuleBlockLogic.class);
        manager.registerBlockHandler("awStructureLogic", AWStructuresBlocks.soundBlock, TemplateRuleBlockLogic.class);
    }

    @Override
    public void addHandledEntities(IStructurePluginManager manager) {
        manager.registerEntityHandler("vanillaEntities", EntityPig.class, TemplateRuleVanillaEntity.class);
        manager.registerEntityHandler("vanillaEntities", EntitySheep.class, TemplateRuleVanillaEntity.class);
        manager.registerEntityHandler("vanillaEntities", EntityCow.class, TemplateRuleVanillaEntity.class);
        manager.registerEntityHandler("vanillaEntities", EntityChicken.class, TemplateRuleVanillaEntity.class);
        manager.registerEntityHandler("vanillaEntities", EntityBoat.class, TemplateRuleVanillaEntity.class);
        manager.registerEntityHandler("vanillaEntities", EntityIronGolem.class, TemplateRuleVanillaEntity.class);
        manager.registerEntityHandler("vanillaEntities", EntityWolf.class, TemplateRuleVanillaEntity.class);
        manager.registerEntityHandler("vanillaEntities", EntityOcelot.class, TemplateRuleVanillaEntity.class);
        manager.registerEntityHandler("vanillaEntities", EntityWither.class, TemplateRuleVanillaEntity.class);
        manager.registerEntityHandler("vanillaEntities", EntitySnowman.class, TemplateRuleVanillaEntity.class);

        manager.registerEntityHandler("vanillaHangingEntity", EntityPainting.class, TemplateRuleEntityHanging.class);
        manager.registerEntityHandler("vanillaHangingEntity", EntityItemFrame.class, TemplateRuleEntityHanging.class);

        manager.registerEntityHandler("vanillaLogicEntity", EntityHorse.class, TemplateRuleEntityLogic.class);
        manager.registerEntityHandler("vanillaLogicEntity", EntityVillager.class, TemplateRuleEntityLogic.class);
        manager.registerEntityHandler("vanillaLogicEntity", EntityMinecartHopper.class, TemplateRuleEntityLogic.class);
        manager.registerEntityHandler("vanillaLogicEntity", EntityMinecartChest.class, TemplateRuleEntityLogic.class);
        manager.registerEntityHandler("vanillaLogicEntity", EntityMinecartEmpty.class, TemplateRuleEntityLogic.class);
        manager.registerEntityHandler("vanillaLogicEntity", EntityMinecartFurnace.class, TemplateRuleEntityLogic.class);
        manager.registerEntityHandler("vanillaLogicEntity", EntityMinecartTNT.class, TemplateRuleEntityLogic.class);

        manager.registerEntityHandler("awGate", EntityGate.class, TemplateRuleGates.class);
    }
}
