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
package shadowmage.ancient_structures.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import shadowmage.ancient_framework.common.config.AWLog;
import shadowmage.ancient_framework.common.gamedata.AWGameData;
import shadowmage.ancient_framework.common.utils.BlockPosition;
import shadowmage.ancient_framework.common.utils.BlockTools;
import shadowmage.ancient_structures.common.manager.StructureTemplateManager;
import shadowmage.ancient_structures.common.template.StructureTemplate;
import shadowmage.ancient_structures.common.world_gen.StructureMap;
import shadowmage.ancient_structures.common.world_gen.WorldStructureGenerator;

public class ItemStructureGenerator extends ItemBuilderCreative
{

/**
 * @param itemID
 */
public ItemStructureGenerator(Configuration config, String itemName)
  {
  super(config, itemName);
  }

@Override
public boolean onUsedFinalLeft(World world, EntityPlayer player, ItemStack stack, BlockPosition hit, int side)
  {
  if(player==null || world.isRemote || hit==null)
    {
    return false;
    }
  ItemStructureSettings.getSettingsFor(stack, buildSettings);
  if(buildSettings.hasName())
    {
    StructureTemplate template = StructureTemplateManager.instance().getTemplate(buildSettings.name);
    if(template==null)
      {
      player.addChatMessage("no structure found to build...");
      return true;
      }
    hit.offsetForMCSide(side);        
    if(WorldStructureGenerator.instance().attemptStructureGenerationAt(world, hit.x, hit.y, hit.z, BlockTools.getPlayerFacingFromYaw(player.rotationYaw), template, AWGameData.get(world, "AWStructureMap", StructureMap.class)))
      {
      AWLog.logDebug("constructing template: "+template);
      }    
    }  
  else
    {
    player.addChatMessage("no structure found to build...");
    return true;
    }
  return true;
  }

}
