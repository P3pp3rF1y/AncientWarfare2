package net.shadowmage.ancientwarfare.automation.gui;

import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseCraftingStation;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.crafting.ResearchCraftingElement;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;

import java.awt.*;

public class GuiWarehouseCraftingStation extends GuiContainerBase<ContainerWarehouseCraftingStation> {

	public GuiWarehouseCraftingStation(ContainerBase par1Container) {
		super(par1Container, 176, 154);
	}

	@Override
	public void initElements() {
		ResearchCraftingElement research = new ResearchCraftingElement(this, getContainer().containerCrafting, 0, 0);
		addGuiElement(research);

		if (getContainer().tileEntity.getWarehouse() == null) {
			Label missingWarehouse = new Label(30, 61, "Missing Warehouse below").setColor(Color.RED.getRGB()).setShadow(false);
			addGuiElement(missingWarehouse);
		}
	}

	@Override
	public void setupElements() {

	}

}
