package net.shadowmage.ancientwarfare.core.gui.crafting;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.container.ContainerEngineeringStation;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;

public class GuiEngineeringStation extends GuiContainerBase<ContainerEngineeringStation> {

	public GuiEngineeringStation(ContainerBase par1Container) {
		super(par1Container, 176, 192);
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
