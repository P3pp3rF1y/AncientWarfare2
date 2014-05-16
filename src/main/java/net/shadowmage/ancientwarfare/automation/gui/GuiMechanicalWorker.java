package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.automation.container.ContainerMechanicalWorker;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeItemSlots;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.ProgressBar;

public class GuiMechanicalWorker extends GuiContainerBase
{

Label energyLabel;

ProgressBar pg;
ProgressBar pg1;
ContainerMechanicalWorker container;

CompositeScrolled comp;

public GuiMechanicalWorker(ContainerBase par1Container)
  {
  super(par1Container, 178, ((ContainerMechanicalWorker)par1Container).guiHeight, defaultBackground);
  container = (ContainerMechanicalWorker)par1Container;
  }

@Override
public void initElements()
  {
  pg1 = new ProgressBar(8, 8, 178-16, 10);
  addGuiElement(pg1);
  
  energyLabel = new Label(8,8,StatCollector.translateToLocal("guistrings.automation.energy_stored")+": "+container.energy);
  addGuiElement(energyLabel);
  
  pg = new ProgressBar(8, 8+10+18+4, 178-16, 16);
  addGuiElement(pg);
  
//  comp = new CompositeItemSlots(178, 0, 100, 100, this);
//  addGuiElement(comp);
//  
//  ItemSlot slot;
//  int x1, y1, xPos, yPos;
//  for(int i = 0; i < 1000; i++)
//    {
//    x1 = i%4;
//    y1 = i/4;
//    xPos = x1*18+8;
//    yPos = y1*18+8;
//    slot = new ItemSlot(xPos, yPos, new ItemStack(Items.stick), this);
//    comp.addGuiElement(slot);
//    }
//  comp.setAreaSize((1000/4)*18);
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
  
  progress = (float)container.energy / (float)container.tile.getMaxEnergy();
  pg1.setProgress(progress);
  }

}
