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
package shadowmage.ancient_framework.common.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import shadowmage.ancient_framework.common.registry.entry.Description;

public abstract class AWBlockBase extends Block
{

private Random RNG = new Random();
public Description description;

public AWBlockBase(int par1, Material par2Material, String baseName)
  {
  super(par1, par2Material); 
  this.setHardness(3.f);
  this.setUnlocalizedName(baseName);
  }

/**
 * equivalent of onBlockActivated, used to activate a TE/open a gui/toggle a lever/etc
 * @param world
 * @param posX
 * @param posY
 * @param posZ
 * @param par5EntityPlayer
 * @param par6
 * @param par7
 * @param par8
 * @param par9
 * @return
 */
public abstract boolean onBlockClicked(World world, int posX, int posY, int posZ, EntityPlayer player, int sideHit, float hitVecX, float hitVecY, float hitVecZ);

/**
 * if this block has persistent inventory, return it here to be dropped on destruction
 * @param world
 * @param x
 * @param y
 * @param z
 * @param par5
 * @param par6
 * @return
 */
public abstract IInventory[] getInventoryToDropOnBreak(World world, int x, int y, int z, int par5, int par6);

@Override
public boolean onBlockActivated(World world, int posX, int posY, int posZ, EntityPlayer player, int sideHit, float hitVecX, float hitVecY, float hitVecZ)
  {  
  return this.onBlockClicked(world, posX, posY, posZ, player, sideHit, hitVecX, hitVecY, hitVecZ);
  }

@Override
public void registerIcons(IconRegister par1IconRegister)
  {
  registerIcons(par1IconRegister, description);
  }

public abstract void registerIcons(IconRegister reg, Description d);
  
@Override
public Icon getIcon(int side, int meta)
  {
  Description d = description;
  if(d!=null)
    {
    return d.getIconFor(0);    
    }
  return super.getIcon(side, meta);
  }

/**
 * ejects contained items into the world, and notifies neighbours of an update, as appropriate
 */
@Override
public void breakBlock(World world, int x, int y, int z, int par5, int par6)
  {
  IInventory[] inventoriesToDrop = this.getInventoryToDropOnBreak(world,x,y,z,par5,par6);
  if(inventoriesToDrop!=null && inventoriesToDrop.length>0)
    {
    for(IInventory inv : inventoriesToDrop)
      {
      this.dropItems(world, x, y, z, par5, par6, inv);
      }
    }   
  super.breakBlock(world, x, y, z, par5, par6);
  }

public void dropItems(World world, int x, int y, int z, int par5, int par6, IInventory inventory)
  {
  if (inventory != null)
    {
    for (int slotIndex = 0; slotIndex < inventory.getSizeInventory(); ++slotIndex)
      {
      ItemStack currentStack = inventory.getStackInSlot(slotIndex);

      if (currentStack != null)
        {
        float xOff = this.RNG.nextFloat() * 0.8F + 0.1F;
        float yOff = this.RNG.nextFloat() * 0.8F + 0.1F;
        float zOff = this.RNG.nextFloat() * 0.8F + 0.1F;

        while (currentStack.stackSize > 0)
          {
          int randomDropQty = this.RNG.nextInt(21) + 10;

          if (randomDropQty > currentStack.stackSize)
            {
            randomDropQty = currentStack.stackSize;
            }
          currentStack.stackSize -= randomDropQty;
          EntityItem var14 = new EntityItem(world, (double)((float)x + xOff), (double)((float)y + yOff), (double)((float)z + zOff), new ItemStack(currentStack.itemID, randomDropQty, currentStack.getItemDamage()));

          if (currentStack.hasTagCompound())
            {
            var14.getEntityItem().setTagCompound((NBTTagCompound)currentStack.getTagCompound().copy());
            }
          float var15 = 0.05F;
          var14.motionX = (double)((float)this.RNG.nextGaussian() * var15);
          var14.motionY = (double)((float)this.RNG.nextGaussian() * var15 + 0.2F);
          var14.motionZ = (double)((float)this.RNG.nextGaussian() * var15);
          world.spawnEntityInWorld(var14);
          }
        }
      }
    }
  }

}
