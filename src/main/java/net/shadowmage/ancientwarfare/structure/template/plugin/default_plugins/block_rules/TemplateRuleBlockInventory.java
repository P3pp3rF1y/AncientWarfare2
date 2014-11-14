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
package net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.api.NBTTools;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;
import net.shadowmage.ancientwarfare.structure.template.build.LootGenerator;

public class TemplateRuleBlockInventory extends TemplateRuleVanillaBlocks
{

public int randomLootLevel = 0;
public NBTTagCompound tag = new NBTTagCompound();
ItemStack[] inventoryStacks;

public TemplateRuleBlockInventory(World world, int x, int y, int z, Block block, int meta, int turns)
  {
  super(world, x, y, z, block, meta, turns);
  TileEntity te = world.getTileEntity(x, y, z);
  if(te instanceof IInventory)
    {
    IInventory inventory = (IInventory)te;
    if(inventory.getSizeInventory()<=0)
      {
      return;
      }
    ItemStack keyStack = inventory.getStackInSlot(0);    
    boolean useKey = keyStack!=null && (keyStack.getItem()==Items.gold_ingot || keyStack.getItem()==Items.diamond || keyStack.getItem()==Items.emerald);
    if(useKey)
      {
      for(int i = 1; i < inventory.getSizeInventory(); i++)
        {
        if(inventory.getStackInSlot(i)!=null)
          {
          useKey = false;
          break;
          }
        }      
      }
    this.randomLootLevel = useKey? keyStack.getItem()==Items.gold_ingot? 1 : keyStack.getItem()==Items.diamond ? 2 : 3 : 0;
    inventoryStacks = new ItemStack[inventory.getSizeInventory()];
    ItemStack stack;
    for(int i = 0; i<inventory.getSizeInventory();i++)
      {
      stack = inventory.getStackInSlot(i);
      inventoryStacks[i] = stack==null ? null : stack.copy();
      }
    }
  te.writeToNBT(tag);
  tag.removeTag("Items");//remove vanilla inventory tag from tile-entities (need to custom handle AW inventoried blocks still)
  }

public TemplateRuleBlockInventory()
  {
  
  }

@Override
public void handlePlacement(World world, int turns, int x, int y, int z, IStructureBuilder builder)
  {
  super.handlePlacement(world, turns, x, y, z, builder);
  TileEntity te = world.getTileEntity(x, y, z);
  IInventory inventory = (IInventory)te;
  tag.setInteger("x", x);
  tag.setInteger("y", y);
  tag.setInteger("z", z);
  te.readFromNBT(tag);
  if(inventory==null){return;}
  if(randomLootLevel>0)
    {    
    for(int i = 0; i< inventory.getSizeInventory(); i++){inventory.setInventorySlotContents(i, null);}//clear the inventory in prep for random loot stuff
    LootGenerator.INSTANCE.generateLootFor(inventory, randomLootLevel-1, world.rand);
    }
  else if(inventoryStacks!=null)
    {
    ItemStack stack;
    for(int i = 0; i < inventory.getSizeInventory();i++)
      {
      stack = i<inventoryStacks.length ? inventoryStacks[i] : null;
      inventory.setInventorySlotContents(i, stack==null? null : stack.copy());
      }
    }
  int localMeta = BlockDataManager.instance().getRotatedMeta(block, this.meta, turns);  
  world.setBlockMetadataWithNotify(x, y, z, localMeta, 3);
  world.markBlockForUpdate(x, y, z);  
  }

@Override
public boolean shouldReuseRule(World world, Block block, int meta, int turns, TileEntity te, int x, int y, int z)
  {
  return false;
  }

@Override
public void writeRuleData(NBTTagCompound tag)
  {
  super.writeRuleData(tag);
  tag.setInteger("lootLevel", randomLootLevel);
  tag.setTag("teData", this.tag);
  
  NBTTagCompound invData = new NBTTagCompound();
  invData.setInteger("length", inventoryStacks.length);
  NBTTagCompound itemTag;
  NBTTagList list = new NBTTagList();
  ItemStack stack;
  for(int i = 0; i < inventoryStacks.length;i++)
    {
    stack = inventoryStacks[i];
    if(stack==null){continue;}
    itemTag = NBTTools.writeItemStack(stack, new NBTTagCompound());    
    itemTag.setInteger("slot", i);
    list.appendTag(itemTag);
    }  
  invData.setTag("inventoryContents", list);
  tag.setTag("inventoryData", invData);
  }

@Override
public void parseRuleData(NBTTagCompound tag)
  {
  super.parseRuleData(tag);
  if(tag.hasKey("inventoryData"))
    {    
    NBTTagCompound inventoryTag = tag.getCompoundTag("inventoryData");
    int length = inventoryTag.getInteger("length");
    inventoryStacks = new ItemStack[length];
    NBTTagCompound itemTag;
    NBTTagList list = inventoryTag.getTagList("inventoryContents", Constants.NBT.TAG_COMPOUND); 
    int slot;
    ItemStack stack;
    for(int i = 0; i < list.tagCount(); i++)
      {      
      itemTag = list.getCompoundTagAt(i);
      stack = NBTTools.readItemStack(itemTag);
      if(stack!=null)
        {
        slot = itemTag.getInteger("slot");
        inventoryStacks[slot] = stack;
        }
      }
    }
  randomLootLevel = tag.getInteger("lootLevel");
  this.tag = tag.getCompoundTag("teData");
  }
}
