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
package net.shadowmage.ancientwarfare.structure.event;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItemLoader;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockStructureBuilder;
import net.shadowmage.ancientwarfare.structure.item.ItemConstructionTool;
import net.shadowmage.ancientwarfare.structure.item.ItemConstructionTool.ConstructionSettings;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureSettings;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateClient;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManagerClient;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class StructureBoundingBoxRenderer
{


private static StructureBoundingBoxRenderer INSTANCE = new StructureBoundingBoxRenderer();
private StructureBoundingBoxRenderer(){}
public static StructureBoundingBoxRenderer instance(){return INSTANCE;}


BlockPosition p1 = new BlockPosition(), p2 = new BlockPosition(), min = new BlockPosition(), max = new BlockPosition();

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
  ItemStack stack = player.inventory.getCurrentItem();
  Item item;
  if(stack==null || (item=stack.getItem())==null)
    {
    return;
    }
  if(item==AWStructuresItemLoader.scanner)
    {
    renderScannerBoundingBox(player, stack, evt.partialTicks);
    }  
  else if(item==AWStructuresItemLoader.builder)
    {
    renderBuildBoundingBox(player, stack, evt.partialTicks);
    }
  else if(item==AWStructuresItemLoader.gateSpawner)
    {
    renderGateBoundingBox(player, stack, evt.partialTicks);
    }
  else if(item instanceof ItemBlockStructureBuilder)
    {
    renderSurvivalBuilderBoundingBox(player, stack, evt.partialTicks);
    }
  else if(item==AWStructuresItemLoader.constructionTool)
    {
    renderConstructionToolBoxes(player, stack, evt.partialTicks);
    }
  }

StructureBB bb = new StructureBB(new BlockPosition(), new BlockPosition()){};
ItemStructureSettings settings = new ItemStructureSettings();

private void renderScannerBoundingBox(EntityPlayer player, ItemStack stack, float delta)
  {
  ItemStructureSettings.getSettingsFor(stack, settings);
  BlockPosition pos1, pos2, min, max;
  if(settings.hasPos1())
    {
    pos1 = settings.pos1();
    }
  else
    {
    pos1 = BlockTools.getBlockClickedOn(player, player.worldObj, player.isSneaking());
    }
  if(settings.hasPos2())
    {
    pos2 = settings.pos2();
    }
  else
    {
    pos2 = BlockTools.getBlockClickedOn(player, player.worldObj, player.isSneaking());
    }
  if(pos1!=null && pos2!=null)
    {
    min = BlockTools.getMin(pos1, pos2);
    max = BlockTools.getMax(pos1, pos2);
    renderBoundingBox(player, min, max, delta);
    }
  }

private void renderSurvivalBuilderBoundingBox(EntityPlayer player, ItemStack stack, float delta)
  {
  if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey("structureName")){return;}
  String name = stack.getTagCompound().getString("structureName");
  StructureTemplateClient t = StructureTemplateManagerClient.instance().getClientTemplate(name);
  if(t==null){return;}
  BlockPosition hit = BlockTools.getBlockClickedOn(player, player.worldObj, true);
  if(hit==null){return;}
  int face = BlockTools.getPlayerFacingFromYaw(player.rotationYaw);
  p1.reassign(hit.x, hit.y, hit.z);
  p2.reassign(hit.x, hit.y, hit.z);
  renderBoundingBox(player, p1, p2, delta);
  p2.reassign(hit.x, hit.y, hit.z);
  p2.moveForward(face, t.zSize - 1 - t.zOffset + 1);
  bb.setFromStructure(p2.x, p2.y, p2.z, face, t.xSize, t.ySize, t.zSize, t.xOffset, t.yOffset, t.zOffset);
  p2.reassign(bb.max.x, bb.max.y, bb.max.z);
  renderBoundingBox(player, bb.min, p2, delta);
  }

private void renderBuildBoundingBox(EntityPlayer player, ItemStack stack, float delta)
  {
  ItemStructureSettings.getSettingsFor(stack, settings);
  if(!settings.hasName()){return;}
  String name = settings.name();
  StructureTemplateClient structure = StructureTemplateManagerClient.instance().getClientTemplate(name);
  if(structure==null){return;}
  BlockPosition hit = BlockTools.getBlockClickedOn(player, player.worldObj, true);
  int face = BlockTools.getPlayerFacingFromYaw(player.rotationYaw);
  if(hit==null){return;}
  bb.setFromStructure(hit.x, hit.y, hit.z, face, structure.xSize, structure.ySize, structure.zSize, structure.xOffset, structure.yOffset, structure.zOffset);
  BlockPosition pos1 = bb.min;
  BlockPosition pos2 = bb.max.copy();
  renderBoundingBox(player, pos1, pos2, delta);
  }

private void renderGateBoundingBox(EntityPlayer player, ItemStack stack, float delta)
  {
  NBTTagCompound tag = stack.getTagCompound();
  if(tag!=null && tag.hasKey("AWGateInfo"))
    {
    tag = tag.getCompoundTag("AWGateInfo");
    if(tag.hasKey("pos1"))
      {
      p1.read(tag.getCompoundTag("pos1"));
      if(tag.hasKey("pos2"))
        {
        p2.read(tag.getCompoundTag("pos2"));
        }
      else
        {
        BlockPosition p = BlockTools.getBlockClickedOn(player, player.worldObj, true);
        if(p==null){return;}
        p2.reassign(p.x, p.y, p.z);
        }
      }
    else
      {
      BlockPosition p = BlockTools.getBlockClickedOn(player, player.worldObj, true);
      if(p==null){return;}
      p1.reassign(p.x, p.y, p.z);
      p2.reassign(p1.x, p1.y, p1.z);
      }
    }
  else
    {
    BlockPosition p = BlockTools.getBlockClickedOn(player, player.worldObj, true);
    if(p==null){return;}
    p1.reassign(p.x, p.y, p.z);
    p2.reassign(p1.x, p1.y, p1.z);
    }
  BlockTools.getMin(p1, p2, min);
  BlockTools.getMax(p1, p2, max);
  renderBoundingBox(player, min, max, delta);
  }
 
private void renderConstructionToolBoxes(EntityPlayer player, ItemStack stack, float delta)
  {
  ConstructionSettings settings = ItemConstructionTool.getSettings(stack);
  BlockPosition p1, p2;
  BlockPosition p3 = BlockTools.getBlockClickedOn(player, player.worldObj, player.isSneaking());
  p1 = settings.hasPos1() ? settings.pos1() : p3;  
  p2 = settings.hasPos2() ? settings.pos2() : p3;
  if(p1!=null && p2!=null)
    {
    renderBoundingBox(player, BlockTools.getMin(p1, p2), BlockTools.getMax(p1, p2), delta);
    }
  if(p3!=null)
    {
    renderBoundingBox(player, p3, p3, delta, 1, 0, 0);    
    }
  }

private void renderBoundingBox(EntityPlayer player, BlockPosition min, BlockPosition max, float delta)
  {
  AxisAlignedBB bb = AxisAlignedBB.getAABBPool().getAABB(min.x, min.y, min.z, max.x+1, max.y+1, max.z+1);
  RenderTools.adjustBBForPlayerPos(bb, player, delta);
  RenderTools.drawOutlinedBoundingBox(bb, 1.f, 1.f, 1.f);
  }

private void renderBoundingBox(EntityPlayer player, BlockPosition min, BlockPosition max, float delta, float r, float g, float b)
  {
  AxisAlignedBB bb = AxisAlignedBB.getAABBPool().getAABB(min.x, min.y, min.z, max.x+1, max.y+1, max.z+1);
  RenderTools.adjustBBForPlayerPos(bb, player, delta);
  RenderTools.drawOutlinedBoundingBox(bb, r, g, b);
  }

}
