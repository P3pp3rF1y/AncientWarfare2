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
package shadowmage.ancient_framework.common.item;

import java.util.HashMap;
import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class AWItemBase extends Item
{

String itemName;

protected HashMap<Integer, Icon> icons = new HashMap<Integer, Icon>();
protected HashMap<Integer, String> iconTextures = new HashMap<Integer, String>();
protected HashMap<Integer, List<String>> tooltips = new HashMap<Integer, List<String>>();
protected HashMap<Integer, ItemStack> displayStacks = new HashMap<Integer, ItemStack>();
protected HashMap<Integer, String> displayNames = new HashMap<Integer, String>();

public AWItemBase(Configuration config, String itemName)
  {
  super(config.getItem(itemName, 10000).getInt(10000));
  this.itemName = itemName;
  }

public AWItemBase addIcon(int dmg, String tex)
  {
  this.iconTextures.put(dmg, tex);
  return this;
  }

public AWItemBase addDisplayStack(int dmg, ItemStack stack)
  {
  displayStacks.put(dmg, stack);
  return this;
  }

public AWItemBase addDisplayName(int dmg, String key)
  {
  this.displayNames.put(dmg, key);
  return this;
  }

public AWItemBase addTooltip(int dmg, List<String> keys)
  {
  this.tooltips.put(dmg, keys);
  return this;
  }

@SideOnly(Side.CLIENT)
@Override
public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
  {  
  if(!displayStacks.isEmpty())
    {
    par3List.addAll(displayStacks.values());
    }
  else
    {
    super.getSubItems(par1, par2CreativeTabs, par3List);
    } 
  }

@Override
public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
  {
  if(tooltips.containsKey(stack.getItemDamage()))
    {
    list.addAll(tooltips.get(stack.getItemDamage()));
    }
  }

@Override
public String getItemStackDisplayName(ItemStack par1ItemStack)
  {
  return getItemDisplayName(par1ItemStack);
  }

@Override
public String getItemDisplayName(ItemStack par1ItemStack)
  {
  String itemName = this.itemName;
  if(displayNames.containsKey(par1ItemStack.getItemDamage()))
    {
    itemName = displayNames.get(par1ItemStack.getItemDamage());
    }
  return StatCollector.translateToLocal(itemName);
  }

@Override
public String getUnlocalizedName()
  {
  return itemName;
  }

@Override
public String getUnlocalizedName(ItemStack par1ItemStack)
  {
  return getUnlocalizedName();
  }

@Override
public void registerIcons(IconRegister par1IconRegister)
  {
  for(Integer key : this.iconTextures.keySet())
    {
    this.icons.put(key, par1IconRegister.registerIcon(this.iconTextures.get(key)));
    }
  }
 
@Override
public Icon getIconFromDamage(int par1)
  {
  return icons.get(par1); 
  }

@Override
public Icon getIconFromDamageForRenderPass(int par1, int par2)
  {
  return getIconFromDamage(par1);
  }

@Override
public Icon getIcon(ItemStack stack, int renderPass, EntityPlayer player,  ItemStack usingItem, int useRemaining)
  {
  return getIconFromDamage(stack.getItemDamage());
  }

@Override
public Icon getIcon(ItemStack stack, int pass)
  {
  return getIconFromDamage(stack.getItemDamage());
  }

}
