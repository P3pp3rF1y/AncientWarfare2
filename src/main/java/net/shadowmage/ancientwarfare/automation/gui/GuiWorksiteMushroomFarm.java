package net.shadowmage.ancientwarfare.automation.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;

public class GuiWorksiteMushroomFarm extends GuiWorksiteBase
{

public GuiWorksiteMushroomFarm(ContainerBase par1Container)
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
