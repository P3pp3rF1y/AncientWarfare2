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
package net.shadowmage.ancientwarfare.automation.render;

import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.automation.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.automation.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class WorkBoundingBoxRenderer
{

private static WorkBoundingBoxRenderer INSTANCE = new WorkBoundingBoxRenderer();
private WorkBoundingBoxRenderer(){}
public static WorkBoundingBoxRenderer instance(){return INSTANCE;}

//cached blockPosition to avoid creating a new object every render tick
BlockPosition pos2Cache = new BlockPosition();

@SubscribeEvent
public void handleRenderLastEvent(RenderWorldLastEvent evt)
  {
  if(!AWAutomationStatics.renderWorkBounds)
    {
    return;
    }
  Minecraft mc = Minecraft.getMinecraft();
  if(mc==null)
    {
    return;
    }
  EntityPlayer player = mc.thePlayer;
  if(player==null)
    {
    return;
    }
  World world = player.worldObj;
  Iterator<TileEntity> it = world.loadedTileEntityList.iterator();
  TileEntity te;
  BlockPosition min;
  BlockPosition max;
  IWorkSite site;
  while(it.hasNext() && (te = it.next())!=null)
    {
    if(te instanceof IWorkSite)
      {      
      site = (IWorkSite)te;
      if(site.hasWorkBounds())
        {
        min = site.getWorkBoundsMin();
        max = site.getWorkBoundsMax();
        if(max==null)
          {
          max = min;
          }
        pos2Cache.reassign(max.x + 1, max.y + 1, max.z + 1);
        renderBoundingBox(player, min, pos2Cache, evt.partialTicks);
        }            
      }
    }
  }

private void renderBoundingBox(EntityPlayer player, BlockPosition min, BlockPosition max, float delta)
  {
  AxisAlignedBB bb = AxisAlignedBB.getAABBPool().getAABB(min.x, min.y, min.z, max.x, max.y, max.z);
  RenderTools.adjustBBForPlayerPos(bb, player, delta);
  RenderTools.drawOutlinedBoundingBox(bb, 1.f, 1.f, 1.f);
  }

}
