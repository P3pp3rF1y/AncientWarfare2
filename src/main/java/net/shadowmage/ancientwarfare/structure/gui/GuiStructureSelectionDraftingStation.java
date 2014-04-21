package net.shadowmage.ancientwarfare.structure.gui;

import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateClient;

public class GuiStructureSelectionDraftingStation extends GuiStructureSelectionBase
{

GuiDraftingStation parent;

public GuiStructureSelectionDraftingStation(GuiDraftingStation parent)
  {
  super((ContainerBase) parent.inventorySlots);
  }

@Override
protected Collection<StructureTemplateClient> getTemplatesForDisplay()
  {
  return super.getTemplatesForDisplay();
  }

@Override
protected boolean onGuiCloseRequested()
  {
  Minecraft.getMinecraft().displayGuiScreen(parent);
  return false;
  }

}
