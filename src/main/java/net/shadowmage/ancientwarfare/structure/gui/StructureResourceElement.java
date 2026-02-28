package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;

import java.util.List;
import java.util.function.Supplier;

public class StructureResourceElement extends CompositeScrolled {
	private final Supplier<List<ItemStack>> getResourceList;

	public StructureResourceElement(GuiContainerBase<?> gui, int topLeftX, int topLeftY, int width, int height, Supplier<List<ItemStack>> getResourceList) {
		super(gui, topLeftX, topLeftY, width, height);
		this.getResourceList = getResourceList;
	}

	public void resetResources() {
		clearElements();
	}

	public void updateResources() {
		resetResources();
		int totalHeight = 4;
		int i = 1;
		int slotsPerRow = (width - 10) / 18;

		for (ItemStack stack : getResourceList.get()) {
			ItemSlot item = new ItemSlot(4 + ((i - 1) % slotsPerRow) * 18, totalHeight, stack, getGui());
			addGuiElement(item);
			if (i % slotsPerRow == 0) {
				totalHeight += 18;
			}
			i++;
		}
		setAreaSize(totalHeight + 18);
	}
}
