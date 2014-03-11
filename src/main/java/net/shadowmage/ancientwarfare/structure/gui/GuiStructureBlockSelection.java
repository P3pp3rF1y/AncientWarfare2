package net.shadowmage.ancientwarfare.structure.gui;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;

public class GuiStructureBlockSelection extends GuiContainerBase
{

GuiStructureScanner parent;

CompositeScrolled area;

public GuiStructureBlockSelection(GuiStructureScanner parent)
  {
  super((ContainerBase) parent.inventorySlots, 256, 240, defaultBackground);
  this.parent = parent;  
  this.shouldCloseOnVanillaKeys = false;
  }

@Override
public void initElements()
  {
  Label label = new Label(8,8, StatCollector.translateToLocal("guistrings.select_blocks")+":");
  addGuiElement(label);
  
  Button button = new Button(256-8-55, 8, 55, 12, StatCollector.translateToLocal("guistrings.done"));
  button.addNewListener(new Listener(Listener.MOUSE_UP)
    {
    @Override
    public boolean onEvent(GuiElement widget, ActivationEvent evt)
      {
      if(widget.isMouseOverElement(evt.mx, evt.my))
        {
        Minecraft.getMinecraft().displayGuiScreen(parent);
        }
      return true;
      }    
    });
  addGuiElement(button);
  
  area = new CompositeScrolled(0, 8+12+4, 256, 240-24);
  this.addGuiElement(area);
  
  int totalHeight = 3;
  Set<String> blockNames = parent.validator.getTargetBlocks();
  
  Block block;
  Checkbox box;
  String name;
  for(int i = 0; i < 256; i++)
    {
    block = Block.getBlockById(i);
    if(block==null || block==Blocks.air){continue;}
    
    name = BlockDataManager.instance().getNameForBlock(block);
    box = new Checkbox(8, totalHeight, 16, 16, name);
    area.addGuiElement(box);
    if(blockNames.contains(name))
      {
      box.setChecked(true);
      }    
    totalHeight +=16;
    }  
  
  area.setAreaSize(totalHeight);
  }

@Override
public void setupElements()
  {

  }

}
