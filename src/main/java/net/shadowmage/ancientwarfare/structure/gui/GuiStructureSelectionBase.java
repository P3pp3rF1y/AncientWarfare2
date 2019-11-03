package net.shadowmage.ancientwarfare.structure.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.structure.container.ContainerStructureSelectionBase;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

public class GuiStructureSelectionBase extends GuiContainerBase<ContainerStructureSelectionBase> implements StructureTemplateManager.ITemplateObserver {

	private Text filterInput;
	private StructureTemplate currentSelection;
	private CompositeScrolled selectionArea;
	private Label selection;

	private CompositeScrolled resourceArea;

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

		filterInput = new Text(8, 18 + 12, 240 - 16, "", this) {
			//kind of dirty...should possibly implement a real onCharEntered callback for when input actually changes
			@Override
			protected void handleKeyInput(int keyCode, char ch) {
				super.handleKeyInput(keyCode, ch);
				refreshGui();
			}
		};
		addGuiElement(filterInput);

		selectionArea = new CompositeScrolled(this, 0, 50, 256, 240 - 50);
		addGuiElement(selectionArea);

		resourceArea = new CompositeScrolled(this, 256, 40, 144, 200);
		addGuiElement(resourceArea);

		this.setSelection(getContainer().structureName);
	}

	@Override
	public void setupElements() {
		selectionArea.clearElements();

		TemplateButton button;
		int totalHeight = 8;

		for (String templateName : getTemplatesForDisplay().stream()
				.filter(templateName -> templateName.toLowerCase().contains(filterInput.getText().toLowerCase()))
				.sorted(Comparator.comparing(String::toLowerCase)).collect(Collectors.toList())) {
			button = new TemplateButton(8, totalHeight, templateName);
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
			updateSurivalResources();
		}
	}

	private class TemplateButton extends Button {
		private String templateName;

		private TemplateButton(int topLeftX, int topLeftY, String templateName) {
			super(topLeftX, topLeftY, 232, 12, templateName);
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
		updateSurivalResources();
	}

	private void updateSurivalResources() {
		resourceArea.clearElements();
		int totalHeight = 8;
		if (currentSelection != null && currentSelection.getValidationSettings().isSurvival()) {
			for (StructureTemplate.BuildResource res : currentSelection.getResourceList()) {
				ItemSlot item = new ItemSlot(8, totalHeight, res.getStackRequired(), this);
				resourceArea.addGuiElement(item);
				totalHeight += 18;
			}
		}
		resourceArea.setAreaSize(totalHeight + 8);
	}

	private void setSelectionName(String name) {
		selection.setText(name);
	}
}
