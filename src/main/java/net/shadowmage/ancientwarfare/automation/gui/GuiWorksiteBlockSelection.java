package net.shadowmage.ancientwarfare.automation.gui;

import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteBlockSelection;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class GuiWorksiteBlockSelection extends GuiContainerBase
{


ContainerWorksiteBlockSelection container;
public GuiWorksiteBlockSelection(ContainerBase par1Container)
  {
  super(par1Container, 256, 240, defaultBackground);
  container = (ContainerWorksiteBlockSelection)par1Container;
  }

@Override
public void initElements()
  {
  BlockPosition min = container.worksite.getWorkBoundsMin();
  BlockPosition max = container.worksite.getWorkBoundsMax();
  int xSize = (max.x-min.x)+1;
  int zSize = (max.z-min.z)+1;
  
  int workX = min.x - container.worksite.xCoord;
  int workZ = min.z - container.worksite.zCoord;
  
  int tlx = 8+12;
  int tly = 8+12;
  
  BlockPosition testPos = new BlockPosition();
  WorkSelectionButton button;
  for(int x = 0; x<xSize; x++)
    {
    for(int z = 0; z<zSize; z++)
      {
      testPos.reassign(x+min.x, 0, z+min.z);
      button = new WorkSelectionButton(tlx + x*12, tly+z*12, x+min.x, z+min.z, container.worksite.getUserSetTargets().contains(testPos));
      addGuiElement(button);
      }
    }  
  }

@Override
public void setupElements()
  {
  
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
  AWLog.logDebug("workbutton clicked: "+pos.x+","+pos.z);
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
