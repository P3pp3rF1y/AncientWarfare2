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

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import shadowmage.ancient_framework.common.config.Statics;
import shadowmage.ancient_framework.common.item.AWItemClickable;
import shadowmage.ancient_framework.common.network.GUIHandler;
import shadowmage.ancient_framework.common.utils.BlockPosition;

public class ItemSpawnerPlacer extends AWItemClickable
{

/**
 * @param itemID
 */
public ItemSpawnerPlacer(Configuration config, String itemName)
  {
  super(config, itemName);
  this.setCreativeTab(AWStructuresItemLoader.structureTab);
  }

@Override
public boolean onUsedFinal(World world, EntityPlayer player, ItemStack stack, BlockPosition hit, int side)
  {
  if(world.isRemote || player==null || stack==null){return false;}  
  if(player.capabilities.isCreativeMode && player.isSneaking())
    {
    GUIHandler.instance().openGUI(Statics.guiSpawnerPlacer, player, 0, 0, 0);
    return true;
    }
  else if(hit!=null)
    {
    hit.offsetForMCSide(side);
    world.setBlock(hit.x, hit.y, hit.z, Block.mobSpawner.blockID);
    if(stack.hasTagCompound() && stack.getTagCompound().hasKey("spawnData"))
      {
      NBTTagCompound tag = stack.getTagCompound().getCompoundTag("spawnData");
      String mobID = tag.getString("mobID");
      TileEntityMobSpawner te = (TileEntityMobSpawner) world.getBlockTileEntity(hit.x, hit.y, hit.z);
      te.getSpawnerLogic().setMobID(mobID);
      world.markBlockForUpdate(hit.x, hit.y, hit.z);      
      }
    if(!player.capabilities.isCreativeMode)
      {
      stack.stackSize--;
      if(stack.stackSize<=0)
        {
        player.inventory.mainInventory[player.inventory.currentItem]=null;
        }
      }
    return true;
    }
  return true;
  }

@Override
public boolean onUsedFinalLeft(World world, EntityPlayer player, ItemStack stack, BlockPosition hit, int side)
  {
  return false;
  }

@Override
public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
  {
  if(stack.hasTagCompound() && stack.getTagCompound().hasKey("spawnData"))
    {
    NBTTagCompound tag = stack.getTagCompound().getCompoundTag("spawnData");
    String mobID = tag.getString("mobID");
    list.add("Mob to place in spawner:");
    list.add(mobID);
    }  
  }



}
