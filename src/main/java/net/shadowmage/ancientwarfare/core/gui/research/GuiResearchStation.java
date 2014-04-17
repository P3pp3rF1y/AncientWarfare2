package net.shadowmage.ancientwarfare.core.gui.research;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.container.ContainerResearchStation;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Tooltip;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;

import org.lwjgl.input.Mouse;

public class GuiResearchStation extends GuiContainerBase
{

ContainerResearchStation container;
Label researcherLabel;
Label researchGoalLabel;

public GuiResearchStation(ContainerBase par1Container)
  {
  super(par1Container, 256, 240, defaultBackground);
  container = (ContainerResearchStation) par1Container;
  container.setGui(this);
  }

@Override
public void initElements()
  {
  String name = container.researcherName ==null? StatCollector.translateToLocal("guistrings.research.no_researcher") : container.researcherName;
  researcherLabel = new Label(8, 8, name);  
  addGuiElement(researcherLabel);
  
  name = "guistrings.research.no_research"; 
  int goalNumber = container.currentGoal;
  if(goalNumber>=0)
    {
    ResearchGoal g = ResearchGoal.getGoal(goalNumber);
    if(g!=null)
      {
      name = g.getName();
      }
    }
  researchGoalLabel = new Label(8, 8+18*2, StatCollector.translateToLocal(name));
  addGuiElement(researchGoalLabel);
  
  Button button = new Button(256-8-55, 8+12+4, 55, 12, StatCollector.translateToLocal("guistrings.research.research_queue"))
    {
    @Override
    protected void onPressed()
      {
      container.removeSlots();
      Minecraft.getMinecraft().displayGuiScreen(new GuiResearchStationSelection(GuiResearchStation.this, Mouse.getX(), Mouse.getY()));
      }
    };  
  addGuiElement(button);
  }

@Override
public void setupElements()
  {
  String name = container.researcherName ==null? StatCollector.translateToLocal("guistrings.research.no_researcher") : container.researcherName;
  researcherLabel.setText(name);
  
  name = "guistrings.research.no_research"; 
  int goalNumber = container.currentGoal;
  if(goalNumber>=0)
    {
    ResearchGoal g = ResearchGoal.getGoal(goalNumber);
    if(g!=null)
      {
      name = g.getName();
      }
    }
  researchGoalLabel.setText(name);
  }


}
