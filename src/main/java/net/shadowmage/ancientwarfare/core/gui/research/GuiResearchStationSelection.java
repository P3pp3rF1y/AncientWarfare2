package net.shadowmage.ancientwarfare.core.gui.research;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketResearchUpdate;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;

import org.lwjgl.input.Mouse;

public class GuiResearchStationSelection extends GuiContainerBase
{

GuiResearchStation parent;

CompositeScrolled queueArea;
CompositeScrolled selectionArea;

private HashMap<GuiElement, Integer> elementToGoal = new HashMap<GuiElement, Integer>();

public GuiResearchStationSelection(GuiResearchStation parent, int x, int y)
  {
  super(parent.container, 480, 240, defaultBackground);
  this.parent = parent;
  Mouse.setCursorPosition(x, y);
  }

@Override
public void initElements()
  {  
  queueArea = new CompositeScrolled(0, 40, 240, 200);
  addGuiElement(queueArea);
  
  selectionArea = new CompositeScrolled(240, 40, 240, 200);
  addGuiElement(selectionArea);
  }

@Override
public void setupElements()
  {
  elementToGoal.clear();
  int goal;
  goal = parent.container.currentGoal;
  
  Button button;
  
  int totalHeight = 8;    
  queueArea.setAreaSize(totalHeight+8);
  
  if(goal>=0)
    {
    totalHeight = addQueuedGoal(totalHeight, goal);
    }
  
  for(Integer g : parent.container.queuedResearch)
    {
    totalHeight = addQueuedGoal(totalHeight, g);
    } 
  
  totalHeight = 8;
  selectionArea.setAreaSize(totalHeight);
  }

private int addQueuedGoal(int totalHeight, int goalNumber)
  {
  ResearchGoal g = ResearchGoal.getGoal(goalNumber);
  if(g==null){return totalHeight;}
  
  Label label = new Label(8, totalHeight+1, g.getName());
  queueArea.addGuiElement(label);
  elementToGoal.put(label, Integer.valueOf(goalNumber));
  
  Button button = new Button(240-8-12, totalHeight, 12, 12, "-")
    {
    @Override
    protected void onPressed()
      {
      PacketResearchUpdate pkt = new PacketResearchUpdate(parent.container.researcherName, elementToGoal.get(this), false, false);
      NetworkHandler.sendToServer(pkt);
      }
    };  
  queueArea.addGuiElement(button);
  elementToGoal.put(button, Integer.valueOf(goalNumber));
  return totalHeight+12;
  }



@Override
protected boolean onGuiCloseRequested()
  {  
  parent.refreshGui();
  parent.container.setGui(parent);
  parent.container.addSlots();
  Minecraft.getMinecraft().displayGuiScreen(parent);
  return false;  
  }
  
}
