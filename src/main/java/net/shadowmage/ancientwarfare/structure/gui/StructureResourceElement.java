package net.shadowmage.ancientwarfare.structure.gui;

import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;

import javax.annotation.Nullable;

public class StructureResourceElement extends CompositeScrolled {
	private StructureTemplate structure;

	public StructureResourceElement(GuiContainerBase<?> gui, int topLeftX, int topLeftY, int width, int height) {
		super(gui, topLeftX, topLeftY, width, height);
	}

	public void resetResources() {
		clearElements();
	}

	public void setStructure(@Nullable StructureTemplate structure) {
		this.structure = structure;
	}

	public void updateResources() {
		resetResources();
		if (structure == null) {
			return;
		}

		int totalHeight = 4;
		int i = 1;
		int slotsPerRow = (width - 10) / 18;

		for (StructureTemplate.BuildResource res : structure.getResourceList()) {
			ItemSlot item = new ItemSlot(4 + ((i - 1) % slotsPerRow) * 18, totalHeight, res.getStackRequired(), getGui());
			addGuiElement(item);
			if (i % slotsPerRow == 0) {
				totalHeight += 18;
			}
			i++;
		}
		setAreaSize(totalHeight + 18);
	}
}
