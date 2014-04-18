package net.shadowmage.ancientwarfare.core.gui.options;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.input.InputHandler.Keybind;

import org.lwjgl.input.Keyboard;

public class GuiKeybindChange extends GuiContainerBase
{

Keybind key;

Label keyLabel;

public GuiKeybindChange(ContainerBase par1Container, Keybind k)
  {
  super(par1Container, 256, 8+8+12+12+12, defaultBackground);
  this.key = k;
  }

@Override
public void initElements()  
  {
  Label label = new Label(8, 8, StatCollector.translateToLocal("guistrings.keybind.select_key")+":");
  addGuiElement(label);
  
  label = new Label(8, 8+12, StatCollector.translateToLocal(key.getName()));
  addGuiElement(label);
  
  keyLabel = new Label(8, 8+12+12, Keyboard.getKeyName(key.getKeyCode()));
  addGuiElement(keyLabel);
  
  Button button = new Button(256-8-55, 8, 55, 12, StatCollector.translateToLocal("guistrings.done"))
    {
    @Override
    protected void onPressed()
      {
      closeGui();
      }
    };
  addGuiElement(button);
  }

@Override
public void handleKeyboardInput()
  {
  int key = Keyboard.getEventKey();
  InputHandler.instance().reassignKeybind(this.key.getName(), key);  
  keyLabel.setText(Keyboard.getKeyName(key));
  }

@Override
public void setupElements()
  {

  }

@Override
protected boolean onGuiCloseRequested()
  {
  Minecraft.getMinecraft().displayGuiScreen(new GuiKeybinds(new ContainerBase(player, 0, 0, 0)));
  return false;
  }

}
