package net.shadowmage.ancientwarfare.automation.gui;

import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseCraftingStation;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.crafting.ResearchCraftingElement;

public class GuiWarehouseCraftingStation extends GuiContainerBase<ContainerWarehouseCraftingStation> {

	public GuiWarehouseCraftingStation(ContainerBase par1Container) {
		super(par1Container, 176, 154);
	}

	@Override
	public void initElements() {
		ResearchCraftingElement research = new ResearchCraftingElement(this, getContainer().containerCrafting, 0, 0);
		addGuiElement(research);
	}

	@Override
	public void setupElements() {

	}

}
