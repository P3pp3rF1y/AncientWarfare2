package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.automation.container.ContainerTorqueGeneratorSterling;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.ProgressBar;

public class GuiTorqueGeneratorSterling extends GuiContainerBase
{

Label energyLabel;

ProgressBar pg;
ProgressBar pg1;
ContainerTorqueGeneratorSterling container;

CompositeScrolled comp;

public GuiTorqueGeneratorSterling(ContainerBase par1Container)
  {
  super(par1Container, 178, ((ContainerTorqueGeneratorSterling)par1Container).guiHeight, defaultBackground);
  container = (ContainerTorqueGeneratorSterling)par1Container;
  }

@Override
public void initElements()
  {
  pg1 = new ProgressBar(8, 8, 178-16, 10);
  addGuiElement(pg1);
  
  energyLabel = new Label(8,8, StatCollector.translateToLocal("guistrings.automation.energy_stored")+": "+container.energy);
  addGuiElement(energyLabel);
  
  pg = new ProgressBar(8, 8+10+18+4, 178-16, 16);
  addGuiElement(pg);
  }

@Override
public void setupElements()
  {
  energyLabel.setText(StatCollector.translateToLocal("guistrings.automation.energy_stored")+": "+container.energy);
  float progress = 0;
  if(container.burnTimeBase>0)
    {
    progress = (float)container.burnTime / (float)container.burnTimeBase;
    }
  pg.setProgress(progress);
  
  progress = (float)container.energy / (float)container.tile.getMaxTorque(null);
  pg1.setProgress(progress);
  }

}
