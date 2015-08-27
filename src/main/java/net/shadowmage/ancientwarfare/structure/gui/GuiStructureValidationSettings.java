package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.elements.*;
import net.shadowmage.ancientwarfare.core.interfaces.IWidgetSelection;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidationProperty;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidationType;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GuiStructureValidationSettings extends GuiContainerBase {

    private final GuiStructureScanner parent;

    private CompositeScrolled area;
    private Label typeLabel;

    private final Set<Button> typeButtons = new HashSet<Button>();
    private final HashMap<Button, StructureValidationType> buttonToValidationType = new HashMap<Button, StructureValidationType>();

    public GuiStructureValidationSettings(GuiStructureScanner parent) {
        super(parent.getContainer());
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
        newValidator.inheritPropertiesFrom(parent.validator);
        parent.validationType = type;
        parent.validator = newValidator;
        this.refreshGui();
    }

//private HashMap<GuiElement, String> elementToPropertyName = new HashMap<GuiElement, String>();

    @Override
    public void setupElements() {
        typeLabel.setText(StatCollector.translateToLocal("guistrings.validation_type") + ": " + parent.validationType.getName());

        int totalHeight = 0;
        area.clearElements();
        for (Button b : typeButtons) {
            area.addGuiElement(b);
        }
        totalHeight += 16 * 3 + 4 + 8;//type buttons height+buffer

        Label label = null;
        String propName;
        Checkbox box;
        NumberInput input;
        for (StructureValidationProperty property : parent.validator.getProperties()) {
            propName = property.getRegName();
            if (propName.equals(StructureValidator.PROP_BIOME_LIST)
                    || propName.equals(StructureValidator.PROP_BIOME_WHITE_LIST)
                    || propName.equals(StructureValidator.PROP_DIMENSION_LIST)
                    || propName.equals(StructureValidator.PROP_DIMENSION_WHITE_LIST)
                    || propName.equals(StructureValidator.PROP_BLOCK_LIST)) {
                continue;//skip the properties handled by blocks, biome, or dimensions setup guis
            }
            label = new Label(8, totalHeight, "structure.validation." + property.getRegName());
            area.addGuiElement(label);

            switch (property.getDataType()) {
                case StructureValidationProperty.DATA_TYPE_INT: {
                    input = new PropertyNumberInputInteger(200, totalHeight - 1, 32, property, this);
                    area.addGuiElement(input);
                }
                break;
                case StructureValidationProperty.DATA_TYPE_BOOLEAN: {
                    box = new PropertyCheckbox(200, totalHeight - 3, 16, 16, property);
                    area.addGuiElement(box);
                }
                break;
            }

            totalHeight += 16;
        }
        area.setAreaSize(totalHeight);
    }

    @Override
    protected boolean onGuiCloseRequested() {
        Minecraft.getMinecraft().displayGuiScreen(parent);
        return false;
    }

    private class PropertyCheckbox extends Checkbox {

        final StructureValidationProperty prop;

        public PropertyCheckbox(int topLeftX, int topLeftY, int width, int height, StructureValidationProperty property) {
            super(topLeftX, topLeftY, width, height, "");
            this.prop = property;
            setChecked(prop.getDataBoolean());
        }

        @Override
        public void onToggled() {
            prop.setValue(checked());
        }
    }

    private class PropertyNumberInputInteger extends NumberInput {

        final StructureValidationProperty prop;

        public PropertyNumberInputInteger(int topLeftX, int topLeftY, int width, StructureValidationProperty property, IWidgetSelection selector) {
            super(topLeftX, topLeftY, width, property.getDataInt(), selector);
            this.prop = property;
            this.setIntegerValue();
        }

        @Override
        public void onValueUpdated(float value) {
            prop.setValue((int) value);
        }
    }

}
