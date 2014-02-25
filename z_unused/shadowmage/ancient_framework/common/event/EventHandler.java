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
package shadowmage.ancient_framework.common.event;

import net.minecraft.world.WorldServer;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.WorldEvent;
import shadowmage.ancient_framework.common.gamedata.AWGameData;
import shadowmage.ancient_framework.common.item.AWItemClickable;
import shadowmage.ancient_framework.common.utils.BlockPosition;

public class EventHandler
{

@ForgeSubscribe
public void onWorlLoad(WorldEvent.Load evt)
  {
  if(evt.world instanceof WorldServer)
    {
    AWGameData.handleWorldLoad(evt.world);
    }  
  }

@ForgeSubscribe
public void onItemUsed(PlayerInteractEvent evt)
  {
  if(evt.entityPlayer!=null && evt.action == Action.LEFT_CLICK_BLOCK && evt.entityPlayer.inventory.getCurrentItem()!=null && evt.entityPlayer.inventory.getCurrentItem().getItem() instanceof AWItemClickable)
    {
    AWItemClickable item = (AWItemClickable) evt.entityPlayer.inventory.getCurrentItem().getItem();
    if(item.hasLeftClick)
      {
      item.onUsedFinalLeft(evt.entityPlayer.worldObj, evt.entityPlayer, evt.entityPlayer.inventory.getCurrentItem(), new BlockPosition(evt.x, evt.y, evt.z), evt.face);
      evt.setCanceled(true);
      }    
    }  
  }

}
