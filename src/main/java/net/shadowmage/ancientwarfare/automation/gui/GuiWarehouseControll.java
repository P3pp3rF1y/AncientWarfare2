package net.shadowmage.ancientwarfare.automation.gui;

import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;

public class GuiWarehouseControll extends GuiContainerBase
{

public GuiWarehouseControll(ContainerBase par1Container)
  {
  super(par1Container, 256, 240, defaultBackground);
  }

@Override
public void initElements()
  {

  }

@Override
public void setupElements()
  {

  }

private static final class FBOWidget extends GuiElement
{

static int fboNumber = -1;

public FBOWidget(int topLeftX, int topLeftY)
  {
  super(topLeftX, topLeftY);
  if(fboNumber<0)
    {
    fboNumber = GL30.glGenFramebuffers();
    }
  
  }

@Override
public void render(int mouseX, int mouseY, float partialTick)
  {
  
  }

}

}
