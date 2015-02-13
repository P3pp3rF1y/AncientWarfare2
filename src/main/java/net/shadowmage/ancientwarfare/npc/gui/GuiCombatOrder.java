package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.container.ContainerCombatOrder;

public class GuiCombatOrder extends GuiContainerBase<ContainerCombatOrder> {

    boolean hasChanged = false;
    CompositeScrolled area;

    public GuiCombatOrder(ContainerBase container) {
        super(container, 256, 120 + 12 + 12 + 60 + 16, defaultBackground);
    }

    @Override
    public void initElements() {
        area = new CompositeScrolled(this, 0, 40, 256, 200);
        addGuiElement(area);
    }

    @Override
    public void setupElements() {
        area.clearElements();
        Label label;
        Button button;
        BlockPosition pos;
        int totalHeight = 8;
        for (int i = 0; i < getContainer().combatOrder.getPatrolSize(); i++) {
            pos = getContainer().combatOrder.getPatrolPoint(i);
            label = new Label(8, totalHeight + 1, pos.toString());
            area.addGuiElement(label);

            button = new IndexedButton(120, totalHeight, 12, 12, "+", i) {
                @Override
                protected void onPressed() {
                    getContainer().combatOrder.incrementPointPosition(index);
                    hasChanged = true;
                    refreshGui();
                }
            };
            area.addGuiElement(button);

            button = new IndexedButton(120 + 12, totalHeight, 12, 12, "-", i) {
                @Override
                protected void onPressed() {
                    getContainer().combatOrder.decrementPointPosition(index);
                    hasChanged = true;
                    refreshGui();
                }
            };
            area.addGuiElement(button);

            button = new IndexedButton(120 + 12 + 12, totalHeight, 60, 12, "guistrings.npc.remove_point", i) {
                @Override
                protected void onPressed() {
                    hasChanged = true;
                    getContainer().combatOrder.removePatrolPoint(index);
                    refreshGui();
                }
            };
            area.addGuiElement(button);

            totalHeight += 12;
        }
        area.setAreaSize(totalHeight);
    }

    @Override
    protected boolean onGuiCloseRequested() {
        if (hasChanged) {
            NBTTagCompound outer = new NBTTagCompound();
            outer.setTag("combatOrder", getContainer().combatOrder.writeToNBT(new NBTTagCompound()));
            sendDataToContainer(outer);
        }
        return super.onGuiCloseRequested();
    }

    private class IndexedButton extends Button {
        int index;

        public IndexedButton(int topLeftX, int topLeftY, int width, int height, String text, int index) {
            super(topLeftX, topLeftY, width, height, text);
            this.index = index;
        }
    }

}
