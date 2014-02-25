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

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import shadowmage.ancient_framework.common.registry.entry.Description;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AWItemBlockBase extends ItemBlock
{

public Description description;
/**
 * @param par1
 */
public AWItemBlockBase(int par1)
  {
  super(par1);
  this.setHasSubtypes(true);
  }

/**
 * Returns the metadata of the block which this Item (ItemBlock) can place
 */
@Override
public int getMetadata(int par1)
  {
  return par1;
  }

/**
 * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
 */
@SideOnly(Side.CLIENT)
@Override
public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
  {
  Description d = description;
  if(d!=null)
    {
    par3List.addAll(d.getDisplayStackCache());
    }
  else
    {
    super.getSubItems(par1, par2CreativeTabs, par3List);
    } 
  }

@Override
public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
  {
  if(stack!=null)
    {
    Description d = description;
    if(d!=null)
      {
      List<String> tips = d.getDisplayTooltips(stack.getItemDamage());
      if(tips!=null && !tips.isEmpty())
        {
        for(String tip : tips)
          {
          list.add(tip);
          }        
        }
      }     
    }  
  }

@Override
public String getItemStackDisplayName(ItemStack par1ItemStack)
  {
  return getItemDisplayName(par1ItemStack);
  }

@Override
public String getUnlocalizedName()
  {
  Description d = description;
  if(d!=null)
    {
    return d.getDisplayName(0);
    }
  return "Unregistered Item : "+itemID;
  }

@Override
public String getUnlocalizedName(ItemStack par1ItemStack)
  {
  Description d = description;
  if(d!=null)
    {
    return d.getDisplayName(par1ItemStack.getItemDamage());
    }
  return "Unregistered Item : "+itemID+":"+par1ItemStack.getItemDamage();
  }

@Override
public String getItemDisplayName(ItemStack par1ItemStack)
  {
  Description d = description;
  if(d!=null)
    {
    return StatCollector.translateToLocal(d.getDisplayName(par1ItemStack.getItemDamage()));
    }
  return "Unregistered Item : "+itemID+":"+par1ItemStack.getItemDamage();
  }

@Override
public void registerIcons(IconRegister par1IconRegister)
  {
  Description d = description;
  if(d!=null)
    {
    d.registerIcons(par1IconRegister);
    }
  }

@Override
public Icon getIconFromDamage(int par1)
  {
  Description d = description;
  if(d!=null)
    {
    return d.getIconFor(par1);
    }
  return super.getIconFromDamage(par1);
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
