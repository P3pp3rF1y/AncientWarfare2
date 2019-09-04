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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static net.shadowmage.ancientwarfare.structure.template.build.validation.properties.StructureValidationProperties.*;

public class GuiStructureValidationSettings extends GuiContainerBase {

	private final GuiStructureScanner parent;

	private CompositeScrolled area;
	private Label typeLabel;

	private final Set<Button> typeButtons = new HashSet<>();
	private final HashMap<Button, StructureValidationType> buttonToValidationType = new HashMap<>();

	private StructureValidator validator;

	public GuiStructureValidationSettings(GuiStructureScanner parent) {
		super(new ContainerBase(parent.getContainer().player));

		this.parent = parent;
		this.shouldCloseOnVanillaKeys = false;
	}

	@Override
	public void initElements() {
		area = new CompositeScrolled(this, 0, 30, 256, 210);
		this.addGuiElement(area);

		Listener listener = new Listener(Listener.MOUSE_UP) {
			@Override
			public boolean onEvent(GuiElement widget, ActivationEvent evt) {
				if (widget.isMouseOverElement(evt.mx, evt.my)) {
					onTypeButtonPressed((Button) widget);
				}
				return true;
			}
		};

		Button button = new Button(8, 8, 78, 16, StructureValidationType.GROUND.getName());
		button.addNewListener(listener);
		buttonToValidationType.put(button, StructureValidationType.GROUND);
		typeButtons.add(button);

		button = new Button(86, 8, 78, 16, StructureValidationType.UNDERGROUND.getName());
		button.addNewListener(listener);
		buttonToValidationType.put(button, StructureValidationType.UNDERGROUND);
		typeButtons.add(button);

		button = new Button(164, 8, 78, 16, StructureValidationType.SKY.getName());
		button.addNewListener(listener);
		buttonToValidationType.put(button, StructureValidationType.SKY);
		typeButtons.add(button);

		button = new Button(8, 24, 78, 16, StructureValidationType.WATER.getName());
		button.addNewListener(listener);
		buttonToValidationType.put(button, StructureValidationType.WATER);
		typeButtons.add(button);

		button = new Button(86, 24, 78, 16, StructureValidationType.UNDERWATER.getName());
		button.addNewListener(listener);
		buttonToValidationType.put(button, StructureValidationType.UNDERWATER);
		typeButtons.add(button);

		button = new Button(164, 24, 78, 16, StructureValidationType.ISLAND.getName());
		button.addNewListener(listener);
		buttonToValidationType.put(button, StructureValidationType.ISLAND);
		typeButtons.add(button);

		button = new Button(8, 40, 78, 16, StructureValidationType.HARBOR.getName());
		button.addNewListener(listener);
		buttonToValidationType.put(button, StructureValidationType.HARBOR);
		typeButtons.add(button);

		typeLabel = new Label(8, 8, "");
		addGuiElement(typeLabel);

		button = new Button(256 - 8 - 55, 8, 55, 12, "guistrings.done");
		button.addNewListener(new Listener(Listener.MOUSE_UP) {
			@Override
			public boolean onEvent(GuiElement widget, ActivationEvent evt) {
				if (widget.isMouseOverElement(evt.mx, evt.my)) {
					closeGui();
				}
				return true;
			}
		});
		addGuiElement(button);
	}

	private void onTypeButtonPressed(Button button) {
		StructureValidationType type = buttonToValidationType.get(button);
		if (type == null) {
			return;
		}//should never happen
		StructureValidator newValidator = type.getValidator();
		newValidator.inheritPropertiesFrom(parent.getContainer().getValidator());
		parent.getContainer().setValidator(newValidator);
		validator = newValidator;
		this.refreshGui();
	}

	private static final Set<IStructureValidationProperty> EXCLUDED_PROPERTIES =
			ImmutableSet.of(BIOME_GROUP_LIST, BIOME_LIST, BIOME_WHITE_LIST, DIMENSION_LIST, DIMENSION_WHITE_LIST);

	@Override
	public void setupElements() {
		typeLabel.setText(I18n.format("guistrings.validation_type") + ": " + parent.getContainer().getValidationTypeName());

		int totalHeight = 0;
		area.clearElements();
		for (Button b : typeButtons) {
			area.addGuiElement(b);
		}
		totalHeight += 16 * 3 + 4 + 8;//type buttons height+buffer

		Label label;
		Checkbox box;
		NumberInput input;
		validator = parent.getContainer().getValidator();
		for (IStructureValidationProperty property : validator.validationType.getValidationProperties()) {
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

	@Override
	protected boolean onGuiCloseRequested() {
		parent.getContainer().setValidator(validator);
		Minecraft.getMinecraft().displayGuiScreen(parent);
		return false;
	}

	private class PropertyCheckbox extends Checkbox {

		private final StructureValidationPropertyBool prop;

		private PropertyCheckbox(int topLeftX, int topLeftY, int width, int height, StructureValidationPropertyBool property) {
			super(topLeftX, topLeftY, width, height, "");
			this.prop = property;
			setChecked(validator.getPropertyValue(prop));
		}

		@Override
		public void onToggled() {
			validator.setPropertyValue(prop, checked());
		}
	}

	private class PropertyNumberInputInteger extends NumberInput {

		private final StructureValidationPropertyInteger prop;

		private PropertyNumberInputInteger(int topLeftX, int topLeftY, int width, StructureValidationPropertyInteger property, IWidgetSelection selector) {
			super(topLeftX, topLeftY, width, validator.getPropertyValue(property), selector);
			this.prop = property;
			this.setIntegerValue();
		}

		@Override
		public void onValueUpdated(float value) {
			validator.setPropertyValue(prop, (int) value);
		}
	}

}
