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
package shadowmage.ancient_structures.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.ForgeSubscribe;
import shadowmage.ancient_framework.client.render.RenderTools;
import shadowmage.ancient_framework.common.utils.BlockPosition;
import shadowmage.ancient_framework.common.utils.BlockTools;
import shadowmage.ancient_structures.common.item.AWStructuresItemLoader;
import shadowmage.ancient_structures.common.item.ItemStructureSettings;
import shadowmage.ancient_structures.common.manager.StructureTemplateManager;
import shadowmage.ancient_structures.common.template.StructureTemplateClient;
import shadowmage.ancient_structures.common.template.build.StructureBB;

public class StructureBoundingBoxRenderer
{


private static StructureBoundingBoxRenderer INSTANCE = new StructureBoundingBoxRenderer();
private StructureBoundingBoxRenderer(){}
public static StructureBoundingBoxRenderer instance(){return INSTANCE;}

@ForgeSubscribe
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
  if(item==AWStructuresItemLoader.structureBuilderCreative || item==AWStructuresItemLoader.structureGenerator)
    {
    renderBuildBoundingBox(player, stack, evt.partialTicks);
    }
  else if(item==AWStructuresItemLoader.structureScanner)
    {
    renderScannerBoundingBox(player, stack, evt.partialTicks);
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
    max.offset(1, 1, 1);
    renderBoundingBox(player, min, max, delta);
    }
  }

private void renderBuildBoundingBox(EntityPlayer player, ItemStack stack, float delta)
  {
  ItemStructureSettings.getSettingsFor(stack, settings);
  if(!settings.hasName()){return;}
  String name = settings.name();
  StructureTemplateClient structure = StructureTemplateManager.instance().getClientTemplate(name);
  if(structure==null){return;}
  BlockPosition hit = BlockTools.getBlockClickedOn(player, player.worldObj, true);
  int face = BlockTools.getPlayerFacingFromYaw(player.rotationYaw);
  if(hit==null){return;}
  bb.setFromStructure(hit.x, hit.y, hit.z, face, structure.xSize, structure.ySize, structure.zSize, structure.xOffset, structure.yOffset, structure.zOffset);
  BlockPosition pos1 = bb.min;
  BlockPosition pos2 = bb.max.copy();
  pos2.offset(1, 1, 1);
  renderBoundingBox(player, pos1, pos2, delta);
  }

private void renderBoundingBox(EntityPlayer player, BlockPosition min, BlockPosition max, float delta)
  {
  AxisAlignedBB bb = AxisAlignedBB.getAABBPool().getAABB(min.x, min.y, min.z, max.x, max.y, max.z);
  RenderTools.adjustBBForPlayerPos(bb, player, delta);
  RenderTools.drawOutlinedBoundingBox(bb, 1.f, 1.f, 1.f);
  }

}
