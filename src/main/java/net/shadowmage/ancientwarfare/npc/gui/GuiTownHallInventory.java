package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.npc.container.ContainerTownHall;

public class GuiTownHallInventory extends GuiContainerBase
{
 
ContainerTownHall container;
public GuiTownHallInventory(ContainerBase container)
  {
  super(container);
  this.container = (ContainerTownHall)container;
  this.ySize = 3*18 + 4*18 + 8 + 8 + 4 + 8 + 16;
  this.xSize = 178;
  }

@Override
public void initElements()
  {
  this.container.addSlots();
  Button button = new Button(8, 8, 75, 12, StatCollector.translateToLocal("guistrings.npc.death_list"))
    {
    @Override
    protected void onPressed()
      {
      container.removeSlots();
      Minecraft.getMinecraft().displayGuiScreen(new GuiTownHallDeathList(GuiTownHallInventory.this));
      }
    };
  addGuiElement(button);
  }

@Override
public void setupElements()
  {

  }


}
