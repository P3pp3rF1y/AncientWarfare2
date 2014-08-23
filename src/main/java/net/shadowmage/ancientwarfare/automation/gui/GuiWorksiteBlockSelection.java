package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteBlockSelection;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class GuiWorksiteBlockSelection extends GuiContainerBase
{

Label label ;
ContainerWorksiteBlockSelection container;

/**
 * Move bounds buttons
 */
Button mNorth, mSouth, mWest, mEast, mUp, mDown;
/**
 * Expand bounds buttons
 */
Button eNorth, eSouth, eWest, eEast, eUp, eDown;
/**
 * Shrink bounds buttons
 */
Button sNorth, sSouth, sWest, sEast, sUp, sDown;

int leftRowX, rightRowX, topRowY, bottomRowY;

public GuiWorksiteBlockSelection(ContainerBase par1Container)
  {
  super(par1Container, 256, 240, defaultBackground);
  container = (ContainerWorksiteBlockSelection)par1Container;
  BlockPosition min = container.worksite.getWorkBoundsMin();
  BlockPosition max = container.worksite.getWorkBoundsMax();
  int xSize = (max.x-min.x)+1;
  int zSize = (max.z-min.z)+1;
  this.xSize = (xSize*12) + 8 + 8 + 12 + 12 + 24;
  this.ySize = (zSize*12) + 8 + 8 + 12 + 12 + 24;  
  }

@Override
public void initElements()
  {
 
  
  label = new Label(0, -12, "W=Worksite, X=Work Target");
  addGuiElement(label);
  }

@Override
public void setupElements()
  {
  clearElements();
  addGuiElement(label);
  label.setRenderPosition(0, -12);
  BlockPosition min = container.worksite.getWorkBoundsMin();
  BlockPosition max = container.worksite.getWorkBoundsMax();
  int xSize = (max.x-min.x)+1;
  int zSize = (max.z-min.z)+1;
  
  int workX = container.worksite.xCoord - min.x;
  int workZ = container.worksite.zCoord - min.z;
  
  int tlx = 8+12+12;
  int tly = 8+12+12;
  
  Button siteButton = new Button(tlx + workX*12, tly + workZ*12, 12, 12, "W");  
  addGuiElement(siteButton);
  
  BlockPosition testPos = new BlockPosition();
  WorkSelectionButton button;
  for(int x = 0; x<xSize; x++)
    {
    for(int z = 0; z<zSize; z++)
      {
      testPos.reassign(x + min.x, min.y, z + min.z);
      button = new WorkSelectionButton(tlx + x*12, tly+z*12, x+min.x, z+min.z, container.targetBlocks.contains(testPos));
      addGuiElement(button);      
      }
    }  
  
  }

@Override
protected boolean onGuiCloseRequested()
  {
  container.sendTargetsToServer();
  NBTTagCompound tag = new NBTTagCompound();
  tag.setBoolean("closeGUI", true);
  sendDataToContainer(tag);
  return false;
  }

private class WorkSelectionButton extends Button
{

boolean include;
BlockPosition pos;

public WorkSelectionButton(int topLeftX, int topLeftY, int x, int z, boolean include)
  {
  super(topLeftX, topLeftY, 12, 12, include ? "X" : " ");
  pos = new BlockPosition(x, container.worksite.getWorkBoundsMin().y ,z);
  this.include = include;
  }

@Override
protected void onPressed()
  {
  this.include = !include;
  this.setText(include ? "X" : " ");
  if(include)
    {
    container.targetBlocks.add(pos.copy());    
    }
  else
    {
    container.removeTarget(pos);
    }
//  container.sendTargetsToServer();
  }
}

}
