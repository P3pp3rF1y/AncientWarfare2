package net.shadowmage.ancientwarfare.core.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.gui.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.interfaces.ITabCallback;

/**
 * tab element for use by CompositeTabbed.  Only has minimal self function
 * @author Shadowmage
 *
 */
public class Tab extends GuiElement
{

ITabCallback parent;
FontRenderer fr;
String label;
boolean top;

public Tab(int topLeftX, int topLeftY, boolean top, String label, ITabCallback parentCaller)
  {
  super(topLeftX, topLeftY);
  fr = Minecraft.getMinecraft().fontRenderer;
  this.setWidth(fr.getStringWidth(label)+6);
  this.label = label;
  this.height = 14;
  this.parent = parentCaller;
  this.top = top;
  this.addNewListener(new Listener(Listener.MOUSE_UP)
    { 
    @Override
    public boolean onEvent(ActivationEvent evt)
      {
      AWLog.logDebug("tab click event...");
      if(visible && enabled && !selected() && isMouseOverElement(evt.mx, evt.my))
        {
        setSelected(true);
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
        if(parent!=null)
          {
          parent.onTabSelected(Tab.this);
          }
        }
      return true;
      }
    });
  }

@Override
public void render(int mouseX, int mouseY, float partialTick)
  {
  if(visible)
    {
    int y = 162;
    if(selected())
      {
      y = 138;
      }
    if(!top)
      {
      y += 48;
      }
    Minecraft.getMinecraft().renderEngine.bindTexture(widgetTexture1);
    renderQuarteredTexture(256, 256, 152, y, 104, 24, renderX, renderY, getWidth(), 16);    
    fr.drawStringWithShadow(label, renderX+3, renderY+4, 0xffffffff);
    }
  }

}
