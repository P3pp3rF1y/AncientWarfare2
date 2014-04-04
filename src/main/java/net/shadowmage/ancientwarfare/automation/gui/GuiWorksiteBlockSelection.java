package net.shadowmage.ancientwarfare.automation.gui;

import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteBlockSelection;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class GuiWorksiteBlockSelection extends GuiContainerBase
{


ContainerWorksiteBlockSelection container;
public GuiWorksiteBlockSelection(ContainerBase par1Container)
  {
  super(par1Container, 256, 240, defaultBackground);
  container = (ContainerWorksiteBlockSelection)par1Container;
  BlockPosition min = container.worksite.getWorkBoundsMin();
  BlockPosition max = container.worksite.getWorkBoundsMax();
  int xSize = (max.x-min.x)+1;
  int zSize = (max.z-min.z)+1;
  this.xSize = (xSize*12) + 8 + 8 + 12 + 12;
  this.ySize = (zSize*12) + 8 + 8 + 12 + 12;  
  }

@Override
public void initElements()
  {
  BlockPosition min = container.worksite.getWorkBoundsMin();
  BlockPosition max = container.worksite.getWorkBoundsMax();
  int xSize = (max.x-min.x)+1;
  int zSize = (max.z-min.z)+1;
  
  int workX = container.worksite.xCoord - min.x;
  int workZ = container.worksite.zCoord - min.z;
  
  int tlx = 8+12;
  int tly = 8+12;
  
  Button siteButton = new Button(tlx + workX*12, tly + workZ*12, 12, 12, "W");  
  addGuiElement(siteButton);
  
  BlockPosition testPos = new BlockPosition();
  WorkSelectionButton button;
  for(int x = 0; x<xSize; x++)
    {
    for(int z = 0; z<zSize; z++)
      {
      testPos.reassign(x+min.x, min.y, z+min.z);
      button = new WorkSelectionButton(tlx + x*12, tly+z*12, x+min.x, z+min.z, container.worksite.getUserSetTargets().contains(testPos));
      addGuiElement(button);      
      }
    }  
  
  label = new Label(0, -12, "W=Worksite, X=Work Target");
  addGuiElement(label);
  }

Label label ;

@Override
public void setupElements()
  {
  label.setRenderPosition(0, -12);
  }

@Override
public void onGuiClosed()
  {
  super.onGuiClosed();
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_INVENTORY, container.worksite.xCoord, container.worksite.yCoord, container.worksite.zCoord);
  }

private class WorkSelectionButton extends Button
{

boolean include;
BlockPosition pos;

public WorkSelectionButton(int topLeftX, int topLeftY, int x, int z, boolean include)
  {
  super(topLeftX, topLeftY, 12, 12, include ? "X" : " ");
  pos = new BlockPosition(x,0,z);
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
  container.sendTargetsToServer();
  }
}

}
