package net.shadowmage.ancientwarfare.structure.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.structure.container.ContainerGateControl;

public class GuiGateControl extends GuiContainerBase<ContainerGateControl> {

	public GuiGateControl(ContainerBase container) {
		this(container, 55 + 8 + 8, 12 + 8 + 8);
	}

	public GuiGateControl(ContainerBase container, int xSize, int ySize) {
		super(container, xSize, ySize);
	}

	@Override
	public void initElements() {
		Button button = new Button(8, 8, 55, 12, "guistrings.gate.repack") {
			@Override
			protected void onPressed() {
				getContainer().repackGate();
				closeGui();
			}
		};
		addGuiElement(button);
	}

	@Override
	public void setupElements() {

	}

}
