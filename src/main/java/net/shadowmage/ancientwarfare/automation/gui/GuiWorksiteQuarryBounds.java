package net.shadowmage.ancientwarfare.automation.gui;

import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteQuarryBounds;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

public class GuiWorksiteQuarryBounds extends GuiContainerBase<ContainerWorksiteQuarryBounds> {

	private NumberInput heightValue;

	private boolean boundsAdjusted = false;

	public GuiWorksiteQuarryBounds(ContainerBase par1Container) {
		super(par1Container, 48, 28);
		this.shouldCloseOnVanillaKeys = true;
	}

	@Override
	public void initElements() {
		this.clearElements();
		addGuiElement(new Label(6, 4, "guistrings.automation.height"));
		heightValue = new NumberInput(6, 12, 30, this.getContainer().maxHeight, this) {
			@Override
			public void onValueUpdated(float value) {
				getContainer().maxHeight = ((int) value);
				boundsAdjusted = true;
				refreshGui();
			}
		};
		heightValue.setIntegerValue();
		addGuiElement(heightValue);
	}

	@Override
	public void setupElements() {
		heightValue.setValue(getContainer().maxHeight);
	}

	@Override
	protected boolean onGuiCloseRequested() {
		getContainer().sendSettingsToServer();
		getContainer().onClose(boundsAdjusted);
		NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_QUARRY, getContainer().tileEntity.getPos());
		return super.onGuiCloseRequested();
	}
}
