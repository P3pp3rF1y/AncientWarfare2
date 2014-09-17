package net.shadowmage.ancientwarfare.core.gui.research;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Tooltip;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketResearchUpdate;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;

import org.lwjgl.input.Mouse;

public class GuiResearchStationSelection extends GuiContainerBase
{

GuiResearchStation parent;

CompositeScrolled queueArea;
CompositeScrolled selectionArea;

public GuiResearchStationSelection(GuiResearchStation parent, int x, int y)
  {
  super(parent.container, 400, 240, defaultBackground);
  this.parent = parent;
  Mouse.setCursorPosition(x, y);
  }

@Override
public void initElements()
  {  
  queueArea = new CompositeScrolled(0, 40, 200, 200);
  addGuiElement(queueArea);
  
  selectionArea = new CompositeScrolled(200, 40, 200, 200);
  addGuiElement(selectionArea);
  
  Label label = new Label(8, 8, "guistrings.research.queued_research");
  addGuiElement(label);
  
  label = new Label(240+8, 8, "guistrings.research.learnable_research");
  addGuiElement(label);
  }

@Override
public void setupElements()
  {
  selectionArea.clearElements();
  queueArea.clearElements();
  int goal;
  goal = parent.container.currentGoal;
  
  int totalHeight = 8;    
  
  if(goal>=0)
    {
    totalHeight = addQueuedGoal(totalHeight, goal, false);
    }
  
  for(Integer g : parent.container.queuedResearch)
    {
    totalHeight = addQueuedGoal(totalHeight, g, true);
    } 

  queueArea.setAreaSize(totalHeight+8);
  
  
  totalHeight = 8;  
  
  if(parent.container.researcherName!=null)
    {
    for(Integer g : ResearchTracker.instance().getResearchableGoals(player.worldObj, parent.container.researcherName))
      {
      totalHeight = addSelectableGoal(totalHeight, g);
      }    
    }
  
  selectionArea.setAreaSize(totalHeight+8);
  }

private int addQueuedGoal(int totalHeight, int goalNumber, boolean removeButton)
  {
  ResearchGoal g = ResearchGoal.getGoal(goalNumber);
  if(g==null){return totalHeight;}
  String name = StatCollector.translateToLocal(g.getName());
  if(!removeButton)
    {
    name = name + " (" + StatCollector.translateToLocal("guistrings.research.current_goal") + ")";
    }
  
  Label label = new Label(8, totalHeight+1, name);
  queueArea.addGuiElement(label);
  
  Tooltip selectableGoalTooltip = new Tooltip(110, 75);
  selectableGoalTooltip.addTooltipElement(new Label(0, 0, StatCollector.translateToLocal("guistrings.research.research_time")+": "+g.getTotalResearchTime()));
  selectableGoalTooltip.addTooltipElement(new Label(0, 10, "guistrings.research.resources_needed"));
  int x = 0, y = 0;
  for(ItemStack stack : g.getResources())
    {
    selectableGoalTooltip.addTooltipElement(new ItemSlot(x*18, y*18 + 20, stack, this));
    x++;
    if(x > 2)
      {
      x = 0;
      y++;
      }
    }
  label.setTooltip(selectableGoalTooltip);
  
  if(removeButton)
    {
    GoalButton button = new GoalButton(200-8-12-12, totalHeight, 12, 12, g, false);  
    queueArea.addGuiElement(button);
    button.setTooltip(selectableGoalTooltip);
    }
  return totalHeight+12;
  }

private int addSelectableGoal(int totalHeight, int goalNumber)
  {
  ResearchGoal g = ResearchGoal.getGoal(goalNumber);
  if(g==null){return totalHeight;}

  Label label = new Label(8, totalHeight+1, g.getName());
  selectionArea.addGuiElement(label);
  
  GoalButton button = new GoalButton(200-8-12-12, totalHeight, 12, 12, g, true);  
  selectionArea.addGuiElement(button);
  
  Tooltip selectableGoalTooltip = new Tooltip(110, 75);
  selectableGoalTooltip.addTooltipElement(new Label(0, 0, StatCollector.translateToLocal("guistrings.research.research_time")+": "+g.getTotalResearchTime()));
  selectableGoalTooltip.addTooltipElement(new Label(0, 10, "guistrings.research.resources_needed"));
  int x = 0, y = 0;
  for(ItemStack stack : g.getResources())
    {
    selectableGoalTooltip.addTooltipElement(new ItemSlot(x*18, y*18 + 20, stack, this));
    x++;
    if(x > 2)
      {
      x = 0;
      y++;
      }
    }
  label.setTooltip(selectableGoalTooltip);
  button.setTooltip(selectableGoalTooltip);
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

private class GoalButton extends Button
{
ResearchGoal goal;
boolean add;
public GoalButton(int topLeftX, int topLeftY, int width, int height, ResearchGoal goal, boolean add)
  {
  super(topLeftX, topLeftY, width, height, add ? "+" : "-");
  this.goal = goal;
  this.add = add;
  }

@Override
protected void onPressed()
  {
  PacketResearchUpdate pkt = new PacketResearchUpdate(parent.container.researcherName, goal.getId(), add, false);
  NetworkHandler.sendToServer(pkt);
  }
}
  
}
