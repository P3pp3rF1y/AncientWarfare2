package net.shadowmage.ancientwarfare.structure.gui;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.core.interfaces.IWidgetSelection;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidationType;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidator;
import net.shadowmage.ancientwarfare.structure.template.build.validation.properties.IStructureValidationProperty;
import net.shadowmage.ancientwarfare.structure.template.build.validation.properties.StructureValidationPropertyBool;
import net.shadowmage.ancientwarfare.structure.template.build.validation.properties.StructureValidationPropertyInteger;

import java.util.Set;

import static net.shadowmage.ancientwarfare.structure.template.build.validation.properties.StructureValidationProperties.*;

public class GuiStructureValidationSettings extends GuiContainerBase {
	private final GuiStructureScanner parent;

	private CompositeScrolled area;
	private Label typeLabel;

	public GuiStructureValidationSettings(GuiStructureScanner parent) {
		super(new ContainerBase(parent.getContainer().player));

		this.parent = parent;
		this.shouldCloseOnVanillaKeys = false;
	}

	@Override
	public void initElements() {
		area = new CompositeScrolled(this, 0, 30, 256, 210);
		this.addGuiElement(area);

		typeLabel = new Label(8, 8, "");
		addGuiElement(typeLabel);

		Button doneButton = new Button(256 - 8 - 55, 8, 55, 12, "guistrings.done");
		doneButton.addNewListener(new Listener(Listener.MOUSE_UP) {
			@Override
			public boolean onEvent(GuiElement widget, ActivationEvent evt) {
				if (widget.isMouseOverElement(evt.mx, evt.my)) {
					closeGui();
				}
				return true;
			}
		});
		addGuiElement(doneButton);
	}

	private Listener getListener(StructureValidationType validationType) {
		return new Listener(Listener.MOUSE_UP) {
			@Override
			public boolean onEvent(GuiElement widget, ActivationEvent evt) {
				if (widget.isMouseOverElement(evt.mx, evt.my)) {
					onTypeButtonPressed(validationType);
				}
				return true;
			}
		};
	}

	private void onTypeButtonPressed(StructureValidationType type) {
		StructureValidator newValidator = type.getValidator();
		newValidator.inheritPropertiesFrom(parent.getContainer().getValidator());
		parent.getContainer().setValidator(newValidator);
		this.refreshGui();
	}

	private static final Set<IStructureValidationProperty> EXCLUDED_PROPERTIES =
			ImmutableSet.of(BIOME_GROUP_LIST, BIOME_LIST, BIOME_WHITE_LIST, DIMENSION_LIST, DIMENSION_WHITE_LIST);

	@Override
	public void setupElements() {
		typeLabel.setText(I18n.format("guistrings.validation_type") + ": " + parent.getContainer().getValidationTypeName());

		int totalHeight = 0;
		area.clearElements();

		addValidationButton(8, 8, StructureValidationType.GROUND);
		addValidationButton(86, 8, StructureValidationType.UNDERGROUND);
		addValidationButton(164, 8, StructureValidationType.SKY);
		addValidationButton(8, 24, StructureValidationType.WATER);
		addValidationButton(86, 24, StructureValidationType.UNDERWATER);
		addValidationButton(164, 24, StructureValidationType.ISLAND);
		addValidationButton(8, 40, StructureValidationType.HARBOR);

		totalHeight += 16 * 3 + 4 + 8;//type buttons height+buffer

		Label label;
		Checkbox box;
		NumberInput input;
		for (IStructureValidationProperty property : parent.getContainer().getValidator().validationType.getValidationProperties()) {
			if (EXCLUDED_PROPERTIES.contains(property)) {
				continue;//skip the properties handled by blocks, biome, or dimensions setup guis
			}
			label = new Label(8, totalHeight, "structure.validation." + property.getName());
			area.addGuiElement(label);

			if (StructureValidationPropertyInteger.class.isAssignableFrom(property.getClass())) {
				input = new PropertyNumberInputInteger(200, totalHeight - 1, 32, (StructureValidationPropertyInteger) property, this);
				area.addGuiElement(input);
			} else if (StructureValidationPropertyBool.class.isAssignableFrom(property.getClass())) {
				box = new PropertyCheckbox(200, totalHeight - 3, 16, 16, (StructureValidationPropertyBool) property);
				area.addGuiElement(box);
			}

			totalHeight += 16;
		}
		area.setAreaSize(totalHeight);
	}

	private void addValidationButton(int topLeftX, int topLeftY, StructureValidationType validationType) {
		Button button = new Button(topLeftX, topLeftY, 78, 16, validationType.getName());
		button.addNewListener(getListener(validationType));
		area.addGuiElement(button);
	}

	@Override
	protected boolean onGuiCloseRequested() {
		Minecraft.getMinecraft().displayGuiScreen(parent);
		return false;
	}

	private class PropertyCheckbox extends Checkbox {

		private final StructureValidationPropertyBool prop;

		private PropertyCheckbox(int topLeftX, int topLeftY, int width, int height, StructureValidationPropertyBool property) {
			super(topLeftX, topLeftY, width, height, "");
			this.prop = property;
			setChecked(parent.getContainer().getValidator().getPropertyValue(prop));
		}

		@Override
		public void onToggled() {
			parent.getContainer().getValidator().setPropertyValue(prop, checked());
		}
	}

	private class PropertyNumberInputInteger extends NumberInput {

		private final StructureValidationPropertyInteger prop;

		private PropertyNumberInputInteger(int topLeftX, int topLeftY, int width, StructureValidationPropertyInteger property, IWidgetSelection selector) {
			super(topLeftX, topLeftY, width, parent.getContainer().getValidator().getPropertyValue(property), selector);
			this.prop = property;
			this.setIntegerValue();
		}

		@Override
		public void onValueUpdated(float value) {
			parent.getContainer().getValidator().setPropertyValue(prop, (int) value);
		}
	}

}
