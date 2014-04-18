package net.shadowmage.ancientwarfare.core.gui.options;

import java.util.Collection;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.input.InputHandler.Keybind;

public class GuiKeybinds extends GuiContainerBase
{

CompositeScrolled area;

public GuiKeybinds(ContainerBase par1Container)
  {
  super(par1Container, 256, 240, defaultBackground);
  }

@Override
public void initElements()
  {
  area = new CompositeScrolled(0, 40, 256, 200);
  addGuiElement(area);
  
  Collection<Keybind> keys = InputHandler.instance().getKeybinds();
  
  Label label;
  KeybindButton button;
  
  int totalHeight = 8;
  for(Keybind k : keys)
    {
    label = new Label(8, totalHeight+1, StatCollector.translateToLocal(k.getName()));
    area.addGuiElement(label);
    
    button = new KeybindButton(180, totalHeight, 55, 12, k);
    area.addGuiElement(button);    
    
    totalHeight+=12;
    }  
  area.setAreaSize(totalHeight);
  }

@Override
public void setupElements()
  {

  }

@Override
protected boolean onGuiCloseRequested()
  {
  Minecraft.getMinecraft().displayGuiScreen(new GuiOptions(new ContainerBase(player, 0, 0, 0)));
  return false;
  }

private class KeybindButton extends Button
{
Keybind keybind;

public KeybindButton(int topLeftX, int topLeftY, int width, int height, Keybind k)
  {
  super(topLeftX, topLeftY, width, height, Keyboard.getKeyName(k.getKeyCode()));
  this.keybind = k;
  }

@Override
protected void onPressed()
  {
  Minecraft.getMinecraft().displayGuiScreen(new GuiKeybindChange(new ContainerBase(player, 0, 0, 0), keybind));
  }

}

}
