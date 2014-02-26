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
package net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins;

import java.util.Iterator;

import net.minecraft.block.Block;
import net.shadowmage.ancientwarfare.structure.template.StructurePluginManager;
import net.shadowmage.ancientwarfare.structure.template.plugin.StructureContentPlugin;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules.TemplateRuleModBlocks;

public class StructurePluginModDefault extends StructureContentPlugin
{

public StructurePluginModDefault()
  {
  
  }

@Override
public void addHandledBlocks(StructurePluginManager manager)
  {
  Iterator<Block> it = Block.blockRegistry.iterator();
  while(it.hasNext())
    {
    manager.registerBlockHandler("modBlockDefault", it.next(), TemplateRuleModBlocks.class);
    }  
  }

@Override
public void addHandledEntities(StructurePluginManager manager)
  {
  
  }

}
