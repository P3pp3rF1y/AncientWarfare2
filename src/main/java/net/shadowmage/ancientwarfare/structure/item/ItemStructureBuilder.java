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
package net.shadowmage.ancientwarfare.structure.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.item.ItemClickable;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilder;


public class ItemStructureBuilder extends ItemClickable implements IItemKeyInterface
{

/**
 * @param itemID
 */
public ItemStructureBuilder(String itemName)
  {
  super(itemName);  
  this.setCreativeTab(AWStructuresItemLoader.structureTab);
  this.hasLeftClick = false;
  this.setMaxStackSize(1);  
  }

ItemStructureSettings buildSettings = new ItemStructureSettings();

public void onLeftClick(ItemStack stack, EntityPlayer player, MovingObjectPosition hit)
  {
  
  }

@Override
public void onRightClick(ItemStack stack, EntityPlayer player, MovingObjectPosition hit)
  {
  if(!player.worldObj.isRemote && !player.isSneaking())
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_BUILDER, 0, 0, 0);   
    }        
  }

ItemStructureSettings viewSettings = new ItemStructureSettings();
@Override
public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
  {
  String structure = "guistrings.no_selection";
  ItemStructureSettings.getSettingsFor(stack, viewSettings);
  if(viewSettings.hasName())
    {
    structure = viewSettings.name;
    }  
  list.add(StatCollector.translateToLocal("guistrings.current_structure")+" "+StatCollector.translateToLocal(structure));
  }

@Override
public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player)
  {
  return false;
  }

@Override
public void onKeyAction(EntityPlayer player, ItemStack stack)
  {
  if(player==null || player.worldObj.isRemote)
    {
    return;
    }
  ItemStructureSettings.getSettingsFor(stack, buildSettings);
  if(buildSettings.hasName())
    {
    StructureTemplate template = StructureTemplateManager.instance().getTemplate(buildSettings.name);
    if(template==null)
      {
      /**
       * TODO add chat message
       */
      return;
      }
    BlockPosition bpHit = BlockTools.getBlockClickedOn(player, player.worldObj, true);    
    StructureBuilder builder = new StructureBuilder(player.worldObj, template, BlockTools.getPlayerFacingFromYaw(player.rotationYaw), bpHit.x, bpHit.y, bpHit.z);
    builder.instantConstruction();
    }  
  else
    {
    /**
     * TODO add chat message
     */
    }  
  }

}
