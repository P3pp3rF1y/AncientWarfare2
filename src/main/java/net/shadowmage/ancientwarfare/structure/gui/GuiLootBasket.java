package net.shadowmage.ancientwarfare.structure.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.structure.container.ContainerLootBasket;

public class GuiLootBasket extends GuiContainerBase<ContainerLootBasket> {
	public GuiLootBasket(ContainerBase container) {
		super(container, 176, 166);

		getContainer().getItemHandler().ifPresent(inventory -> {
			if (inventory.getSlots() > 27) {
				this.ySize = 220;
			}
		});
	}

	@Override
	public void initElements() {
		addGuiElement(new Label(8, 6, "guistrings.loot_basket.top_inventory").setColor(4210752));
		if (getContainer().getItemHandler().map(inventory -> inventory.getSlots() > 27).orElse(false)) {
			addGuiElement(new Label(8, 128, player.inventory.getDisplayName().getUnformattedText()).setColor(4210752));
		} else {
			addGuiElement(new Label(8, 74, player.inventory.getDisplayName().getUnformattedText()).setColor(4210752));
		}
	}

	@Override
	public void setupElements() {
		//noop
	}
}
