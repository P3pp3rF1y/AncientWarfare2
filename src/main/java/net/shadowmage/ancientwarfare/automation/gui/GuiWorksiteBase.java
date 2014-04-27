package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteBase;
import net.shadowmage.ancientwarfare.automation.tile.TileWorksiteBase;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.inventory.InventorySide;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided.SideSlotMap;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

public class GuiWorksiteBase extends GuiContainerBase
{

public GuiWorksiteBase(ContainerBase par1Container)
  {
  super(par1Container, 178, ((ContainerWorksiteBase)par1Container).guiHeight, defaultBackground);
  }

@Override
public void initElements()
  {
  TileWorksiteBase worksite = ((ContainerWorksiteBase)inventorySlots).worksite;
  Label label;
  for(InventorySide side : InventorySide.values())
    {
    if(side==InventorySide.NONE){continue;}
    SideSlotMap slotMap = worksite.inventory.getSlotMapForSide(side);
    if(slotMap==null){continue;}
    label = new Label(slotMap.guiX, slotMap.guiY, StatCollector.translateToLocal(slotMap.label));
    addGuiElement(label);
    }
  label = new Label(8, ((ContainerWorksiteBase)inventorySlots).playerSlotsLabelHeight, StatCollector.translateToLocal("guistrings.inventory.player"));
  addGuiElement(label);
  
  int invY = ySize-8-12;
  if(worksite.hasAltSetupGui())
    {
    invY-=12;
    }
  Button button = new Button(8, invY, 55, 12, StatCollector.translateToLocal("guistrings.inventory.setsides"))
    {
    @Override
    protected void onPressed()
      {      
      TileWorksiteBase worksite = ((ContainerWorksiteBase)inventorySlots).worksite;
      NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_INVENTORY_SIDE_ADJUST, worksite.xCoord, worksite.yCoord, worksite.zCoord);      
      }
    };
  addGuiElement(button);
  
  if(worksite.hasUserSetTargets())
    {
    button = new Button(178-8-100, invY, 100, 12, StatCollector.translateToLocal("guistrings.inventory.settargets"))
      {
      @Override
      protected void onPressed()
        {
        TileWorksiteBase worksite = ((ContainerWorksiteBase)inventorySlots).worksite;
        NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_SET_TARGETS, worksite.xCoord, worksite.yCoord, worksite.zCoord);
        }
      };
    addGuiElement(button);
    }
  
  if(worksite.hasAltSetupGui())
    {
    button = new Button(8, ySize-8-12, 95, 12, StatCollector.translateToLocal("guistrings.automation.advanced_setup"))
      {
      @Override
      protected void onPressed()
        {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("openAltGui", true);
        sendDataToContainer(tag);
        }
      };
    addGuiElement(button);
    }
  }

@Override
public void setupElements()
  {
  
  
  }

}
