package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.automation.container.ContainerMailbox;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;

import org.lwjgl.input.Mouse;

public class GuiMailboxInventory extends GuiContainerBase
{

Label inputName, outputName;
Button inputNameSelect, outputNameSelect, sideSelectButton;
Checkbox autoExportFromRear;
Checkbox privateBox;
ContainerMailbox container;

public GuiMailboxInventory(ContainerBase par1Container)
  {
  super(par1Container, 178, 240, defaultBackground);
  container = (ContainerMailbox)par1Container;
  ySize = container.guiHeight;
  }

@Override
public void initElements()
  {
  inputNameSelect = new Button(178-8-75, 8, 75, 12, StatCollector.translateToLocal("guistrings.automation.mailbox_name_select"))
    {
    @Override
    protected void onPressed()
      {
      container.removeSlots();
      int x = Mouse.getX();
      int y = Mouse.getY();
      Minecraft.getMinecraft().displayGuiScreen(new GuiMailboxNameSelect(GuiMailboxInventory.this, true));
      Mouse.setCursorPosition(x, y);
      }
    };
  addGuiElement(inputNameSelect);
  
  outputNameSelect = new Button(178-8-75, 8+12+2*18, 75, 12, StatCollector.translateToLocal("guistrings.automation.mailbox_target_select"))
    {
    @Override
    protected void onPressed()
      {
      container.removeSlots();
      int x = Mouse.getX();
      int y = Mouse.getY();
      Minecraft.getMinecraft().displayGuiScreen(new GuiMailboxNameSelect(GuiMailboxInventory.this, false));
      Mouse.setCursorPosition(x, y);
      }
    };
  addGuiElement(outputNameSelect);
  
  sideSelectButton = new Button(178-8-75, ySize-8-12-12, 75, 12, StatCollector.translateToLocal("guistrings.inventory.setsides"))
    {
    @Override
    protected void onPressed()
      {
      container.removeSlots();
      int x = Mouse.getX();
      int y = Mouse.getY();
      Minecraft.getMinecraft().displayGuiScreen(new GuiMailboxInventorySideSetup(GuiMailboxInventory.this));
      Mouse.setCursorPosition(x, y);
      }
    };
  addGuiElement(sideSelectButton);
  
  autoExportFromRear = new Checkbox(8, ySize-12-12-8, 12, 12, StatCollector.translateToLocal("guistrings.automation.auto_output"))
    {
    @Override
    public void onToggled()
      {
      container.handleAutoExportToggle(checked());
      }
    };
  autoExportFromRear.setChecked(container.autoExport);
  addGuiElement(autoExportFromRear);
  
  privateBox = new Checkbox(8, ySize-12-8, 12, 12, StatCollector.translateToLocal("guistrings.automation.private_mailbox"))
    {
    @Override
    public void onToggled()
      {
      container.handlePrivateBoxToggle(checked());
      }
    };
  privateBox.setChecked(container.privateBox);
  addGuiElement(privateBox);
  
  inputName = new Label(8, 8, "");
  addGuiElement(inputName);
  
  outputName = new Label(8, 8+12+2*18, "");
  addGuiElement(outputName);
  }

@Override
public void setupElements()
  {
  inputName.setText(container.mailboxName);
  outputName.setText(container.targetName);  
  }

}
