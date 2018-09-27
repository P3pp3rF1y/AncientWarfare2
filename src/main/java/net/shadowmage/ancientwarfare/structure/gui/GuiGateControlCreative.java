package net.shadowmage.ancientwarfare.structure.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;

public class GuiGateControlCreative extends GuiGateControl {

	public GuiGateControlCreative(ContainerBase container) {
		super(container, 55 + 8 + 8, 12 + 8 + 8 + 16 + 16);
	}

	@Override
	public void initElements() {
		super.initElements();
		addGuiElement(new Label(8, 24, "guistrings.gate.gate_owner"));
	}

	@Override
	public void setupElements() {
		Text owner = new Text(8, 40, 55, getContainer().entity.getOwner().getName(), this) {
			@Override
			public void onTextUpdated(String oldText, String newText) {
				getContainer().updateOwner(newText);
			}
		};
		addGuiElement(owner);
	}

}
