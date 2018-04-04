package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.client.resources.I18n;
import net.shadowmage.ancientwarfare.automation.container.ContainerStirlingGenerator;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.ProgressBar;

public class GuiStirlingGenerator extends GuiContainerBase<ContainerStirlingGenerator> {

	private Label energyLabel;

	private ProgressBar pg;
	private ProgressBar pg1;

	public GuiStirlingGenerator(ContainerBase par1Container) {
		super(par1Container, 178, ((ContainerStirlingGenerator) par1Container).guiHeight);
	}

	@Override
	public void initElements() {
		pg1 = new ProgressBar(8, 8, 178 - 16, 10);
		addGuiElement(pg1);

		energyLabel = new Label(8, 8, I18n.format("guistrings.automation.current_energy", String.format("%.2f", getContainer().energy)));
		addGuiElement(energyLabel);

		pg = new ProgressBar(8, 8 + 10 + 18 + 4, 178 - 16, 16);
		addGuiElement(pg);
	}

	@Override
	public void setupElements() {
		energyLabel.setText(I18n.format("guistrings.automation.current_energy", String.format("%.2f", getContainer().energy)));
		float progress = 0;
		if (getContainer().burnTimeBase > 0) {
			progress = (float) getContainer().burnTime / (float) getContainer().burnTimeBase;
		}
		pg.setProgress(progress);

		progress = (float) getContainer().energy / (float) getContainer().tileEntity.getMaxTorque(null);
		pg1.setProgress(progress);
	}

}
