package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.util.NonNullList;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.core.gui.elements.Tooltip;
import net.shadowmage.ancientwarfare.structure.container.ContainerStructureSelectionBase;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

public class GuiStructureSelectionBase extends GuiContainerBase<ContainerStructureSelectionBase> implements StructureTemplateManager.ITemplateObserver {
	private static final int STRUCTURE_SELECTION_WIDTH = 200;
	private static final int STRUCTURE_SELECT_BUTTON_PADDING = 8;
	private static final int MINIMUM_TEMPLATE_NAME_PADDING = 2;
	private static final int PREVIEW_HEIGHT = 148;

	private Text filterInput;
	private StructureTemplate currentSelection;
	private CompositeScrolled selectionArea;
	private Label selection;

	private StructureResourceElement resourceArea;
	private StructurePreviewElement preview;

	public GuiStructureSelectionBase(ContainerBase par1Container) {
		super(par1Container, 400, 240);
		StructureTemplateManager.registerObserver(this);
	}

	@Override
	public void onGuiClosed() {
		StructureTemplateManager.unregisterObserver(this);
	}

	@Override
	public void initElements() {
		addGuiElement(new Button(xSize - 55 - 8, 8, 55, 12, "guistrings.done") {
			@Override
			protected void onPressed() {
				if (currentSelection != null) {
					getContainer().handleNameSelection(currentSelection.name);
					getContainer().addSlots();
					closeGui();
				}
			}
		});

		Label label = new Label(8, 8, "guistrings.current_selection");
		addGuiElement(label);

		selection = new Label(8, 20, "");
		addGuiElement(selection);

		filterInput = new Text(8, 18 + 12, STRUCTURE_SELECTION_WIDTH - 16, "", this) {
			//kind of dirty...should possibly implement a real onCharEntered callback for when input actually changes
			@Override
			protected void handleKeyInput(int keyCode, char ch) {
				super.handleKeyInput(keyCode, ch);
				refreshGui();
			}
		};
		addGuiElement(filterInput);

		selectionArea = new CompositeScrolled(this, 0, 50, STRUCTURE_SELECTION_WIDTH, ySize - 50);
		addGuiElement(selectionArea);

		resourceArea = new StructureResourceElement(this, STRUCTURE_SELECTION_WIDTH, 180, xSize - STRUCTURE_SELECTION_WIDTH, 60,
				() -> currentSelection == null ? NonNullList.create() :
						currentSelection.getResourceList().stream().map(StructureTemplate.BuildResource::getStackRequired).collect(Collectors.toList())
		);
		addGuiElement(resourceArea);

		preview = new StructurePreviewElement(STRUCTURE_SELECTION_WIDTH + 2, 30, xSize - STRUCTURE_SELECTION_WIDTH - 7, PREVIEW_HEIGHT);
		addGuiElement(preview);

		setSelection(getContainer().structureName);
	}

	@Override
	public void setupElements() {
		selectionArea.clearElements();

		TemplateButton button;
		int totalHeight = 8;

		for (String templateName : getTemplatesForDisplay().stream()
				.filter(templateName -> templateName.toLowerCase().contains(filterInput.getText().toLowerCase()))
				.sorted(Comparator.comparing(String::toLowerCase)).collect(Collectors.toList())) {
			button = new TemplateButton(STRUCTURE_SELECT_BUTTON_PADDING, totalHeight, templateName, STRUCTURE_SELECTION_WIDTH - 2 * STRUCTURE_SELECT_BUTTON_PADDING - 12);
			selectionArea.addGuiElement(button);
			totalHeight += 12;
		}

		selectionArea.setAreaSize(totalHeight + 8);
	}

	protected Set<String> getTemplatesForDisplay() {
		return StructureTemplateManager.getTemplates();
	}

	@Override
	public void notifyTemplateChange(StructureTemplate template) {
		if (template.name.equals(selection.getText())) {
			currentSelection = template;
			updateSurvivalResources();
		}
	}

	private String getShortenedTemplateName(int width, String templateName) {
		StringBuilder shortenedName = new StringBuilder();
		int stringWidth = 0;
		int maxWidth = width - (2 * MINIMUM_TEMPLATE_NAME_PADDING + fontRenderer.getStringWidth("..."));
		for (int i = 0; i < templateName.length(); i++) {
			char character = templateName.charAt(i);
			stringWidth += fontRenderer.getCharWidth(character);
			if (stringWidth > maxWidth) {
				shortenedName.append("...");
				break;
			}
			shortenedName.append(character);
		}
		return shortenedName.toString();
	}

	private class TemplateButton extends Button {
		private final String templateName;

		private TemplateButton(int topLeftX, int topLeftY, String templateName, int width) {
			super(topLeftX, topLeftY, width, 12, getShortenedTemplateName(width, templateName));

			if (!templateName.equals(text)) {
				Tooltip tooltip = new Tooltip(fontRenderer.getStringWidth(templateName) + MINIMUM_TEMPLATE_NAME_PADDING * 2, 10);
				tooltip.addTooltipElement(new Label(0, 0, templateName));
				setTooltip(tooltip);
			}
			this.templateName = templateName;
		}

		@Override
		protected void onPressed() {
			setSelection(templateName);
		}

	}

	private void setSelection(String templateName) {
		setSelectionName(templateName);
		currentSelection = StructureTemplateManager.getTemplate(templateName).orElse(null);
		preview.setTemplateName(templateName);
		updateSurvivalResources();
	}

	private void updateSurvivalResources() {
		if (currentSelection != null && currentSelection.getValidationSettings().isSurvival()) {
			preview.setHeight(PREVIEW_HEIGHT);
			resourceArea.updateResources();
			resourceArea.setVisible(true);
		} else {
			preview.setHeight(ySize - 35);
			resourceArea.resetResources();
			resourceArea.setVisible(false);
		}
	}

	private void setSelectionName(String name) {
		selection.setText(name);
	}
}
