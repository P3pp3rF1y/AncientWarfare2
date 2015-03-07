package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.*;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidator;

import java.util.HashSet;
import java.util.Set;

public class GuiDimensionSelection extends GuiContainerBase {

    GuiStructureScanner parent;

    CompositeScrolled area;
    Checkbox whiteList;
    StructureValidator validator;

    NumberInput dimensionSelection;
    Button add;

    Set<Integer> dims = new HashSet<Integer>();

    public GuiDimensionSelection(GuiStructureScanner parent) {
        super(parent.getContainer());
        this.parent = parent;
        this.shouldCloseOnVanillaKeys = false;
        this.validator = parent.validator;
        for (int dim : validator.getAcceptedDimensions()) {
            dims.add(dim);
        }
    }

    @Override
    public void initElements() {
        Label label = new Label(8, 8, StatCollector.translateToLocal("guistrings.select_dimensions") + ":");
        addGuiElement(label);

        area = new CompositeScrolled(this, 0, 40, 256, 200);
        this.addGuiElement(area);

        Button button = new Button(256 - 8 - 55, 8, 55, 12, StatCollector.translateToLocal("guistrings.done")) {
            @Override
            protected void onPressed() {
                closeGui();
            }
        };
        addGuiElement(button);

        whiteList = new Checkbox(8, 20, 16, 16, StatCollector.translateToLocal("guistrings.dimension_whitelist") + "?") {
            @Override
            public void onToggled() {
                parent.validator.setDimensionWhiteList(checked());
            }
        };
        addGuiElement(whiteList);
        whiteList.setChecked(parent.validator.isDimensionWhiteList());

        dimensionSelection = new NumberInput(140, 22, 35, 0, this);
        dimensionSelection.setIntegerValue();
        dimensionSelection.setAllowNegative();
        add = new Button(140 + 35 + 4, 22, 12, 12, "+") {
            @Override
            protected void onPressed() {
                int num = dimensionSelection.getIntegerValue();
                dims.add(num);
                validator.setValidDimension(dims);
                refreshGui();
            }
        };

        addGuiElement(add);
        addGuiElement(dimensionSelection);
    }

    @Override
    public void setupElements() {
        area.clearElements();
        whiteList.setChecked(parent.validator.isDimensionWhiteList());

        int totalHeight = 8;
        for (Integer dim : dims) {
            area.addGuiElement(new DimensionButton(8, totalHeight, 232, 12, dim));
            totalHeight += 12;
        }
        area.setAreaSize(totalHeight);
    }

    @Override
    protected boolean onGuiCloseRequested() {
        Minecraft.getMinecraft().displayGuiScreen(parent);
        return false;
    }

    private class DimensionButton extends Button {
        int dim;

        public DimensionButton(int topLeftX, int topLeftY, int width, int height, int dim) {
            super(topLeftX, topLeftY, width, height, StatCollector.translateToLocal("guistrings.dimension") + ": " + String.valueOf(dim));
            this.dim = dim;
        }

        @Override
        protected void onPressed() {
            dims.remove(dim);
            validator.setValidDimension(dims);
            refreshGui();
        }
    }

}
