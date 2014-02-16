package net.shadowmage.ancientwarfare.core.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

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

protected int xSize;
protected int ySize;

public GuiContainerBase(ContainerBase par1Container)
  {
  super(par1Container);
  par1Container.setGui(this);
  }

@Override
public void handleMouseInput()
  {
  super.handleMouseInput();
  
  int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
  int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
  int button = Mouse.getEventButton();
  boolean state = Mouse.getEventButtonState();
  int wheel = Mouse.getEventDWheel();
  
  int type = button >= 0 ? (state ? ActionListener.MOUSE_DOWN : ActionListener.MOUSE_UP) : ActionListener.MOUSE_WHEEL; 
  ActivationEvent evt = new ActivationEvent(type, button, state, x, y, wheel);
  for(GuiElement element : this.elements)
    {
    element.handleMouseInput(evt);
    }
  }

@Override
public void handleKeyboardInput()
  {
  super.handleKeyboardInput();
  
  int key = Keyboard.getEventKey();
  boolean state = Keyboard.getEventKeyState();
  ActivationEvent evt = new ActivationEvent(state ? ActionListener.KEY_DOWN : ActionListener.KEY_UP, key, Keyboard.getEventCharacter(), state);
  for(GuiElement element : this.elements)
    {
    element.handleKeyboardInput(evt);
    }
  }

@Override
public void initGui()
  {
  super.initGui();
  for(GuiElement element : this.elements)
    {
    element.updateRenderPosition(guiLeft, guiTop);
    }
  }

@Override
public void updateScreen()
  {
  if(this.shouldUpdate)
    {
    this.initGui();
    this.shouldUpdate = false;
    }
  super.updateScreen();
  }

@Override
protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
  {
  for(GuiElement element : elements)
    {
    element.render(var2, var3, var1);
    }
  if(tooltipStack!=null)
    {
    super.renderToolTip(tooltipStack, tooltipX, tooltipY);
    tooltipStack = null;
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

/**
 * deferred to allow proper render-order, and draw the tooltip on top of everything else
 */
@Override
protected void renderToolTip(ItemStack stack, int x, int y)
  {
  tooltipStack = stack;
  tooltipX = x;
  tooltipY = y;  
  }


public class ActivationEvent
{
int type;
int key;
boolean keyEvent;
int mButton;
boolean state;
char ch;
int mx;
int my;
int mw;//mousewheel delta movement
private ActivationEvent(int type, int button, boolean state, int mx, int my, int mw)
  {
  this.mButton = button;
  this.state = state;
  this.mx = mx;
  this.my = my;
  this.mw = mw;
  this.keyEvent = key!=0;
  }

private ActivationEvent(int type, int key, char character, boolean state)
  {
  this.type = type;
  this.key = key;
  this.ch = character;
  this.state = state;
  }
}
}
