package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class GuiStructureSelectionDraftingStation extends GuiStructureSelectionBase {

    GuiDraftingStation parent;
    List<StructureTemplateClient> templateList = new ArrayList<StructureTemplateClient>();

    public GuiStructureSelectionDraftingStation(GuiDraftingStation parent) {
        super(parent.getContainer());
        this.parent = parent;
    }

    @Override
    protected Collection<StructureTemplateClient> getTemplatesForDisplay() {
        templateList.clear();
        templateList.addAll(super.getTemplatesForDisplay());
        Iterator<StructureTemplateClient> it = templateList.iterator();
        StructureTemplateClient ct;
        while (it.hasNext() && (ct = it.next()) != null) {
            if (!ct.survival) {
                it.remove();
            }
        }
        return templateList;
    }

    @Override
    protected boolean onGuiCloseRequested() {
        Minecraft.getMinecraft().displayGuiScreen(parent);
        return false;
    }

}
