package net.shadowmage.ancientwarfare.automation.gui;

import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteAutoCrafting;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.crafting.ResearchCraftingElement;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;

public class GuiWorksiteAutoCrafting extends GuiContainerBase<ContainerWorksiteAutoCrafting> {

	public GuiWorksiteAutoCrafting(ContainerBase par1Container) {
		super(par1Container, 176, 216);
	}

	@Override
	public void initElements() {
		ResearchCraftingElement research = new ResearchCraftingElement(this, getContainer().containerCrafting, 0, 0);
		addGuiElement(research);

		Button button = new Button(143 - 18, 44, 36, 12, "guistrings.automation.craft") {
			@Override
			protected void onPressed() {
				getContainer().craft();
			}
		};
		addGuiElement(button);
	}

	@Override
	public void setupElements() {

	}
}
