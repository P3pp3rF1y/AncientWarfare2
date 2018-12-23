package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;

import java.util.Collection;

public class GuiStructureSelectionDraftingStation extends GuiStructureSelectionBase {

	private final GuiDraftingStation parent;

	public GuiStructureSelectionDraftingStation(GuiDraftingStation parent) {
		super(parent.getContainer());
		this.parent = parent;
	}

	@Override
	protected Collection<StructureTemplate> getTemplatesForDisplay() {
		return StructureTemplateManager.INSTANCE.getSurvivalStructures().values();
	}

	@Override
	protected boolean onGuiCloseRequested() {
		Minecraft.getMinecraft().displayGuiScreen(parent);
		return false;
	}

}
