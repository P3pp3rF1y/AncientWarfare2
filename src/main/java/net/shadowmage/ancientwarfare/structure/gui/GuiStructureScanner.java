package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
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

public class GuiStructureScanner extends GuiContainerBase<ContainerStructureScanner> implements IContainerListener {

	private Text nameInput;
	private Label validationTypeLabel;
	private Button exportButton;
	private Checkbox includeImmediately;
	private Button setupValidationButton;
	private Button selectBiomesButton;
	private Button selectDimensionsButton;
	private Label statusMessage;
	private int statusTicks = 0;
	private Button boundsButton;

	public GuiStructureScanner(ContainerBase par1Container) {
		super(par1Container);

		par1Container.addListener(this);
	}

	@Override
	public void initElements() {
		int totalHeight = 8;

		statusMessage = new Label(80, 10, "");
		addGuiElement(statusMessage);

		if (getContainer().getScannerTile().isPresent()) {
			totalHeight += 20;
		}

		Label label = new Label(8, totalHeight, I18n.format("guistrings.input_name") + ":");
		this.addGuiElement(label);

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
		totalHeight += 12;

		includeImmediately = new Checkbox(8, totalHeight, 16, 16, "guistrings.include_immediately") {
			@Override
			public void onToggled() {
				getContainer().setIncludeImmediately(checked());
			}
		};
		this.addGuiElement(includeImmediately);
		totalHeight += 16 + 8;

		validationTypeLabel = new Label(8, totalHeight, I18n.format("guistrings.validation_type") + " " + getContainer().getValidationTypeName());
		this.addGuiElement(validationTypeLabel);
		totalHeight += 10;

		setupValidationButton = new Button(8, totalHeight, 120, 16, "guistrings.setup_validation") {
			@Override
			protected void onPressed() {
				Minecraft.getMinecraft().displayGuiScreen(new GuiStructureValidationSettings(GuiStructureScanner.this));
			}
		};
		this.addGuiElement(setupValidationButton);
		totalHeight += 16;

		selectBiomesButton = new Button(8, totalHeight, 120, 16, "guistrings.select_biomes") {
			@Override
			protected void onPressed() {
				Minecraft.getMinecraft().displayGuiScreen(new GuiStructureBiomeSelection(GuiStructureScanner.this));
			}
		};
		this.addGuiElement(selectBiomesButton);
		totalHeight += 16;

		selectDimensionsButton = new Button(8, totalHeight, 120, 16, "guistrings.select_dimensions") {
			@Override
			protected void onPressed() {
				Minecraft.getMinecraft().displayGuiScreen(new GuiDimensionSelection(GuiStructureScanner.this));
			}
		};
		this.addGuiElement(selectDimensionsButton);

		addButtons();
		updateElements();
	}

	private void addButtons() {
		int totalHeight = 8;
		exportButton = new Button(256 - 55 - 8, totalHeight, 55, 16, "guistrings.export");
		exportButton.addNewListener(new Listener(Listener.MOUSE_UP) {
			@Override
			public boolean onEvent(GuiElement widget, ActivationEvent evt) {
				if (widget.isMouseOverElement(evt.mx, evt.my)) {
					export();
				}
				return true;
			}
		});
		addGuiElement(exportButton);

		boundsButton = new Button(256 - 65 -8, -20, 65, 16,
				getContainer().getBoundsActive() ? "guistrings.bounds_off" : "guistrings.bounds_on");
		boundsButton.addNewListener(new Listener(Listener.MOUSE_UP) {
			@Override
			public boolean onEvent(GuiElement widget, ActivationEvent evt) {
				if (widget.isMouseOverElement(evt.mx, evt.my)) {
					toggleBounds();
				}
				return true;
			}
		});
		addGuiElement(boundsButton);
	}

	private void toggleBounds() {
		getContainer().toggleBounds();
		boundsButton.setText(getContainer().getBoundsActive() ? "guistrings.bounds_off" : "guistrings.bounds_on");
	}

	private void updateElements() {
		if (exportButton == null) {
			return;
		}
		boolean hasScanner = getContainer().hasScanner();
		boolean readyToExport = getContainer().getReadyToExport();
		exportButton.setEnabled(readyToExport);
		boundsButton.setEnabled(readyToExport);

		nameInput.setText(getContainer().getName());
		nameInput.setEnabled(hasScanner);

		includeImmediately.setChecked(getContainer().getIncludeImmediately());
		includeImmediately.setEnabled(readyToExport);

		setupValidationButton.setEnabled(readyToExport);
		selectBiomesButton.setEnabled(readyToExport);
		selectDimensionsButton.setEnabled(readyToExport);
	}

	@Override
	public void setupElements() {
		if (!getContainer().hasScanner()) {
			return;
		}

		validationTypeLabel.setText(I18n.format("guistrings.validation_type") + " " + getContainer().getValidationTypeName());
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (statusTicks > 0) {
			statusTicks--;
		}
		statusMessage.setVisible(statusTicks > 0);
	}

	private void export() {
		String name = nameInput.getText();
		if (!validateName(name)) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiStructureIncorrectName(this));
		} else {
			getContainer().export();
			if (!getContainer().getScannerTile().isPresent()) {
				this.closeGui();
			} else {
				statusMessage.setText("Exported");
				statusTicks = 60;
			}
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

	@Override
	public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {
		//noop
	}

	@Override
	public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
		if (slotInd == 0) {
			updateElements();
		}
	}

	@Override
	public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {
		//noop
	}

	@Override
	public void sendAllWindowProperties(Container containerIn, IInventory inventory) {
		//noop
	}
}
