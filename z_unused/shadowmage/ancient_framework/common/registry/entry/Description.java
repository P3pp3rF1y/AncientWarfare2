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
package shadowmage.ancient_framework.common.registry.entry;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class Description
{
private final HashMap<Integer, String> names = new HashMap<Integer, String>();
private final HashMap<Integer, String> descriptions = new HashMap<Integer, String>();
private final HashMap<Integer, List<String>> tooltips = new HashMap<Integer, List<String>>();
private final HashMap<Integer, Icon> icons = new HashMap<Integer, Icon>();
private final HashMap<Integer, String> iconTextures = new HashMap<Integer, String>();
private final ArrayList<ItemStack> displayStackCache = new ArrayList<ItemStack>();

public Description()
  {
  
  }

public Description setName(String name, int damage)
  {
  this.names.put(damage, name);
  return this;
  }

public Description setDescription(String desc, int damage)
  {
  this.descriptions.put(damage, desc);
  return this;
  }

public Description addTooltip(String tooltip, int damage)
  {  
  if(!this.tooltips.containsKey(damage))
    {
    this.tooltips.put(damage, new ArrayList<String>());
    }
  this.tooltips.get(damage).add(tooltip);
  return this;
  }

public Description setIcon(Icon icon, int damage)
  { 
  this.icons.put(damage, icon);
  return this;
  }

public Description setIconTexture(String tex, int damage)
  {
  this.iconTextures.put(damage, tex);
  return this;
  }

public String getDisplayName(int damage)
  {
  return this.names.get(damage);
  }

public String getDescription(int damage)
  {
  return this.descriptions.get(damage);
  }

public List<String> getDisplayTooltips(int damage)
  {
  List<String> lst = this.tooltips.get(damage);
  if(lst==null)
    {
    return Collections.emptyList();
    }
  return lst;
  }

public Icon getIconFor(int damage)
  {
  return this.icons.get(damage);
  }

public String getIconTexture(int damage)
  {
  return this.iconTextures.get(damage);
  }

public String getDisplayName(ItemStack stack)
  {
  return stack==null ? "" :  this.getDisplayName(stack.getItemDamage());
  }

public String getDescription(ItemStack stack)
  {
  return stack==null ? "" : this.getDescription(stack.getItemDamage());
  }

public List<String> getDisplayTooltip(ItemStack stack)
  {
  return (List<String>) (stack==null ? Collections.emptyList() : this.getDisplayTooltips(stack.getItemDamage()));
  }

public Icon getIconFor(ItemStack stack)
  {
  return stack==null ? null : this.getIconFor(stack.getItemDamage());
  }

public String getIconTextureFor(ItemStack stack)
  {
  return stack== null ? "foo" : this.getIconTexture(stack.getItemDamage());
  }

public Description addDisplayStack(ItemStack stack)
  {
  if(stack!=null)
    {
    this.displayStackCache.add(stack);
    }
  return this;
  }

public List<ItemStack> getDisplayStackCache()
  {
  return this.displayStackCache;
  }

public void registerIcons(IconRegister reg)
  {
  for(Integer key : this.iconTextures.keySet())
    {    
    this.icons.put(key, reg.registerIcon(this.iconTextures.get(key)));
    }
  }

}
