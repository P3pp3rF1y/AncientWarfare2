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
package net.shadowmage.ancientwarfare.structure.api;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * base template-rule class.  Plugins should define their own rule classes.
 * all data to place the block/entity/target of the rule must be contained in the rule.
 * ONLY one rule per block-position in the template.  So -- no entity/block combination in same space unless
 * handled specially via a plugin rule
 * @author Shadowmage
 */
public abstract class TemplateRule
{

public int ruleNumber = -1;
/**
 * all sub-classes must implement a no-param constructor for when loaded from file (at which point they should initialize from the parseRuleData method)
 */
public TemplateRule()
  {
 
  }

/**
 * input params are the target position for placement of this rule and destination orientation
 * @param world
 * @param turns
 * @param x
 * @param y
 * @param z 
 */
public abstract void handlePlacement(World world, int turns, int x, int y, int z, IStructureBuilder builder);

public abstract void parseRuleData(NBTTagCompound tag);
public abstract void writeRuleData(NBTTagCompound tag);

public abstract void addResources(List<ItemStack> resources);

public abstract boolean shouldPlaceOnBuildPass(World world, int turns, int x, int y, int z, int buildPass);

public void writeRule(BufferedWriter out) throws IOException
  {
  NBTTagCompound tag = new NBTTagCompound();
  writeRuleData(tag);
  writeTag(out, tag);
  }

public void parseRule(int ruleNumber, List<String> lines)
  {
  this.ruleNumber = ruleNumber;
  NBTTagCompound tag = readTag(lines);
  parseRuleData(tag);
  }

public final void writeTag(BufferedWriter out, NBTTagCompound tag) throws IOException
  {
  out.write("tag:");
  out.newLine();
  List<String> tagData = NBTTools.getLinesFor(tag);
  for(String line : tagData)
    {
    out.write(line);
    out.newLine();
    }
  out.write(":endtag");
  out.newLine();
  }

public final NBTTagCompound readTag(List<String> ruleData)
  {
  List<String> tagLines = new ArrayList<String>();  
  String line;
  Iterator<String> it = ruleData.iterator();
  while(it.hasNext() && (line=it.next())!=null)
    {
    if(line.startsWith("tag:"))
      {
      it.remove();
      while(it.hasNext() && (line=it.next())!=null)
        {
        it.remove();
        if(line.startsWith(":endtag"))
          {
          break;
          }
        tagLines.add(line);       
        }
      }
    }
  return NBTTools.readNBTFrom(tagLines);
  }

}
