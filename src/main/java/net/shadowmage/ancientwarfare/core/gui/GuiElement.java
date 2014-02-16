package net.shadowmage.ancientwarfare.core.gui;

import java.util.ArrayList;
import java.util.List;

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

private List<ActionListener> actionListeners = new ArrayList<ActionListener>();

protected boolean mouseInterface;
protected boolean keyboardInterface;

protected boolean enabled;
protected boolean visible;
protected boolean selected;//isFocused -- for text-input lines / etc

private int topLeftX;
private int topLeftY;

protected int renderX;
protected int renderY;

public GuiElement(int topLeftX, int topLeftY)
  {
  this.topLeftX = topLeftX;
  this.topLeftY = topLeftY;
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
  if(mouseInterface && visible && enabled && !actionListeners.isEmpty() && isMouseOverElement(evt.mx, evt.my))
    {
    for(ActionListener o : this.actionListeners)
      {
      //   bitwise check of types, if it returns !=0 at least 1 type bit was shared, so should execute
      if((o.type & evt.type)!=0)
        {
        if(!o.onActivationEvent(evt))
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
    for(ActionListener o : this.actionListeners)
      {
      //   bitwise check of types, if it returns !=0 at least 1 type bit was shared, so should execute
      if((o.type & evt.type)!=0)
        {
        if(!o.onActivationEvent(evt))
          {
          break;
          }
        }
      }
    }
  }

public final void addNewListener(ActionListener listener)
  {
  listener.setElement(this);
  this.actionListeners.add(listener);
  }

public abstract boolean isMouseOverElement(int mouseX, int mouseY);

public abstract void render(int mouseX, int mouseY, float partialTick);//called from gui to draw this element



}
