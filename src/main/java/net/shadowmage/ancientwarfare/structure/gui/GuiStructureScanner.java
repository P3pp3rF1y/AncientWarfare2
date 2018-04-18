package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.structure.container.ContainerStructureScanner;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidationType;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidator;

import java.io.File;

public class GuiStructureScanner extends GuiContainerBase<ContainerStructureScanner> {

	private Text nameInput;
	private Label validationTypeLabel;
	private Checkbox includeOnExport;

	protected StructureValidationType validationType = StructureValidationType.GROUND;
	protected StructureValidator validator;

	public GuiStructureScanner(ContainerBase par1Container) {
		super(par1Container);
		validator = validationType.getValidator();
		this.shouldCloseOnVanillaKeys = false;
	}

	@Override
	public void initElements() {
		Label label = new Label(8, 8, I18n.format("guistrings.input_name") + ":");
		this.addGuiElement(label);

		nameInput = new Text(8, 8 + 12, 160, "", this);
		nameInput.removeAllowedChars('/', '\\', '$', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', ':', ';', '"', '\'', '+', '=', '<', '>', '?', '.', ',',
				'[', ']', '{', '}', '|');
		this.addGuiElement(nameInput);

		Button button = new Button(256 - 55 - 8, 8, 55, 16, "guistrings.export");
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

		button = new Button(256 - 55 - 8, 8 + 16, 55, 16, "guistrings.cancel") {
			@Override
			protected void onPressed() {
				closeGui();
			}
		};
		this.addGuiElement(button);

		int totalHeight = 36;

		Checkbox box = new Checkbox(8, totalHeight, 16, 16, "guistrings.include_immediately");
		box.setChecked(true);
		this.addGuiElement(box);
		includeOnExport = box;
		totalHeight += 16 + 8;

		validationTypeLabel = new Label(8, totalHeight, I18n.format("guistrings.validation_type") + " " + validationType.getName());
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

		button = new Button(8, totalHeight, 120, 16, "guistrings.select_blocks") {
			@Override
			protected void onPressed() {
				Minecraft.getMinecraft().displayGuiScreen(new GuiStructureBlockSelection(GuiStructureScanner.this));
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
		totalHeight += 16;
	}

	@Override
	public void setupElements() {
		validationTypeLabel.setText(I18n.format("guistrings.validation_type") + " " + validationType.getName());
	}

	private void export() {
		String name = nameInput.getText();
		if (!validateName(name)) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiStructureIncorrectName(this));
		} else {
			NBTTagCompound val = new NBTTagCompound();
			validator.writeToNBT(val);
			getContainer().export(name, includeOnExport.checked(), val);
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
