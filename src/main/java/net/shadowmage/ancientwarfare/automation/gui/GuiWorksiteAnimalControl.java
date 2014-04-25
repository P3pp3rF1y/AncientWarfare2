package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteAnimalControl;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteAnimalFarm;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

public class GuiWorksiteAnimalControl extends GuiContainerBase
{

ContainerWorksiteAnimalControl container;
WorkSiteAnimalFarm worksite;

NumberInput pigCount, sheepCount, cowCount, chickenCount;

public GuiWorksiteAnimalControl(ContainerBase par1Container)
  {
  super(par1Container, 168, 48+16, defaultBackground);
  container = (ContainerWorksiteAnimalControl)par1Container;
  }

@Override
public void initElements()
  {  
  Label label;
  
  label = new Label(8, 8, StatCollector.translateToLocal("guistrings.automation.max_pigs"));
  addGuiElement(label);
  pigCount = new NumberInput(130, 8, 30, this.container.maxPigs, this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      container.maxPigs = (int)value;
      }
    };
  pigCount.setIntegerValue();
  addGuiElement(pigCount);
  
  label = new Label(8, 20, StatCollector.translateToLocal("guistrings.automation.max_sheep"));
  addGuiElement(label);
  sheepCount = new NumberInput(130, 20, 30, this.container.maxSheep, this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      container.maxSheep = (int)value;
      }
    };
  sheepCount.setIntegerValue();
  addGuiElement(sheepCount);
  
  label = new Label(8, 32, StatCollector.translateToLocal("guistrings.automation.max_cows"));
  addGuiElement(label);
  cowCount = new NumberInput(130, 32, 30, this.container.maxCows, this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      container.maxCows = (int)value;
      }
    };
  cowCount.setIntegerValue();
  addGuiElement(cowCount);
  
  label = new Label(8, 44, StatCollector.translateToLocal("guistrings.automation.max_chickens"));
  addGuiElement(label);
  chickenCount = new NumberInput(130, 44, 30, this.container.maxChickens, this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      container.maxChickens = (int)value;
      }
    };
  chickenCount.setIntegerValue();
  addGuiElement(chickenCount);
  }

@Override
public void setupElements()
  {
  pigCount.setValue(container.maxPigs);
  sheepCount.setValue(container.maxSheep);
  cowCount.setValue(container.maxCows);
  chickenCount.setValue(container.maxChickens);
  }

@Override
protected boolean onGuiCloseRequested()
  {
  container.sendSettingsToServer();
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_INVENTORY, worksite.xCoord, worksite.yCoord, worksite.zCoord);
  return false;
  }

}
