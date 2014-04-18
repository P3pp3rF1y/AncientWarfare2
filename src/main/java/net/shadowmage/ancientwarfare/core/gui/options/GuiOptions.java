package net.shadowmage.ancientwarfare.core.gui.options;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.config.ClientOptions;
import net.shadowmage.ancientwarfare.core.config.ClientOptions.ClientOption;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;

public class GuiOptions extends GuiContainerBase
{

CompositeScrolled area;

public GuiOptions(ContainerBase par1Container)
  {
  super(par1Container, 256, 240, defaultBackground);
  }

@Override
public void initElements()
  {
  Button button = new Button(256-8-90, 8, 90, 12, StatCollector.translateToLocal("guistrings.keybinds"))
    {
    @Override
    protected void onPressed()
      {
      Minecraft.getMinecraft().displayGuiScreen(new GuiKeybinds(new ContainerBase(player,0,0,0)));
      }
    };
  addGuiElement(button);
  
  area = new CompositeScrolled(0, 40, 256, 200);
  addGuiElement(area);
  
  int totalHeight = 8;
  
  Label label;
  Checkbox box;
  NumberInput input;
  
  ClientOption o;
  for(String key : ClientOptions.INSTANCE.getOptionKeys())
    {
    o = ClientOptions.INSTANCE.getClientOption(key);
    
    if(o.isBooleanValue())
      {
      box = new Checkbox(8, totalHeight, 16, 16, StatCollector.translateToLocal(o.getName()))
        {
        @Override
        public void onToggled()
          {
          optionMap.get(this).setValue(checked());
          }
        };
      area.addGuiElement(box);
      optionMap.put(box, o);
      }
    else if(o.isIntValue())
      {
      label = new Label(8, totalHeight, StatCollector.translateToLocal(o.getName()));
      area.addGuiElement(label);
      input = new NumberInput(150, totalHeight, 35, o.getIntValue(), this)
        {
        @Override
        public void onValueUpdated(float value)
          {
          optionMap.get(this).setValue((int)value);
          }
        };
      input.setIntegerValue();
      input.setAllowNegative();
      area.addGuiElement(input);
      optionMap.put(input, o);      
      }
    
    totalHeight+=16;
    }  
  area.setAreaSize(totalHeight+8);
  }

private HashMap<GuiElement, ClientOption> optionMap = new HashMap<GuiElement, ClientOption>();

@Override
public void setupElements()
  {

  }

}
