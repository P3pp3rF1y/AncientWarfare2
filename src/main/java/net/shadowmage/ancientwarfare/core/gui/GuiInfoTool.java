package net.shadowmage.ancientwarfare.core.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.container.ContainerInfoTool;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;

public class GuiInfoTool extends GuiContainerBase<ContainerInfoTool> {
	public GuiInfoTool(ContainerBase container) {
		super(container, 176, 90);
	}

	@Override
	public void initElements() {
		addGuiElement(new Label(2, -10, I18n.format("guistrings.info_tool.click_to_print_stack_info")));
	}

	@Override
	public void setupElements() {
		//noop
	}

	@Override
	protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
		getContainer().printItemInfo(slotId);
	}
}
