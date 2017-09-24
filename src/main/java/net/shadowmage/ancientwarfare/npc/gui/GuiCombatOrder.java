package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.npc.container.ContainerCombatOrder;

public class GuiCombatOrder extends GuiContainerBase<ContainerCombatOrder> {

    private boolean hasChanged = false;
    private CompositeScrolled area;

    public GuiCombatOrder(ContainerBase container) {
        super(container, 256, 220);
    }

    @Override
    public void initElements() {
        area = new CompositeScrolled(this, 0, 0, xSize, ySize);
        addGuiElement(area);
    }

    @Override
    public void setupElements() {
        area.clearElements();
        Label label;
        Button button;
        BlockPos pos;
        int totalHeight = 8;
        for (int i = 0; i < getContainer().combatOrder.size(); i++) {
            pos = getContainer().combatOrder.get(i);
            label = new Label(8, totalHeight + 1, pos.toString());
            area.addGuiElement(label);

            button = new IndexedButton(120, totalHeight, 12, 12, "+", i) {
                @Override
                protected void onPressed() {
                    getContainer().combatOrder.increment(index);
                    hasChanged = true;
                    refreshGui();
                }
            };
            area.addGuiElement(button);

            button = new IndexedButton(120 + 12, totalHeight, 12, 12, "-", i) {
                @Override
                protected void onPressed() {
                    getContainer().combatOrder.decrement(index);
                    hasChanged = true;
                    refreshGui();
                }
            };
            area.addGuiElement(button);

            button = new IndexedButton(120 + 12 + 12, totalHeight, 60, 12, "guistrings.npc.remove_point", i) {
                @Override
                protected void onPressed() {
                    hasChanged = true;
                    getContainer().combatOrder.remove(index);
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
            getContainer().close();
        }
        return super.onGuiCloseRequested();
    }

    private class IndexedButton extends Button {
        final int index;

        public IndexedButton(int topLeftX, int topLeftY, int width, int height, String text, int index) {
            super(topLeftX, topLeftY, width, height, text);
            this.index = index;
        }
    }

}
