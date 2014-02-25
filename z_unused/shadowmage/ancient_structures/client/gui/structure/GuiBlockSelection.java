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
package shadowmage.ancient_structures.client.gui.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import shadowmage.ancient_framework.client.gui.GuiContainerAdvanced;
import shadowmage.ancient_framework.client.gui.elements.GuiButtonSimple;
import shadowmage.ancient_framework.client.gui.elements.GuiCheckBoxSimple;
import shadowmage.ancient_framework.client.gui.elements.GuiItemStack;
import shadowmage.ancient_framework.client.gui.elements.GuiScrollableArea;
import shadowmage.ancient_framework.client.gui.elements.GuiString;
import shadowmage.ancient_framework.client.gui.elements.IGuiElement;
import shadowmage.ancient_framework.common.config.Statics;
import shadowmage.ancient_structures.common.manager.BlockDataManager;

public class GuiBlockSelection extends GuiContainerAdvanced
{

GuiStructureScanner parent;
GuiScrollableArea area;
GuiButtonSimple doneButton;

public GuiBlockSelection(GuiStructureScanner parent)
  {
  super(parent.container);
  this.parent = parent;
  }

@Override
public int getXSize()
  {
  return 256;
  }

@Override
public int getYSize()
  {
  return 240;
  }

@Override
public String getGuiBackGroundTexture()
  {
  return Statics.TEXTURE_PATH+"gui/guiBackgroundLarge.png";
  }

@Override
public void renderExtraBackGround(int mouseX, int mouseY, float partialTime)
  {

  }

@Override
public void updateScreenContents()
  {

  }

@Override
public void setupControls()
  {
  this.guiElements.clear();
  this.doneButton = (GuiButtonSimple) this.addGuiButton(0, 35, 18, "Done").updateRenderPos(256-35-10, 10);
  this.guiElements.put(1, new GuiString(1, this, 220, 10, "Select Target Blocks: ").updateRenderPos(8, 10));  
  this.guiElements.put(2, this.area = new GuiScrollableArea(2, this, 8, 18+10+4, getXSize()-16, getYSize() - 20-18-4, 8));
  
  int totalHeight = 0;
  int elementNum = 3;
  Block block;
  for(int i = 0; i <256; i++)
    {
    block = Block.blocksList[i];
    if(block==null){continue;}
    addBlock(elementNum, totalHeight, 0, block); 
    totalHeight += 18;
    }
  
  area.updateTotalHeight(totalHeight);
  }

private void addBlock(int elementNum, int y, int x, Block block)
  {
  GuiString label = new GuiString(elementNum, area, 100, 12, BlockDataManager.getBlockName(block));
  label.updateRenderPos(x, y);
  area.elements.add(label);
  
  GuiItemStack item = new GuiItemStack(elementNum, area);
  item.updateRenderPos(x+110, y);
  item.isClickable = false;
  item.setItemStack(new ItemStack(block));
  area.elements.add(item);
  
  GuiCheckBoxSimple box = new GuiCheckBoxSimple(elementNum, area, 16, 16);
  box.updateRenderPos(x+130, y);
  area.elements.add(box);
  
  blockBoxes.put(box, block);
  }

private HashMap<GuiCheckBoxSimple, Block> blockBoxes = new HashMap<GuiCheckBoxSimple, Block>();

@Override
public void updateControls()
  {

  }

@Override
public void onElementActivated(IGuiElement element)
  {
  if(element==this.doneButton)
    {
    List<String> selectedBlocks = new ArrayList<String>();
    for(GuiCheckBoxSimple box : this.blockBoxes.keySet())
      {
      if(box.checked)
        {
        selectedBlocks.add(BlockDataManager.getBlockName(this.blockBoxes.get(box)));
        }
      }
    parent.onBlockSelectionCallback(selectedBlocks);
    Minecraft.getMinecraft().displayGuiScreen(parent);
    }
  }

}
