package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.*;
import net.shadowmage.ancientwarfare.core.interfaces.IWidgetSelection;
import net.shadowmage.ancientwarfare.npc.container.ContainerWorkOrder;
import net.shadowmage.ancientwarfare.npc.orders.WorkOrder.WorkEntry;

import java.util.List;

public class GuiWorkOrder extends GuiContainerBase<ContainerWorkOrder> {

//TODO display work priority type via button
//TODO add number-input for changing work-length

    boolean hasChanged = false;
    CompositeScrolled area;

    public GuiWorkOrder(ContainerBase container) {
        super(container);
    }

    @Override
    public void initElements() {
        area = new CompositeScrolled(this, 0, 40, xSize, ySize - 40);
        addGuiElement(area);

        //TODO add 'shift selection' to the top of the GUI -- early day / late day / night??
    }

    @Override
    public void setupElements() {
        area.clearElements();
        List<WorkEntry> entries = getContainer().wo.getEntries();
        ItemStack blockStack;
        ItemSlot slot;
        Label label;
        Button button;
        NumberInput input;
        int totalHeight = 8;
        int index = 0;
        for (WorkEntry entry : entries) {
            blockStack = new ItemStack(Item.getItemFromBlock(entry.getBlock()));
            slot = new ItemSlot(8, totalHeight + 2, blockStack, this);
            area.addGuiElement(slot);

            label = new Label(8 + 20, totalHeight, String.valueOf(entry.getPosition()));
            area.addGuiElement(label);

            button = new IndexedButton(8 + 20 + 20, totalHeight + 10, 12, 12, "+", index) {
                @Override
                protected void onPressed() {
                    getContainer().wo.incrementPosition(index);
                    hasChanged = true;
                    refreshGui();
                }
            };
            area.addGuiElement(button);

            button = new IndexedButton(8 + 20 + 12 + 20, totalHeight + 10, 12, 12, "-", index) {
                @Override
                protected void onPressed() {
                    getContainer().wo.decrementPosition(index);
                    hasChanged = true;
                    refreshGui();
                }
            };
            area.addGuiElement(button);

            button = new IndexedButton(8 + 20 + 12 + 12 + 20, totalHeight + 10, 60, 12, "guistrings.npc.remove_work_point", index) {
                @Override
                protected void onPressed() {
                    getContainer().wo.removePosition(index);
                    hasChanged = true;
                    refreshGui();
                }
            };
            area.addGuiElement(button);

            label = new Label(8 + 20 + 12 + 12 + 60 + 40 + 20, totalHeight, "guistrings.npc.work_length");
            area.addGuiElement(label);

            input = new WorkEntryNumberInput(8 + 20 + 12 + 12 + 60 + 40 + 20, totalHeight + 10, 60, entry.getWorkLength() / 1200, this, entry) {
                @Override
                public void onValueUpdated(float value) {
                    int ticks = (int) (value * 1200.f);
                    entry.setWorkLength(ticks);
                    hasChanged = true;
                }
            };

            area.addGuiElement(input);

            totalHeight += 25;
            index++;
        }
        area.setAreaSize(totalHeight);
    }

    @Override
    protected boolean onGuiCloseRequested() {
        if (hasChanged) {
            NBTTagCompound outer = new NBTTagCompound();
            outer.setTag("wo", getContainer().wo.writeToNBT(new NBTTagCompound()));
            sendDataToContainer(outer);
        }
        return super.onGuiCloseRequested();
    }

    private class WorkEntryNumberInput extends NumberInput {

        WorkEntry entry;

        public WorkEntryNumberInput(int topLeftX, int topLeftY, int width,
                                    float defaultText, IWidgetSelection selector, WorkEntry e) {
            super(topLeftX, topLeftY, width, defaultText, selector);
            this.entry = e;
        }

    }

    private class IndexedButton extends Button {
        int index;

        public IndexedButton(int topLeftX, int topLeftY, int width, int height, String text, int index) {
            super(topLeftX, topLeftY, width, height, text);
            this.index = index;
        }
    }

}
