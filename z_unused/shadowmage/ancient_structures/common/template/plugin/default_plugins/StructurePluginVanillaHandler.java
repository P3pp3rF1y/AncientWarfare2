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
package shadowmage.ancient_structures.common.template.plugin.default_plugins;

import java.util.HashSet;

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
import shadowmage.ancient_structures.common.template.plugin.StructureContentPlugin;
import shadowmage.ancient_structures.common.template.plugin.StructurePluginManager;
import shadowmage.ancient_structures.common.template.plugin.default_plugins.block_rules.TemplateRuleBlockDoors;
import shadowmage.ancient_structures.common.template.plugin.default_plugins.block_rules.TemplateRuleBlockInventory;
import shadowmage.ancient_structures.common.template.plugin.default_plugins.block_rules.TemplateRuleBlockLogic;
import shadowmage.ancient_structures.common.template.plugin.default_plugins.block_rules.TemplateRuleBlockSign;
import shadowmage.ancient_structures.common.template.plugin.default_plugins.block_rules.TemplateRuleVanillaBlocks;
import shadowmage.ancient_structures.common.template.plugin.default_plugins.entity_rules.TemplateRuleEntityHanging;
import shadowmage.ancient_structures.common.template.plugin.default_plugins.entity_rules.TemplateRuleEntityLogic;
import shadowmage.ancient_structures.common.template.plugin.default_plugins.entity_rules.TemplateRuleVanillaEntity;

public class StructurePluginVanillaHandler extends StructureContentPlugin
{

HashSet<Block> specialHandledBlocks = new HashSet<Block>();//just a temp cache to keep track of what blocks to not register with blanket block rule

public StructurePluginVanillaHandler()
  {
  
  }

@Override
public void addHandledBlocks(StructurePluginManager manager)
  {  
  specialHandledBlocks.add(Block.doorIron);
  specialHandledBlocks.add(Block.doorWood);
  specialHandledBlocks.add(Block.signPost);
  specialHandledBlocks.add(Block.signWall);
  specialHandledBlocks.add(Block.mobSpawner);
  specialHandledBlocks.add(Block.commandBlock);  
  specialHandledBlocks.add(Block.skull);
  specialHandledBlocks.add(Block.furnaceBurning);
  specialHandledBlocks.add(Block.brewingStand);
  specialHandledBlocks.add(Block.beacon);
  specialHandledBlocks.add(Block.dispenser);
  specialHandledBlocks.add(Block.furnaceIdle);
  specialHandledBlocks.add(Block.chest);
  specialHandledBlocks.add(Block.dropper);
  specialHandledBlocks.add(Block.hopperBlock);
  specialHandledBlocks.add(Block.beacon);
    
  Block block;
  for(int i = 0; i < 256; i++)
    {
    block = Block.blocksList[i];
    if(block!=null && ! specialHandledBlocks.contains(block))
      {
      manager.registerBlockHandler("vanillaBlocks", block, TemplateRuleVanillaBlocks.class);
      }
    } 
  specialHandledBlocks.clear();
  
  manager.registerBlockHandler("vanillaDoors", Block.doorIron, TemplateRuleBlockDoors.class);
  manager.registerBlockHandler("vanillaDoors", Block.doorWood, TemplateRuleBlockDoors.class);
  manager.registerBlockHandler("vanillaSign", Block.signPost, TemplateRuleBlockSign.class);
  manager.registerBlockHandler("vanillaSign", Block.signWall, TemplateRuleBlockSign.class);
  manager.registerBlockHandler("vanillaLogic", Block.mobSpawner, TemplateRuleBlockLogic.class);
  manager.registerBlockHandler("vanillaLogic", Block.commandBlock, TemplateRuleBlockLogic.class);
  manager.registerBlockHandler("vanillaLogic", Block.brewingStand, TemplateRuleBlockLogic.class);
  manager.registerBlockHandler("vanillaLogic", Block.beacon, TemplateRuleBlockLogic.class);
  manager.registerBlockHandler("vanillaLogic", Block.skull, TemplateRuleBlockLogic.class);
  manager.registerBlockHandler("vanillaLogic", Block.furnaceBurning, TemplateRuleBlockLogic.class);
  manager.registerBlockHandler("vanillaLogic", Block.furnaceIdle, TemplateRuleBlockLogic.class);
  manager.registerBlockHandler("vanillaLogic", Block.beacon, TemplateRuleBlockLogic.class);
  manager.registerBlockHandler("vanillaInventory", Block.dispenser, TemplateRuleBlockInventory.class);
  manager.registerBlockHandler("vanillaInventory", Block.chest, TemplateRuleBlockInventory.class);
  manager.registerBlockHandler("vanillaInventory", Block.dropper, TemplateRuleBlockInventory.class);
  manager.registerBlockHandler("vanillaInventory", Block.hopperBlock, TemplateRuleBlockInventory.class);
  }


@Override
public void addHandledEntities(StructurePluginManager manager)
  {  
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
  }

}
