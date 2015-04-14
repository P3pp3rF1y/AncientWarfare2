package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.automation.container.ContainerMailbox;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.block.Direction;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import org.lwjgl.input.Mouse;

import java.util.EnumSet;

public class GuiMailboxInventorySideSetup extends GuiContainerBase<ContainerMailbox> {

    GuiMailboxInventory parent;

    public GuiMailboxInventorySideSetup(GuiMailboxInventory parent) {
        super(parent.getContainer(), 240, 108);
        this.parent = parent;
    }

    @Override
    public void initElements() {

    }

    @Override
    public void setupElements() {
        this.clearElements();

        Label label;
        SideButton sideButton;
        RelativeSide accessed;
        int dir;

        label = new Label(8, 6, "guistrings.automation.block_side");
        addGuiElement(label);
        label = new Label(74, 6, "guistrings.automation.direction");
        addGuiElement(label);
        label = new Label(128, 6, "guistrings.automation.inventory_accessed");
        addGuiElement(label);

        int height = 18;
        for (RelativeSide side : RotationType.FOUR_WAY.getValidSides()) {
            label = new Label(8, height, side.getTranslationKey());
            addGuiElement(label);

            dir = RelativeSide.getMCSideToAccess(RotationType.FOUR_WAY, getContainer().tileEntity.getBlockMetadata(), side);
            label = new Label(74, height, Direction.getDirectionFor(dir).getTranslationKey());
            addGuiElement(label);

            accessed = getContainer().sideMap.get(side);
            sideButton = new SideButton(128, height, side, accessed);
            addGuiElement(sideButton);

            height += 14;
        }
    }

    @Override
    protected boolean onGuiCloseRequested() {
        getContainer().addSlots();
        int x = Mouse.getX();
        int y = Mouse.getY();
        Minecraft.getMinecraft().displayGuiScreen(parent);
        Mouse.setCursorPosition(x, y);
        return false;
    }

    private class SideButton extends Button {
        RelativeSide side;//base side
        RelativeSide selection;//accessed side

        public SideButton(int topLeftX, int topLeftY, RelativeSide side, RelativeSide selection) {
            super(topLeftX, topLeftY, 55, 12, selection.getTranslationKey());
            if (side == null) {
                throw new IllegalArgumentException("access side may not be null..");
            }
            this.side = side;
            this.selection = selection;
        }

        @Override
        protected void onPressed() {
            int ordinal = selection.ordinal();
            RelativeSide next;
            EnumSet<RelativeSide> validSides = getContainer().tileEntity.inventory.getValidSides();
            for (int i = 0; i < RelativeSide.values().length; i++) {
                ordinal++;
                if (ordinal >= RelativeSide.values().length) {
                    ordinal = 0;
                }
                next = RelativeSide.values()[ordinal];
                if (validSides.contains(next)) {
                    selection = next;
                    break;
                }
            }
            getContainer().sideMap.put(side, selection);
            setText(selection.getTranslationKey());
            getContainer().sendSlotChange(side, selection);
        }

    }

}
