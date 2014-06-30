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
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.shadowmage.ancientwarfare.automation.item.ItemBlockWorksite;
import net.shadowmage.ancientwarfare.core.config.ClientOptions;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;

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
  if(ClientOptions.INSTANCE.getBooleanValue(ClientOptions.OPTION_RENDER_WORK_BOUNDS))
    {
    renderWorkBounds(player, evt.partialTicks);
    }
  ItemStack stack = player.inventory.getCurrentItem();
  if(stack!=null && stack.getItem() instanceof ItemBlockWorksite)
    {
    renderWorksiteItemSetupBounds(player, stack, evt.partialTicks);
    }  
  }

private void renderWorksiteItemSetupBounds(EntityPlayer player, ItemStack stack, float delta)
  {
  if(!stack.hasTagCompound())
    {
    stack.setTagCompound(new NBTTagCompound());
    }
  
  BlockPosition min = null;
  BlockPosition max = null;
    
  if(stack.getTagCompound().hasKey("pos1"))
    {
    min = new BlockPosition();
    min.read(stack.getTagCompound().getCompoundTag("pos1"));
    }
  else
    {
    min = BlockTools.getBlockClickedOn(player, player.worldObj, player.isSneaking());
    }
  
  if(stack.getTagCompound().hasKey("pos2"))
    {
    max = new BlockPosition();
    max.read(stack.getTagCompound().getCompoundTag("pos2"));
    }
  else
    {
    max = BlockTools.getBlockClickedOn(player, player.worldObj, player.isSneaking());
    if(max==null && min!=null)
      {
      max = min;
      }
    }

  if(min==null || max==null){return;}
  renderBoundingBox(player, BlockTools.getMin(min, max), BlockTools.getMax(min, max).offset(1, 1, 1), delta);  
  }

@SubscribeEvent
public void onRenderTick(RenderTickEvent evt)
  {
  if(evt.phase!=Phase.END){return;}
  Minecraft mc = Minecraft.getMinecraft();
  if(mc==null || mc.theWorld==null || mc.currentScreen!=null || mc.gameSettings.showDebugInfo || mc.thePlayer==null)
    {
    return;
    } 
  EntityPlayer player = mc.thePlayer;
  ItemStack stack = player.getCurrentEquippedItem();
  if(stack!=null && stack.getItem() instanceof ItemBlockWorksite)
    {
    if(!stack.hasTagCompound())
      {
      stack.setTagCompound(new NBTTagCompound());
      }    
    BlockPosition min = null;
    BlockPosition max = null;    
    if(stack.getTagCompound().hasKey("pos1"))
      {
      min = new BlockPosition();
      min.read(stack.getTagCompound().getCompoundTag("pos1"));
      }
    else
      {
      min = BlockTools.getBlockClickedOn(player, player.worldObj, player.isSneaking());
      }  
    if(stack.getTagCompound().hasKey("pos2"))
      {
      max = new BlockPosition();
      max.read(stack.getTagCompound().getCompoundTag("pos2"));
      }
    else
      {
      max = BlockTools.getBlockClickedOn(player, player.worldObj, player.isSneaking());
      if(max==null && min!=null)
        {
        max = min;
        }
      }
    if(min==null || max==null){return;}
    BlockPosition m = BlockTools.getMin(min, max);
    BlockPosition m1 = BlockTools.getMax(min, max);
    min = m;
    max = m1;
    int x = max.x-min.x + 1;
    int y = max.y-min.y + 1;
    int z = max.z-min.z + 1;  
    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
    fr.drawString("foo.x: "+x, 8, 8, 0xffffffff);
    fr.drawString("foo.y: "+y, 8, 18, 0xffffffff);
    fr.drawString("foo.z: "+z, 8, 28, 0xffffffff);
    }
  }

@SuppressWarnings("unchecked")
private void renderWorkBounds(EntityPlayer player, float delta)
  {
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
        pos2Cache.reassign(max.x + 1, max.y + 1, max.z + 1);//using cached value so that the reference can be manipulated
//        renderBoundingBox(player, min, pos2Cache, delta);
        }
      }
    }
  }

private void renderBoundingBox(EntityPlayer player, BlockPosition min, BlockPosition max, float delta, float r, float g, float b, float expansion)
  {
  AxisAlignedBB bb = AxisAlignedBB.getAABBPool().getAABB(min.x, min.y, min.z, max.x, max.y, max.z);
  if(expansion!=0.f){bb = bb.expand(expansion, expansion, expansion);}
  RenderTools.adjustBBForPlayerPos(bb, player, delta);
  RenderTools.drawOutlinedBoundingBox(bb, r, g, b);
  }

private void renderBoundingBox(EntityPlayer player, BlockPosition min, BlockPosition max, float delta)
  {
  renderBoundingBox(player, min, max, delta, 1.f, 1.f, 1.f, 0.f);
  }

}
