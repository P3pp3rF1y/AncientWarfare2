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
package shadowmage.ancient_structures.common.template.plugin.default_plugins;

import net.minecraft.block.Block;
import shadowmage.ancient_structures.common.template.plugin.StructureContentPlugin;
import shadowmage.ancient_structures.common.template.plugin.StructurePluginManager;
import shadowmage.ancient_structures.common.template.plugin.default_plugins.block_rules.TemplateRuleModBlocks;

public class StructurePluginModDefault extends StructureContentPlugin
{

public StructurePluginModDefault()
  {
  
  }

@Override
public void addHandledBlocks(StructurePluginManager manager)
  {
  Block block;
  for(int i = 256; i < 4096; i++)
    {
    block = Block.blocksList[i];
    if(block==null){continue;}
    manager.registerBlockHandler("modBlockDefault", block, TemplateRuleModBlocks.class);
    }
  }

@Override
public void addHandledEntities(StructurePluginManager manager)
  {
  
  }

}
