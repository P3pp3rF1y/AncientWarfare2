/**
   Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
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

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import shadowmage.ancient_framework.common.config.AWLog;
import shadowmage.ancient_framework.common.config.Statics;
import shadowmage.ancient_framework.common.item.AWItemClickable;
import shadowmage.ancient_framework.common.network.GUIHandler;
import shadowmage.ancient_framework.common.utils.BlockPosition;
import shadowmage.ancient_framework.common.utils.BlockTools;


public class ItemStructureScanner extends AWItemClickable
{

public ItemStructureScanner(Configuration config, String itemName)
  {
  super(config, itemName);
  this.setMaxStackSize(1);  
  this.hasLeftClick = true;
  this.setCreativeTab(AWStructuresItemLoader.structureTab);
  AWLog.logDebug("set creative tab for structure scanner to: "+AWStructuresItemLoader.structureTab);
  }

/**
 * client-side structure setting container, do not access from server-methods!!
 */
ItemStructureSettings viewSettings = new ItemStructureSettings();
@Override
public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List list, boolean par4)
  { 
  if(par1ItemStack!=null)
    {
    ItemStructureSettings.getSettingsFor(par1ItemStack, viewSettings);
    /**
     * TODO add info to tooltip from nbt-tag
     */
    NBTTagCompound tag;
    if(par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("structData"))
      {
      tag = par1ItemStack.getTagCompound().getCompoundTag("structData");
      }
    else
      {
      tag = new NBTTagCompound();
      }
    if(viewSettings.hasPos1() && viewSettings.hasPos2() && viewSettings.hasBuildKey())
      {
      list.add("Right Click: Scan and Process (4/4)");
      list.add("(Shift)Right Click: Cancel/clear");
      }        
    else if(!viewSettings.hasPos1())
      {
      list.add("Left Click: Set first bound (1/4)");
      list.add("Hold shift to offset for side hit");
      list.add("(Shift)Right Click: Cancel/clear");
      }
    else if(!viewSettings.hasPos2())
      {
      list.add("Left Click: Set second bound (2/4)");
      list.add("Hold shift to offset for side hit");
      list.add("(Shift)Right Click: Cancel/clear");
      }
    else if(!viewSettings.hasBuildKey())
      {
      list.add("Left Click: Set build key and");
      list.add("    direction (3/4)");
      list.add("Hold shift to offset for side hit");
      list.add("(Shift)Right Click: Cancel/clear");
      }    
    }  
  }

@Override
public boolean shouldPassSneakingClickToBlock(World par2World, int par4, int par5, int par6)
  {
  return false;
  }

/**
 * server-side structure setting container, do not access from client-methods!!
 */
ItemStructureSettings scanSettings = new ItemStructureSettings();
@Override
public boolean onUsedFinal(World world, EntityPlayer player, ItemStack stack,  BlockPosition hit, int side)
  {
  if(world.isRemote)
    {
    return true;
    }  
  if(!MinecraftServer.getServer().getConfigurationManager().isPlayerOpped(player.getEntityName()))
    {
    return true;
    }
  ItemStructureSettings.getSettingsFor(stack, scanSettings);
  if(player.isSneaking())
    {
    scanSettings.clearSettings();
    ItemStructureSettings.setSettingsFor(stack, scanSettings);
    }
  else if(scanSettings.hasPos1() && scanSettings.hasPos2() && scanSettings.hasBuildKey())
    {
    BlockPosition key = scanSettings.key;
    if(player.getDistance(key.x+0.5d, key.y, key.z+0.5d) > 10)
      {
      player.addChatMessage("You are too far away to scan that building, move closer to chosen build-key position");
      return true;
      }
    player.addChatMessage("Initiating Scan (4/4)");
    GUIHandler.instance().openGUI(Statics.guiStructureScannerCreative, player, 0,0,0);    
    return true;
    } 
  return true;
  }

@Override
public boolean onUsedFinalLeft(World world, EntityPlayer player, ItemStack stack, BlockPosition hit, int side)
  {
  if(world.isRemote)
    {
    return true;
    }  
  if(!MinecraftServer.getServer().getConfigurationManager().isPlayerOpped(player.getEntityName()))
    {
    return true;
    } 
  if(hit!=null && player.isSneaking())
    {
    hit.offsetForMCSide(side);
    }
  ItemStructureSettings.getSettingsFor(stack, scanSettings);
  if(scanSettings.hasPos1() && scanSettings.hasPos2() && scanSettings.hasBuildKey())
    {
    player.addChatMessage("Right Click to Process");
    }
  else if(!scanSettings.hasPos1())
    {
    scanSettings.setPos1(hit.x, hit.y, hit.z);
    player.addChatMessage("Setting Scan Position 1 (Step 1/4)");
    }
  else if(!scanSettings.hasPos2())
    {
    scanSettings.setPos2(hit.x, hit.y, hit.z);
    player.addChatMessage("Setting Scan Position 2 (Step 2/4)");
    }
  else if(!scanSettings.hasBuildKey())
    {
    scanSettings.setBuildKey(hit.x, hit.y, hit.z, BlockTools.getPlayerFacingFromYaw(player.rotationYaw));
    player.addChatMessage("Setting Scan Build Position and Facing (Step 3/4)");
    }
  ItemStructureSettings.setSettingsFor(stack, scanSettings);
  return true;
  }

}
