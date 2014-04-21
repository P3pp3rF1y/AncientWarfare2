package net.shadowmage.ancientwarfare.structure.gui;

import java.util.Collection;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateClient;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManagerClient;

public class GuiStructureSelection extends GuiStructureSelectionBase
{

public GuiStructureSelection(ContainerBase par1Container)
  {
  super(par1Container);
  sorter = new ComparatorStructureTemplateClient();
  sorter.setFilterText("");
  }



@Override
protected Collection<StructureTemplateClient> getTemplatesForDisplay()
  {
  return StructureTemplateManagerClient.instance().getClientStructures();
  }

}
