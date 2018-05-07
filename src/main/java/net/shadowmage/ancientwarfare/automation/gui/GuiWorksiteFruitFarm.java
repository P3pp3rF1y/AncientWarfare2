package net.shadowmage.ancientwarfare.automation.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;

public class GuiWorksiteFruitFarm extends GuiWorksiteBase {

	public GuiWorksiteFruitFarm(ContainerBase par1Container) {
		super(par1Container);
	}

	@Override
	public void initElements() {
		addLabels();
		addSideSelectButton();
		addBoundsAdjustButton();
	}

	@Override
	public void setupElements() {

	}

}
