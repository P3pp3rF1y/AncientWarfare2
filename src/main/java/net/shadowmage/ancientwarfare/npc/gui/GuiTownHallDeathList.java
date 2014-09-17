package net.shadowmage.ancientwarfare.npc.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Line;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall.NpcDeathEntry;

public class GuiTownHallDeathList extends GuiContainerBase
{

GuiTownHallInventory parent;
CompositeScrolled area;
public GuiTownHallDeathList(GuiTownHallInventory parent)
  {
  super((ContainerBase)parent.inventorySlots);
  this.parent = parent;
  }

@Override
public void initElements()
  {
  area = new CompositeScrolled(0, 40, xSize, ySize-40);
  addGuiElement(area);
  Button button = new Button(8, 8, 55, 12, "guistrings.npc.clear_death_list")
    {
    @Override
    protected void onPressed()
      {
      NBTTagCompound tag = new NBTTagCompound();
      tag.setBoolean("clear", true);
      sendDataToContainer(tag);
      }
    };
  addGuiElement(button);
  }

@Override
public void setupElements()
  {
  area.clearElements();
  List<NpcDeathEntry> deathList = parent.container.getDeathList();
  int totalHeight = 8;
  
  Label label;
  String labelText;
  for(NpcDeathEntry entry : deathList)
    {
    labelText = StatCollector.translateToLocal("guistrings.npc.npc_name");
    label = new Label(8, totalHeight, labelText+": "+entry.npcName);
    area.addGuiElement(label);
    totalHeight+=12;
    
    labelText = StatCollector.translateToLocal("guistrings.npc.npc_type");
    label = new Label(8, totalHeight, labelText+": "+StatCollector.translateToLocal("npc."+entry.npcType+".name"));
    area.addGuiElement(label);
    totalHeight+=12;
    
    labelText = StatCollector.translateToLocal("guistrings.npc.death_cause");
    label = new Label(8, totalHeight, labelText+": "+ StatCollector.translateToLocal(entry.deathCause));
    area.addGuiElement(label);
    totalHeight+=12;

    labelText = StatCollector.translateToLocal("guistrings.npc.can_res");
    label = new Label(8, totalHeight, labelText+": "+ String.valueOf(entry.canRes));
    area.addGuiElement(label);
    totalHeight+=12;
    
    if(entry.canRes)
      {    
      labelText = StatCollector.translateToLocal("guistrings.npc.resurrected");
      label = new Label(8, totalHeight, labelText+": "+ String.valueOf(entry.resurrected));
      area.addGuiElement(label);
      totalHeight+=12;      
      }
    
    area.addGuiElement(new Line(0, totalHeight-1, xSize, totalHeight-1, 1, 0x000000ff));
    totalHeight+=4;
    }
  area.setAreaSize(totalHeight);
  }

@Override
protected boolean onGuiCloseRequested()
  {
  Minecraft.getMinecraft().displayGuiScreen(parent);
  parent.container.addSlots();
  parent.refreshGui();
  return false;
  }

}
