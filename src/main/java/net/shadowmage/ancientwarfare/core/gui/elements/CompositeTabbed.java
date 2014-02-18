package net.shadowmage.ancientwarfare.core.gui.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.core.gui.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;
import net.shadowmage.ancientwarfare.core.interfaces.ITabCallback;

/**
 * Composite Tabbed Area <br>
 * 
 * Must have at least one tab to be considered a valid element.<br>
 * 
 * WILL NPE if improperly setup and no tabs exist
 * @author Shadowmage
 *
 */
public class CompositeTabbed extends Composite implements ITabCallback
{

protected HashMap<Tab, List<GuiElement>> tabElements = new HashMap<Tab, List<GuiElement>>();
protected HashMap<String, Tab> tabs = new HashMap<String, Tab>();
protected Tab currentTab = null;

protected boolean hasTopTabs = false;
protected boolean hasBottomTabs = false;

public CompositeTabbed(int topLeftX, int topLeftY, int width, int height)
  {
  super(topLeftX, topLeftY, width, height);
  }

@Override
protected void addDefaultListeners()
  {
  this.addNewListener(new Listener(Listener.ALL_EVENTS)
    {
    @Override
    public boolean onEvent(ActivationEvent evt)
      {
      if((evt.type & Listener.KEY_TYPES) != 0)
        {
        if(currentTab!=null)
          {
          for(GuiElement element : tabElements.get(currentTab))
            {
            element.handleKeyboardInput(evt);
            }
          }        
        }
      else if((evt.type & Listener.MOUSE_TYPES) != 0)
        {
        /**
         * adjust mouse event position for relative to composite
         */
        int x = evt.mx;
        int y = evt.my;
        evt.mx-=renderX;
        evt.my-=renderY;         
        for(GuiElement element : tabElements.get(currentTab))
          {
          element.handleMouseInput(evt);
          }
        for(GuiElement element : elements)
          {
          element.handleMouseInput(evt);
          }
        evt.mx = x;
        evt.my = y;
        }
      return true;
      }
    });
  }

public void addTab(String tabName, boolean top)
  {
  int w = 3;//offset by starting border
  for(Tab t : tabs.values())
    {
    if(t.top == top)
      {
      w += t.getWidth();      
      }
    }
  Tab t = new Tab(w, top ? 0 : height - 16, top, tabName, this);
  this.tabs.put(tabName, t); 
  if(currentTab==null)
    {
    this.onTabSelected(t);
    }
  if(top){this.hasTopTabs = true;}
  else{this.hasBottomTabs = true;}
  this.elements.add(t);
  this.tabElements.put(t, new ArrayList<GuiElement>());
  }

@Override
public void addGuiElement(GuiElement element)
  {
  if(this.currentTab!=null)
    {
    this.tabElements.get(currentTab).add(element);
    }
  else
    {
    throw new IllegalArgumentException("cannot add elements to a null tab, you must create at least one tab first");
    }  
  }

public void addGuiElement(String tabName, GuiElement element)
  {
  if(!tabs.containsKey(tabName))
    {
    throw new IllegalArgumentException("cannot add elements to a null tab, you must create the tab first");
    }
  this.tabElements.get(tabs.get(tabName)).add(element);
  }

public void removeGuiElement(String tabName, GuiElement element)
  {
  if(!tabs.containsKey(tabName))
    {
    throw new IllegalArgumentException("cannot add elements to a null tab, you must create the tab first");
    }
  this.tabElements.get(tabs.get(tabName)).remove(element);
  }

public void removeTab(String tabName)
  {
  Tab tab = this.tabs.get(tabName);
  if(tab==null){return;}
  tabs.remove(tab);
  tabElements.remove(tab);
  if(tab==this.currentTab)
    {
    if(tabs.isEmpty())
      {
      this.currentTab = null;
      }
    else
      {
      this.currentTab = tabs.values().iterator().next();
      }
    }
  }

public void setActiveTab(String tabName)
  {
  this.onTabSelected(tabs.get(tabName));
  }

@Override
public void onTabSelected(Tab tab)
  {
  for(GuiElement t : this.tabs.values())
    {
    t.setSelected(false);
    }
  tab.setSelected(true);
  this.currentTab = tab;
  for(GuiElement element : this.tabElements.get(currentTab))
    {
    element.updateRenderPosition(0, hasTopTabs? 13 : 0);
    }
  }

@Override
public void render(int mouseX, int mouseY, float partialTick)
  {
  mouseX-=renderX;
  mouseY-=renderY;
  Minecraft.getMinecraft().renderEngine.bindTexture(backgroundTextureLocation);
  int topY = renderY;
  int height = this.height;
  if(hasTopTabs)
    {
    topY+=13;
    height-=13;
    }
  if(hasBottomTabs){height-=13;}
  this.renderQuarteredTexture(256, 256, 0, 0, 256, 240, renderX, topY, getWidth(), height);
  setViewport();
  for(GuiElement element : this.tabs.values())
    {
    element.render(mouseX, mouseY, partialTick);
    }    
  for(GuiElement element : this.tabElements.get(currentTab))
    {
    element.render(mouseX, mouseY, partialTick);
    }
  resetViewport();
  }

@Override
protected void updateElementPositions()
  {
  for(GuiElement element : this.tabElements.get(currentTab))
    {
    element.updateRenderPosition(0, hasTopTabs? 13 : 0);
    }
  }

}
