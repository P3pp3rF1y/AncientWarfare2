package net.shadowmage.ancientwarfare.automation.gui;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteTest;
import net.shadowmage.ancientwarfare.automation.tile.TileWorksiteBase;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.block.RelativeSide;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided.SideSlotMap;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

public class GuiWorksiteTest extends GuiContainerBase
{

public GuiWorksiteTest(ContainerBase par1Container)
  {
  super(par1Container, 178, ((ContainerWorksiteTest)par1Container).guiHeight + 16, defaultBackground);
  }

@Override
public void initElements()
  {
  TileWorksiteBase worksite = ((ContainerWorksiteTest)inventorySlots).worksite;
  Label label;
  for(RelativeSide side : RelativeSide.values())
    {
    SideSlotMap slotMap = worksite.inventory.getSlotMapForSide(side);
    if(slotMap==null){continue;}
    label = new Label(slotMap.guiX, slotMap.guiY, StatCollector.translateToLocal(slotMap.label));
    addGuiElement(label);
    }
  label = new Label(8, ((ContainerWorksiteTest)inventorySlots).playerSlotsLabelHeight, StatCollector.translateToLocal("guistrings.inventory.player"));
  addGuiElement(label);
  Button button = new Button(8, ((ContainerWorksiteTest)inventorySlots).guiHeight-8-12, 55, 12, StatCollector.translateToLocal("guistrings.inventory.setsides"))
    {
    @Override
    protected void onPressed()
      {      
      TileWorksiteBase worksite = ((ContainerWorksiteTest)inventorySlots).worksite;
      NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_INVENTORY_SIDE_ADJUST, worksite.xCoord, worksite.yCoord, worksite.zCoord);      
      }
    };
  addGuiElement(button);
  }

@Override
public void setupElements()
  {
  
  
  }

}
