package net.shadowmage.ancientwarfare.core.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.interfaces.IContainerGuiCallback;

public class GuiContainerBase extends GuiContainer implements IContainerGuiCallback
{

private boolean shouldUpdate = false;
private List<GuiElement> elements = new ArrayList<GuiElement>();

private ItemStack tooltipStack;
private int tooltipX;
private int tooltipY;

public GuiContainerBase(ContainerBase par1Container)
  {
  super(par1Container);
  par1Container.setGui(this);
  }

@Override
public void handleMouseInput()
  {
  // TODO
  super.handleMouseInput();
  }

@Override
public void handleKeyboardInput()
  {
  // TODO
  super.handleKeyboardInput();
  }

@Override
public void initGui()
  {
  // TODO
  super.initGui();
  }

@Override
public void updateScreen()
  {
  // TODO
  super.updateScreen();
  }

@Override
protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
  {
  for(GuiElement element : elements)
    {
    //TODO create elements, render
    }
  if(tooltipStack!=null)
    {
    super.renderToolTip(tooltipStack, tooltipX, tooltipY);
    }
  }

@Override
public void refreshGui()
  {
  this.shouldUpdate = true;
  }

@Override
public void handlePacketData(Object data)
  {
  
  }

@Override
protected void renderToolTip(ItemStack stack, int x, int y)
  {
  tooltipStack = stack;
  tooltipX = x;
  tooltipY = y;  
  }
}
