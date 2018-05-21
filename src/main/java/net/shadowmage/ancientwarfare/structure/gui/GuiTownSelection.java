package net.shadowmage.ancientwarfare.structure.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.structure.container.ContainerTownSelection;
import net.shadowmage.ancientwarfare.structure.town.TownTemplate;
import net.shadowmage.ancientwarfare.structure.town.TownTemplateManager;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GuiTownSelection extends GuiContainerBase<ContainerTownSelection> {
	private TownTemplate currentSelection;
	private CompositeScrolled selectionArea;
	private Label selection;

	public GuiTownSelection(ContainerBase container) {
		super(container, 257, 240);
	}

	protected GuiTownSelection(ContainerBase container, int xSize, int ySize) {
		super(container, xSize, ySize);
	}

	@Override
	public void initElements() {
		addGuiElement(new Button(xSize - 55 - 8, 8, 55, 12, "guistrings.done") {
			@Override
			protected void onPressed() {
				if (currentSelection != null) {
					getContainer().handleNameSelection(currentSelection.getTownTypeName());
					getContainer().addSlots();
					closeGui();
				}
			}
		});

		Label label = new Label(8, 8, "guistrings.current_selection");
		addGuiElement(label);

		selection = new Label(8, 20, "");
		addGuiElement(selection);

		addGuiElement(new Label(8, 34, "Width"));
		NumberInput chunkWidth = new NumberInput(40, 33, 10, getContainer().getWidth(), this) {
			@Override
			public void onValueUpdated(float value) {
				getContainer().handleWidthUpdate((int) value);
			}
		};
		chunkWidth.setIntegerValue();
		addGuiElement(chunkWidth);

		addGuiElement(new Label(56, 34, "Length"));
		NumberInput chunkLength = new NumberInput(94, 33, 10, getContainer().getLength(), this) {
			@Override
			public void onValueUpdated(float value) {
				getContainer().handleLengthUpdate((int) value);
			}
		};
		chunkLength.setIntegerValue();
		addGuiElement(chunkLength);

		selectionArea = new CompositeScrolled(this, 0, 50, 256, 240 - 50);
		addGuiElement(selectionArea);

		TownTemplateManager.INSTANCE.getTemplate(getContainer().townName).ifPresent(this::setSelection);
	}

	@Override
	public void setupElements() {
		selectionArea.clearElements();
		selection.setText((currentSelection == null ? "guistrings.none" : currentSelection.getTownTypeName()));

		TemplateButton button;
		int totalHeight = 8;

		for (TownTemplate template : getTownTemplatesSorted()) {
			button = new TemplateButton(8, totalHeight, template);
			selectionArea.addGuiElement(button);
			totalHeight += 12;
		}

		selectionArea.setAreaSize(totalHeight + 8);
	}

	private List<TownTemplate> getTownTemplatesSorted() {
		return TownTemplateManager.INSTANCE.getTemplates().stream().sorted(Comparator.comparing(t -> t.getTownTypeName().toLowerCase())).collect(Collectors.toList());
	}

	private class TemplateButton extends Button {
		TownTemplate template;

		public TemplateButton(int topLeftX, int topLeftY, TownTemplate template) {
			super(topLeftX, topLeftY, 232, 12, template.getTownTypeName());
			this.template = template;
		}

		@Override
		protected void onPressed() {
			setSelection(template);
		}

	}

	private void setSelection(TownTemplate template) {
		currentSelection = template;
		selection.setText(template == null ? "guistrings.none" : template.getTownTypeName());
	}
}
