package net.shadowmage.ancientwarfare.core.gui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.config.Statics;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.interfaces.IContainerGuiCallback;
import net.shadowmage.ancientwarfare.core.interfaces.ISlotClickCallback;
import net.shadowmage.ancientwarfare.core.interfaces.ITooltipRenderer;
import net.shadowmage.ancientwarfare.core.interfaces.IWidgetSelection;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketGui;
import net.shadowmage.ancientwarfare.core.util.RenderTools;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class GuiContainerBase extends GuiContainer implements IContainerGuiCallback, ITooltipRenderer, ISlotClickCallback, IWidgetSelection
{

protected static final String defaultBackground = "guiBackgroundLarge.png";


private static LinkedList<Viewport> viewportStack = new LinkedList<Viewport>();

private boolean widgetSelected = false;
protected boolean shouldCloseOnVanillaKeys = true;
private float partialRenderTick = 0.f;
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
public void handleTooltipRender(ItemStack stack)
  {
  int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
  int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
  this.tooltipStack = stack;
  this.tooltipX = x;
  this.tooltipY = y;
  }

protected void clearElements()
  {
  this.elements.clear();
  }

/**
 * send the input nbt-tag to the server-side container
 * @param tag
 */
protected void sendDataToContainer(NBTTagCompound tag)
  {
  PacketGui pkt = new PacketGui();
  pkt.packetData = tag;
  NetworkHandler.sendToServer(pkt);
  }

protected void addGuiElement(GuiElement element)
  {
  this.elements.add(element);
  }

protected void removeGuiElement(GuiElement element)
  {
  this.elements.remove(element);
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
  
  int type = button >= 0 ? (state ? Listener.MOUSE_DOWN : Listener.MOUSE_UP) : wheel!=0 ? Listener.MOUSE_WHEEL : Listener.MOUSE_MOVED; 
  ActivationEvent evt = new ActivationEvent(type, button, state, x, y, wheel);
  for(GuiElement element : this.elements)
    {
    element.handleMouseInput(evt);
    }
  }

@Override
public void handleKeyboardInput()
  {
  int key = Keyboard.getEventKey();
  boolean state = Keyboard.getEventKeyState();
  char ch = Keyboard.getEventCharacter();
  
  ActivationEvent evt = new ActivationEvent(state ? Listener.KEY_DOWN : Listener.KEY_UP, key, ch, state);  
  for(GuiElement element : this.elements)
    {
    element.handleKeyboardInput(evt);
    }
  
  if(!widgetSelected && state)
    {
    if(key == 87)
      {
      this.mc.toggleFullscreen();
      return;
      }
    boolean sendTyped = true;
    if(!shouldCloseOnVanillaKeys && (key == 1 || key == this.mc.gameSettings.keyBindInventory.getKeyCode()))
      {
      sendTyped = false;
      }
    if(sendTyped)
      {
      this.keyTyped(ch, key);      
      }
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
    element.updateGuiPosition(guiLeft, guiTop);
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
    Minecraft.getMinecraft().renderEngine.bindTexture(backgroundTexture);
    RenderTools.renderQuarteredTexture(256, 256, 0, 0, 256, 256, width/2 - xSize/2, height/2 - ySize/2, xSize, ySize);
    }
  }

@Override
public void drawScreen(int par1, int par2, float par3)
  {
  this.partialRenderTick = par3;
  super.drawScreen(par1, par2, par3);  
  if(tooltipStack!=null)
    {
    super.renderToolTip(tooltipStack, tooltipX, tooltipY);
    tooltipStack = null;
    }
  }

protected void drawGuiContainerForegroundLayer(int par1, int par2)
  {
  GL11.glDisable(GL11.GL_LIGHTING);
  GL11.glPushMatrix();
  GL11.glTranslatef(-guiLeft, -guiTop, 0);
  for(GuiElement element : elements)
    {
    element.render(par1, par2, partialRenderTick);
    }   
  GL11.glPopMatrix();
  GL11.glEnable(GL11.GL_LIGHTING);
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
 * Sub-classes should implement this method to add initial gui elements.<br>
 * Only called a single time, shortly after construction,
 * but before any rendering or other update methods are called
 */
public abstract void initElements();

/**
 * sub-classes should implement this method to setup/change any elements that need adjusting when the gui is initialized<br>
 * any elements that are positioned outside of the gui-window space will need their positions updated by calling element.setPosition(xPos, yPos)
 * as they reference internal position relative to the guiLeft / guiTop values from this gui (which are passed in and updated directly after setupElements() is called)<br>
 * Always called at least once, directly after {@link #initElements()}
 */
public abstract void setupElements();

/**
 * sub-classes should override this method to handle any expected packet data
 */
@Override
public void handlePacketData(Object data)
  {
  
  }

/**
 * TODO implement basic handling of slot-click action for widget-based slots
 */
@Override
public void onSlotClicked(IInventory inventory, int slotIndex, int button)
  {
  ((ContainerBase)inventorySlots).onSlotClicked(inventory, slotIndex, button);
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

@Override
public void onWidgetSelected(GuiElement element)
  {
  this.widgetSelected = true;
  }

@Override
public void onWidgetDeselected(GuiElement element)
  {
  this.widgetSelected = false;
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
public final int type;
public int key;
public boolean keyEvent;
public int mButton;
public boolean state;
public char ch;
public int mx;
public int my;
public int mw;//mousewheel delta movement
private ActivationEvent(int type, int button, boolean state, int mx, int my, int mw)
  {
  this.type = type;
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


/**
 * Push a new scissors-test viewport onto the stack.<br>
 * If this viewport would extend outside of the currently-set viewport, it
 * will be truncated to fit inside of the existing viewport
 * @param x
 * @param y
 * @param w
 * @param h
 */
public static void pushViewport(int x, int y, int w, int h)
  {
  int tlx, tly, brx, bry;
  tlx = x;
  tly = y;
  brx = x + w;
  bry = y + h;
  
  Viewport p = viewportStack.peek();
  if(p!=null)
    {
    if(tlx<p.x){tlx = p.x;}
    if(brx>p.x+p.w){brx = p.x + p.w;}
    if(tly<p.y){tly = p.y;}
    if(bry>p.y+p.h){bry = p.y+p.h;}    
    }
  x = tlx;
  y = tly;
  w = brx - tlx;
  h = bry - tly;
  
  Minecraft mc = Minecraft.getMinecraft();
  ScaledResolution scaledRes = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
  int guiScale = scaledRes.getScaleFactor();
  GL11.glEnable(GL11.GL_SCISSOR_TEST);  
  
  GL11.glScissor(x*guiScale, mc.displayHeight - y*guiScale - h*guiScale, w*guiScale, h*guiScale);
  
  viewportStack.push(new Viewport(x, y, w, h));
  }

/**
 * pop a scissors-test viewport off of the stack
 */
public static void popViewport()
  {  
  Viewport p = viewportStack.poll();
  if(p==null)
    {
    GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
  p = viewportStack.peek();
  if(p!=null)
    {
    Minecraft mc = Minecraft.getMinecraft();
    ScaledResolution scaledRes = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
    int guiScale = scaledRes.getScaleFactor();
    GL11.glEnable(GL11.GL_SCISSOR_TEST);  
    
    GL11.glScissor(p.x*guiScale, mc.displayHeight - p.y*guiScale - p.h*guiScale, p.w*guiScale, p.h*guiScale);    
    }
  else
    {
    GL11.glDisable(GL11.GL_SCISSOR_TEST);    
    }
  }

/**
 * class used to represent a currently drawable portion of the screen.
 * Used in a stack for figuring out what composites may draw where
 * @author John
 *
 */
private static class Viewport
{
int x, y, w, h;
private Viewport(int x, int y, int w, int h)
  {
  this.x = x;
  this.y = y;
  this.h = h;
  this.w = w;
  }
}

}
