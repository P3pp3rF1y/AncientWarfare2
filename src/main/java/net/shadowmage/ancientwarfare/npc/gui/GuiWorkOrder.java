package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.*;
import net.shadowmage.ancientwarfare.core.interfaces.IWidgetSelection;
import net.shadowmage.ancientwarfare.npc.container.ContainerWorkOrder;
import net.shadowmage.ancientwarfare.npc.orders.WorkOrder.WorkEntry;

import java.util.List;
import java.util.Locale;

public class GuiWorkOrder extends GuiContainerBase<ContainerWorkOrder> {

    private boolean hasChanged = false;
    private CompositeScrolled area;
    private Button routeButton, shiftButton;

    public GuiWorkOrder(ContainerBase container) {
        super(container);
    }

    @Override
    public void initElements() {
        area = new CompositeScrolled(this, 0, 40, xSize, ySize - 40);
        addGuiElement(area);
        routeButton = new Button(8, 8, 100, 12, "") {
            @Override
            protected void onPressed() {
                getContainer().wo.togglePriority();
                hasChanged = true;
                refreshGui();
            }
        };
        addGuiElement(routeButton);
        shiftButton = new Button(8, 24, 100, 12, "") {
            @Override
            protected void onPressed() {
                getContainer().wo.toggleShift();
                hasChanged = true;
                refreshGui();
            }
        };
        addGuiElement(shiftButton);
    }

    @Override
    public void setupElements() {
        area.clearElements();
        String type = getContainer().wo.getPriorityType().name().toLowerCase(Locale.ENGLISH);
        routeButton.setText("guistrings.npc.work_priority." + type);
        type = getContainer().wo.isNightShift() ? "night" : "day";
        shiftButton.setText("guistrings.npc.work_shift." + type);
        List<WorkEntry> entries = getContainer().wo.getEntries();
        int totalHeight = 8;
        int index = 0;
        for (WorkEntry entry : entries) {
            GuiElement element = new ItemSlot(8, totalHeight + 2, new ItemStack(entry.getBlock(Minecraft.getMinecraft().world)), this);
            area.addGuiElement(element);

            element = new Label(8 + 20, totalHeight, entry.getPosition().toString());
            area.addGuiElement(element);

            element = new IndexedButton(8 + 20 + 20, totalHeight + 10, 12, 12, "+", index) {
                @Override
                protected void onPressed() {
                    getContainer().wo.increment(index);
                    hasChanged = true;
                    refreshGui();
                }
            };
            area.addGuiElement(element);

            element = new IndexedButton(8 + 20 + 12 + 20, totalHeight + 10, 12, 12, "-", index) {
                @Override
                protected void onPressed() {
                    getContainer().wo.decrement(index);
                    hasChanged = true;
                    refreshGui();
                }
            };
            area.addGuiElement(element);

            element = new IndexedButton(8 + 20 + 12 + 12 + 20, totalHeight + 10, 60, 12, "guistrings.npc.remove_work_point", index) {
                @Override
                protected void onPressed() {
                    getContainer().wo.remove(index);
                    hasChanged = true;
                    refreshGui();
                }
            };
            area.addGuiElement(element);

            element = new Label(8 + 20 + 12 + 12 + 60 + 40 + 20, totalHeight, "guistrings.npc.work_length");
            area.addGuiElement(element);

            element = new WorkEntryNumberInput(8 + 20 + 12 + 12 + 60 + 40 + 20, totalHeight + 10, 60, entry.getWorkLength() / 1200, this, entry) {
                @Override
                public void onValueUpdated(float value) {
                    int ticks = (int) (value * 1200.f);
                    entry.setWorkLength(ticks);
                    hasChanged = true;
                }
            };

            area.addGuiElement(element);

            totalHeight += 25;
            index++;
        }
        area.setAreaSize(totalHeight);
    }

    @Override
    protected boolean onGuiCloseRequested() {
        if (hasChanged) {
            getContainer().onClose();
        }
        return super.onGuiCloseRequested();
    }

    private class WorkEntryNumberInput extends NumberInput {

        final WorkEntry entry;

        public WorkEntryNumberInput(int topLeftX, int topLeftY, int width,
                                    float defaultText, IWidgetSelection selector, WorkEntry e) {
            super(topLeftX, topLeftY, width, defaultText, selector);
            this.entry = e;
        }

    }

    private class IndexedButton extends Button {
        final int index;

        public IndexedButton(int topLeftX, int topLeftY, int width, int height, String text, int index) {
            super(topLeftX, topLeftY, width, height, text);
            this.index = index;
        }
    }

}
