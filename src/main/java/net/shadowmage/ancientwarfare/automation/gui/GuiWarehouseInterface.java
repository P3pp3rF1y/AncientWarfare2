package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseInterface;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.WarehouseInterfaceFilter;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.core.interfaces.ITooltipRenderer;

import java.util.List;

public class GuiWarehouseInterface extends GuiContainerBase<ContainerWarehouseInterface> {

    CompositeScrolled area;
    boolean hasChanged = false;

    public GuiWarehouseInterface(ContainerBase par1Container) {
        super(par1Container, 178, 240);
    }

    @Override
    public void initElements() {
        area = new CompositeScrolled(this, 0, 0, xSize, 88);
        addGuiElement(area);
    }

    @Override
    public void setupElements() {
        area.clearElements();
        List<WarehouseInterfaceFilter> filters = getContainer().filters;

        int totalHeight = 8;

        ItemSlot slot;
        NumberInput input;
        Button button;

        for (WarehouseInterfaceFilter filter : filters) {
            slot = new FilterItemSlot(8, totalHeight, filter, this);
            area.addGuiElement(slot);

            input = new FilterQuantityInput(8 + 30, totalHeight + 3, filter);
            input.setIntegerValue();
            area.addGuiElement(input);

            button = new FilterRemoveButton(xSize - 16 - 12, totalHeight + 3, filter);
            area.addGuiElement(button);

            totalHeight += 18;
        }

        if (filters.size() < 9) {
            button = new Button(8, totalHeight, 95, 12, "guistrings.automation.new_filter") {
                @Override
                protected void onPressed() {
                    WarehouseInterfaceFilter filter = new WarehouseInterfaceFilter();
                    filter.setFilterQuantity(64);
                    getContainer().filters.add(filter);
                    getContainer().sendFiltersToServer();
                    refreshGui();
                }
            };
            area.addGuiElement(button);
            totalHeight += 12;
        }

        area.setAreaSize(totalHeight);
    }


    private class FilterRemoveButton extends Button {
        WarehouseInterfaceFilter filter;

        public FilterRemoveButton(int topLeftX, int topLeftY, WarehouseInterfaceFilter filter) {
            super(topLeftX, topLeftY, 12, 12, "-");
            this.filter = filter;
        }

        @Override
        protected void onPressed() {
            getContainer().filters.remove(filter);
            getContainer().sendFiltersToServer();
            refreshGui();
        }
    }

    private class FilterQuantityInput extends NumberInput {
        WarehouseInterfaceFilter filter;

        public FilterQuantityInput(int topLeftX, int topLeftY, WarehouseInterfaceFilter filter) {
            super(topLeftX, topLeftY, 40, filter.getFilterQuantity(), GuiWarehouseInterface.this);
            this.filter = filter;
        }

        @Override
        public void onValueUpdated(float value) {
            int val = (int) value;
            this.filter.setFilterQuantity(val);
            refreshGui();
            getContainer().sendFiltersToServer();
        }

    }

    private class FilterItemSlot extends ItemSlot {
        WarehouseInterfaceFilter filter;

        public FilterItemSlot(int topLeftX, int topLeftY, WarehouseInterfaceFilter filter, ITooltipRenderer render) {
            super(topLeftX, topLeftY, filter.getFilterItem(), render);
            this.filter = filter;
            this.setRenderItemQuantity(false);
        }

        @Override
        public void onSlotClicked(ItemStack stack) {
            ItemStack in = stack == null ? null : stack.copy();
            this.setItem(in);
            if (in != null) {
                in.stackSize = 1;
            }
            filter.setFilterQuantity(0);
            filter.setFilterItem(in == null ? null : in.copy());
            getContainer().sendFiltersToServer();
        }
    }

}
