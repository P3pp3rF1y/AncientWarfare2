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
package net.shadowmage.ancientwarfare.structure.template.build.validation;

import cpw.mods.fml.common.eventhandler.Event.Result;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.BiomeEvent;
import net.minecraftforge.event.terraingen.BiomeEvent.GetVillageBlockID;
import net.minecraftforge.event.terraingen.BiomeEvent.GetVillageBlockMeta;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilder;

public class StructureBuilderWorldGen extends StructureBuilder
{

public StructureBuilderWorldGen(World world, StructureTemplate template, int face, int x, int y, int z)
  {
  super(world, template, face, x, y, z);
  }

protected void placeAir()
  {
  if(!template.getValidationSettings().isPreserveBlocks())
    {
    template.getValidationSettings().handleClearAction(world, destination.x, destination.y, destination.z, template, bb);    
    }
  }

@Override
public void placeBlock(int x, int y, int z, Block block, int meta, int priority)
  {
  if(template.getValidationSettings().isBlockSwap())
    {
    BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
    BiomeEvent.GetVillageBlockID evt1 = new GetVillageBlockID(biome, block, meta);
    MinecraftForge.EVENT_BUS.post(evt1);
    if(evt1.getResult()== Result.DENY && evt1.replacement!=block)
      {
      block = evt1.replacement;
      }    
    BiomeEvent.GetVillageBlockMeta evt2 = new GetVillageBlockMeta(biome, block, meta);
    MinecraftForge.EVENT_BUS.post(evt2);
    if(evt2.getResult()==Result.DENY)
      {
      meta = evt2.replacement;
      }    
    }
  super.placeBlock(x, y, z, block, meta, priority);
  }

public void instantConstruction()
  {
  template.getValidationSettings().preGeneration(world, buildOrigin.x, buildOrigin.y, buildOrigin.z, buildFace, template, bb);
  super.instantConstruction();
  }

}
