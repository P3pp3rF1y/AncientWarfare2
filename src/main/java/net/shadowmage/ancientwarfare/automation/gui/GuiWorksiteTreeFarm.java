package net.shadowmage.ancientwarfare.automation.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;

public class GuiWorksiteTreeFarm extends GuiWorksiteBase
{

public GuiWorksiteTreeFarm(ContainerBase par1Container)
  {
  super(par1Container);
  }

@Override
public void initElements()
  {
  addLabels();
  addSideSelectButton();
  addTargetSelectButton();
  }

@Override
public void setupElements()
  {

  }

}
