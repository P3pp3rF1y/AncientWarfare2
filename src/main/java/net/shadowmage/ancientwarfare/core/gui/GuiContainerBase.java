package net.shadowmage.ancientwarfare.core.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.config.Statics;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.interfaces.IContainerGuiCallback;

public abstract class GuiContainerBase extends GuiContainer implements IContainerGuiCallback
{

private boolean initDone = false;
private boolean shouldUpdate = false;
private List<GuiElement> elements = new ArrayList<GuiElement>();

private ItemStack tooltipStack;
private int tooltipX;
private int tooltipY;

private String backgroundTextureName;
private ResourceLocation backgroundTexture;

public GuiContainerBase(ContainerBase par1Container, int xSize, int ySize, String backgroundTexture)
  {
  super(par1Container);
  par1Container.setGui(this);
  this.xSize = xSize;
  this.ySize = ySize;
  if(backgroundTexture!=null)
    {
    this.backgroundTextureName = backgroundTexture;
    this.backgroundTexture = new ResourceLocation(Statics.coreModID, "textures/gui/"+backgroundTextureName);    
    }
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
  if(!initDone)
    {
    initElements();
    initDone = true;
    }
  this.setupElements();
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
  if(backgroundTexture!=null)
    {
    //TODO render background
    Minecraft.getMinecraft().renderEngine.bindTexture(backgroundTexture);
    }
  }

@Override
public void drawScreen(int par1, int par2, float par3)
  {
  super.drawScreen(par1, par2, par3);  
  for(GuiElement element : elements)
    {
    element.render(par1, par2, par3);
    }
  if(tooltipStack!=null)
    {
    super.renderToolTip(tooltipStack, tooltipX, tooltipY);
    tooltipStack = null;
    }
  }

/**
 * call this method to enforce a re-initialization of gui at the start of next game tick (not render tick)<br>
 * setupElements() will then be called<br>
 * and all elements will have their position updated
 */
@Override
public void refreshGui()
  {
  this.shouldUpdate = true;
  }

/**
 * sub-classes should implement this method to setup/change any elements that need adjusting when the gui is initialized<br>
 * any elements that are positioned outside of the gui-window space will need their positions updated by calling element.updateRenderPosition(guiLeft, guiTop)
 */
public abstract void setupElements();

public abstract void initElements();

/**
 * sub-classes should override this method to handle any expected packet data
 */
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

/**
 * Action event for gui-widgets.  A single event is sent to all element / listeners for each input event (key up/down, mouse up/down/move/wheel)
 * @author Shadowmage
 */
public class ActivationEvent
{
/**
 * the type of event:<br>
 * 0=Key up <br> 
 * 1=Key down <br>
 * 2=Mouse up <br>
 * 4=Mouse Down <br>  
 * 8=Mouse Wheel 
 */
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
