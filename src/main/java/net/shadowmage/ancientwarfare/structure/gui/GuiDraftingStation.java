package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.structure.container.ContainerDraftingStation;

public class GuiDraftingStation extends GuiContainerBase<ContainerDraftingStation> {
	private static final int RESOURCE_HEIGHT = 60;
	private StructureResourceElement resourceListArea;
	private Button stopButton;
	private Button startButton;
	private Label selectionLabel;
	private StructurePreviewElement structurePreview;

	public GuiDraftingStation(ContainerBase par1Container) {
		super(par1Container, 400, 240);
	}

	@Override
	public void initElements() {
		structurePreview = new StructurePreviewElement(176, 4, xSize - 176 - 5, 174);
		addGuiElement(structurePreview);

		resourceListArea = new StructureResourceElement(this, 176, ySize - RESOURCE_HEIGHT, 400 - 176, RESOURCE_HEIGHT,
				() -> getContainer().neededResources);
		addGuiElement(resourceListArea);

		Button selectButton = new Button(8, 8, 95, 12, "guistrings.structure.select_structure") {
			@Override
			protected void onPressed() {
				getContainer().removeSlots();
				Minecraft.getMinecraft().displayGuiScreen(new GuiStructureSelectionDraftingStation(GuiDraftingStation.this));
			}
		};
		addGuiElement(selectButton);

		selectionLabel = new Label(8, 20, getContainer().structureName == null ? "guistrings.structure.no_selection" : getContainer().structureName);
		addGuiElement(selectionLabel);

		stopButton = new Button(8, 32, 55, 12, "guistrings.stop") {
			@Override
			protected void onPressed() {
				getContainer().handleStopInput();
			}
		};

		startButton = new Button(8, 32, 55, 12, "guistrings.start") {
			@Override
			protected void onPressed() {
				getContainer().handleStartInput();
			}
		};

		Label label = new Label(8, 94 - 16 - 18 - 12, "guistrings.output");
		addGuiElement(label);

		label = new Label(8, 94 - 16, "guistrings.input");
		addGuiElement(label);
	}

	@Override
	public void setupElements() {
		removeGuiElement(startButton);
		removeGuiElement(stopButton);
		getContainer().setGui(this);
		structurePreview.setTemplateName(getContainer().structureName);
		resourceListArea.updateResources();

		String name = getContainer().structureName;
		if (name == null) {
			selectionLabel.setText("guistrings.structure.no_selection");
		} else {
			selectionLabel.setText(name);
		}
		if (getContainer().isStarted) {
			addGuiElement(stopButton);
		} else if (getContainer().structureName != null) {
			addGuiElement(startButton);
		}
	}
}
