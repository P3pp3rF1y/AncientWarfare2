package net.shadowmage.ancientwarfare.core.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;

/**
 * base GUI Element class
 * subclasses should add their own actionListeners during constructor
 * to handle default implementation details such as playing sound on button
 * click, toggling states or changing highlighting.
 * @author Shadowmage
 *
 */
public abstract class GuiElement
{

private List<Listener> actionListeners = new ArrayList<Listener>();

protected boolean mouseInterface;
protected boolean keyboardInterface;

protected boolean enabled;
protected boolean visible;
protected boolean selected;//isFocused -- for text-input lines / etc

private int topLeftX;
private int topLeftY;

protected int renderX;
protected int renderY;

protected int width;
protected int height;

protected static ResourceLocation backgroundTextureLocation;
protected static ResourceLocation widgetTexture1;
protected static ResourceLocation widgetTexture2;

static
{
backgroundTextureLocation = new ResourceLocation("ancientwarfare", "textures/gui/guiBackgroundLarge.png");
widgetTexture1 = new ResourceLocation("ancientwarfare", "textures/gui/guiButtons1.png");
widgetTexture2 = new ResourceLocation("ancientwarfare", "textures/gui/guiButtons2.png");
}

public GuiElement(int topLeftX, int topLeftY)
  {
  this.topLeftX = topLeftX;
  this.topLeftY = topLeftY;
  this.enabled = true;
  this.visible = true;
  this.selected = true;
  }

public GuiElement(int topLeftX, int topLeftY, int width, int height)
  {
  this(topLeftX, topLeftY);
  this.width = width;
  this.height = height;
  }

/**
 * called to update the internal positioning of this element.
 * needs to be called anytime the parent gui layout is changed (resized / etc)
 * @param guiLeft
 * @param guiTop
 */
public final void updateRenderPosition(int guiLeft, int guiTop)
  {
  renderX = topLeftX + guiLeft;
  renderY = topLeftY + guiTop;
  }

/**
 * called from GUI to process mouse interface.  
 * all functionality should be implemented via ActionListeners
 * even default functionality (e.g. play sound on click, toggle state, etc)
 * @param mouseX
 * @param mouseY
 * @param button
 * @param state
 * @param wheel
 */
public final void handleMouseInput(ActivationEvent evt)
  {
  if(mouseInterface && visible && enabled && !actionListeners.isEmpty())
    {
    for(Listener o : this.actionListeners)
      {
      //   bitwise check of types, if it returns !=0 at least 1 type bit was shared, so should execute
      if((o.type & evt.type)!=0)
        {
        if(!o.onEvent(evt))
          {
          break;
          }
        }
      }
    }
  }

/**
 * called from GUI to process keyboard interface
 * all functionality should be implemented via ActionListeners
 * including default functionality.
 * @param key
 */
public final void handleKeyboardInput(ActivationEvent evt)
  {
  if(keyboardInterface && visible && enabled && selected && !actionListeners.isEmpty())
    {
    for(Listener o : this.actionListeners)
      {
      //   bitwise check of types, if it returns !=0 at least 1 type bit was shared, so should execute
      if((o.type & evt.type)!=0)
        {
        if(!o.onEvent(evt))
          {
          break;
          }
        }
      }
    }
  }

/**
 * add a new event listener to this element
 * if the element is not set to receive those event types
 * --auto-flag the element to receive those events
 * @param listener the new listener to add
 */
public final void addNewListener(Listener listener)
  {
  listener.setElement(this);
  this.actionListeners.add(listener);
  int mouseTypes = Listener.MOUSE_DOWN | Listener.MOUSE_MOVED | Listener.MOUSE_UP | Listener.MOUSE_WHEEL;
  int keyTypes = Listener.KEY_DOWN | Listener.KEY_UP;
  if((listener.type & mouseTypes)!=0)
    {
    this.mouseInterface = true;
    }
  if((listener.type & keyTypes)!=0)
    {
    this.keyboardInterface = true;
    }
  }

public final boolean isMouseOverElement(int mouseX, int mouseY)
  {
  return mouseX >= renderX && mouseX < renderX + width && mouseY >= renderY && mouseY < renderY + height;
  }

public abstract void render(int mouseX, int mouseY, float partialTick);//called from gui to draw this element

/**
 * @param textureWidth texture width
 * @param textureHeight texture height
 * @param texStartX pixel start U
 * @param texStartY pixel start V
 * @param texUsedWidth pixel U width (width of used tex in pixels)
 * @param texUsedHeight pixel V height (height of used tex in pixels)
 * @param renderStartX render position x
 * @param renderStartY render position y
 * @param renderHeight render height
 * @param renderWidth render width
 */
protected void renderQuarteredTexture(int textureWidth, int textureHeight, int texStartX, int texStartY, int texUsedWidth, int texUsedHeight, int renderStartX, int renderStartY, int renderWidth, int renderHeight)
  {
  //perspective percent x, y
  float perX = 1.f / ((float)textureWidth);
  float perY = 1.f / ((float)textureHeight);  
  float texMinX = ((float) texStartX) * perX;
  float texMinY = ((float) texStartY) * perY;
  float texMaxX = (float)(texStartX + texUsedWidth) * perX;
  float texMaxY = (float)(texStartY + texUsedHeight) * perY;
  float halfWidth = (((float) renderWidth) / 2.f) * perX;
  float halfHeight = (((float) renderHeight) / 2.f) * perY;    
  float halfRenderWidth = ((float)renderWidth) * 0.5f;
  float halfRenderHeight = ((float)renderHeight) * 0.5f;
    
  //draw top-left quadrant
  renderTexturedQuad(renderStartX, renderStartY, renderStartX+halfRenderWidth, renderStartY+halfRenderHeight, texMinX, texMinY, texMinX+halfWidth, texMinY+halfHeight);
  
  //draw top-right quadrant
  renderTexturedQuad(renderStartX+halfRenderWidth, renderStartY, renderStartX+halfRenderWidth*2, renderStartY+halfRenderHeight, texMaxX-halfWidth, texMinY, texMaxX, texMinY+halfHeight);
  
//  draw bottom-left quadrant
  renderTexturedQuad(renderStartX, renderStartY+halfRenderHeight, renderStartX+halfRenderWidth, renderStartY+halfRenderHeight*2, texMinX, texMaxY-halfHeight, texMinX+halfWidth, texMaxY);
 
  //draw bottom-right quadrant
  renderTexturedQuad(renderStartX+halfRenderWidth, renderStartY+halfRenderHeight, renderStartX+halfRenderWidth*2, renderStartY+halfRenderHeight*2, texMaxX-halfWidth, texMaxY-halfHeight, texMaxX, texMaxY);
  }

protected void renderTexturedQuad(float x1, float y1, float x2, float y2, float u1, float v1, float u2, float v2)
  {
  GL11.glBegin(GL11.GL_QUADS);
  GL11.glTexCoord2f(u1, v1);
  GL11.glVertex2f(x1, y1);
  GL11.glTexCoord2f(u1, v2);
  GL11.glVertex2f(x1, y2);
  GL11.glTexCoord2f(u2, v2);
  GL11.glVertex2f(x2, y2);
  GL11.glTexCoord2f(u2, v1);
  GL11.glVertex2f(x2, y1);
  GL11.glEnd();
  }

}
