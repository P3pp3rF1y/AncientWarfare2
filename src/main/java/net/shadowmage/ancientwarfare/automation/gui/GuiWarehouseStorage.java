package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseStorage;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

public class GuiWarehouseStorage extends GuiContainerBase
{

ContainerWarehouseStorage container;

Text nameInput;
Button filterScreenButton;

public GuiWarehouseStorage(ContainerBase par1Container)
  {
  super(par1Container, 178, 240, defaultBackground);
  container = (ContainerWarehouseStorage) par1Container;
  ySize = container.guiHeight;
  }

@Override
public void initElements()
  {
  nameInput = new Text(6, 6, 106, "", this);
  addGuiElement(nameInput);
  
  filterScreenButton = new Button(178-8-55, 6, 55, 12, StatCollector.translateToLocal("guistrings.automation.filter_setup"))
    {
    @Override
    protected void onPressed()
      {
      NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WAREHOUSE_STORAGE_FILTER, container.storageTile.xCoord, container.storageTile.yCoord, container.storageTile.zCoord);
      }
    };
  addGuiElement(filterScreenButton);
  }

@Override
public void setupElements()
  {

  }

@Override
protected boolean onGuiCloseRequested()
  {
  container.sendDataToServer();
  return true;
  }

}
