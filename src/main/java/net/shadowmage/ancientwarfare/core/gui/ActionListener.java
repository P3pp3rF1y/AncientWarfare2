package net.shadowmage.ancientwarfare.core.gui;

import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;

public abstract class ActionListener
{
public static final int KEY_UP = 0;
public static final int KEY_DOWN = 1;
public static final int MOUSE_UP = 2;
public static final int MOUSE_DOWN = 4;
public static final int MOUSE_WHEEL = 8;

public final int type;
private GuiElement element;

public GuiElement getElement()
  {
  return element;
  }

public ActionListener(int type)
  {
  this.type = type;
  }

public void setElement(GuiElement element)
  {
  this.element = element;
  }

public abstract boolean onActivationEvent(ActivationEvent evt);

}
