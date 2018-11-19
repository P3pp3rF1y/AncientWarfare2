package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.item.ItemStack;
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

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

public class GuiStructureSelectionBase extends GuiContainerBase<ContainerStructureSelectionBase> {

	private Text filterInput;
	private StructureTemplate currentSelection;
	private CompositeScrolled selectionArea;
	private Label selection;

	private CompositeScrolled resourceArea;

	public GuiStructureSelectionBase(ContainerBase par1Container) {
		super(par1Container, 400, 240);
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

		selectionArea = new CompositeScrolled(this, 0, 138, 256, 240 - 138);
		addGuiElement(selectionArea);

		resourceArea = new CompositeScrolled(this, 256, 40, 144, 200);
		addGuiElement(resourceArea);

		StructureTemplate t = StructureTemplateManager.INSTANCE.getTemplate(getContainer().structureName);
		this.setSelection(t);
	}

	@Override
	public void setupElements() {
		selectionArea.clearElements();
		setSelectionName((currentSelection == null ? "guistrings.none" : currentSelection.name));

		TemplateButton button;
		int totalHeight = 8;

		for (StructureTemplate template : getTemplatesForDisplay().stream()
				.filter(t -> t.name.toLowerCase().contains(filterInput.getText().toLowerCase()))
				.sorted(Comparator.comparing(t -> t.name.toLowerCase())).collect(Collectors.toList())) {
			button = new TemplateButton(8, totalHeight, template);
			selectionArea.addGuiElement(button);
			totalHeight += 12;
		}

		selectionArea.setAreaSize(totalHeight + 8);
	}

	protected Collection<StructureTemplate> getTemplatesForDisplay() {
		return StructureTemplateManager.INSTANCE.getTemplates();
	}

	private class TemplateButton extends Button {
		private StructureTemplate template;

		private TemplateButton(int topLeftX, int topLeftY, StructureTemplate template) {
			super(topLeftX, topLeftY, 232, 12, template.name);
			this.template = template;
		}

		@Override
		protected void onPressed() {
			setSelection(template);
		}
	}

	private void setSelection(StructureTemplate template) {
		resourceArea.clearElements();
		int totalHeight = 8;
		this.currentSelection = template;
		this.setSelectionName(template == null ? "guistrings.none" : template.name);

		if (template != null) {
			if (template.getValidationSettings().isSurvival()) {
				for (ItemStack stack : template.getResourceList()) {
					ItemSlot item = new ItemSlot(8, totalHeight, stack, this);
					resourceArea.addGuiElement(item);
					totalHeight += 18;
				}
			}
		}
		resourceArea.setAreaSize(totalHeight + 8);
	}

	private void setSelectionName(String name) {
		selection.setText(name);
	}
}
