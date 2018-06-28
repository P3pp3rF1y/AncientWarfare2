package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.structure.container.ContainerStructureScanner;

import java.io.File;

public class GuiStructureScanner extends GuiContainerBase<ContainerStructureScanner> {

	private Text nameInput;
	private Label validationTypeLabel;

	public GuiStructureScanner(ContainerBase par1Container) {
		super(par1Container);
	}

	@Override
	public void initElements() {
		int totalHeight = 8;

		Label label = new Label(8, totalHeight, I18n.format("guistrings.input_name") + ":");
		this.addGuiElement(label);

		Button button = new Button(256 - 55 - 8, totalHeight, 55, 16, "guistrings.export");
		button.addNewListener(new Listener(Listener.MOUSE_UP) {
			@Override
			public boolean onEvent(GuiElement widget, ActivationEvent evt) {
				if (widget.isMouseOverElement(evt.mx, evt.my)) {
					export();
				}
				return true;
			}
		});
		this.addGuiElement(button);

		totalHeight += 12;

		nameInput = new Text(8, totalHeight, 160, getContainer().getName(), this) {
			@Override
			public void onTextUpdated(String oldText, String newText) {
				if (!oldText.equals(newText)) {
					getContainer().updateName(newText);
				}
			}
		};
		nameInput.removeAllowedChars('/', '\\', '$', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', ':', ';', '"', '\'', '+', '=', '<', '>', '?', '.', ',', '[', ']', '{', '}', '|');
		this.addGuiElement(nameInput);
		totalHeight += 4;

		button = new Button(256 - 55 - 8, totalHeight, 55, 16, "guistrings.cancel") {
			@Override
			protected void onPressed() {
				closeGui();
			}
		};
		this.addGuiElement(button);
		totalHeight += 8;

		Checkbox box = new Checkbox(8, totalHeight, 16, 16, "guistrings.include_immediately") {
			@Override
			public void onToggled() {
				getContainer().setIncludeImmediately(checked());
			}
		};
		box.setChecked(getContainer().getIncludeImmediately());
		this.addGuiElement(box);
		totalHeight += 16 + 8;

		validationTypeLabel = new Label(8, totalHeight, I18n.format("guistrings.validation_type") + " " + getContainer().getValidationTypeName());
		this.addGuiElement(validationTypeLabel);
		totalHeight += 10;

		button = new Button(8, totalHeight, 120, 16, "guistrings.setup_validation") {
			@Override
			protected void onPressed() {
				Minecraft.getMinecraft().displayGuiScreen(new GuiStructureValidationSettings(GuiStructureScanner.this));
			}
		};
		this.addGuiElement(button);
		totalHeight += 16;

		button = new Button(8, totalHeight, 120, 16, "guistrings.select_biomes") {
			@Override
			protected void onPressed() {
				Minecraft.getMinecraft().displayGuiScreen(new GuiStructureBiomeSelection(GuiStructureScanner.this));
			}
		};
		this.addGuiElement(button);
		totalHeight += 16;

		button = new Button(8, totalHeight, 120, 16, "guistrings.select_dimensions") {
			@Override
			protected void onPressed() {
				Minecraft.getMinecraft().displayGuiScreen(new GuiDimensionSelection(GuiStructureScanner.this));
			}
		};
		this.addGuiElement(button);
	}

	@Override
	public void setupElements() {
		validationTypeLabel.setText(I18n.format("guistrings.validation_type") + " " + getContainer().getValidationTypeName());
	}

	private void export() {
		String name = nameInput.getText();
		if (!validateName(name)) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiStructureIncorrectName(this));
		} else {
			getContainer().export();
			this.closeGui();
		}
	}

	private boolean validateName(String name) {
		if (name.equals("")) {
			return false;
		}
		for (int i = 0; i < name.length(); i++) {
			if (!validateChar(name.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	private boolean validateChar(char ch) {
		return ch != File.separatorChar;//TODO validate chars
	}

}
