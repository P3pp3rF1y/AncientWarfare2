package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.shadowmage.ancientwarfare.automation.container.ContainerMailbox;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.block.Direction;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import org.lwjgl.input.Mouse;

public class GuiMailboxInventorySideSetup extends GuiContainerBase<ContainerMailbox> {

    private final GuiMailboxInventory parent;

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
        EnumFacing dir;

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

            dir = RelativeSide.getMCSideToAccess(RotationType.FOUR_WAY, getContainer().tileEntity.getPrimaryFacing(), side);
            label = new Label(74, height, Direction.getDirectionFor(dir).getTranslationKey());
            addGuiElement(label);

            if(getContainer().receivedSides.contains(dir)) {
                accessed = RelativeSide.TOP;
            } else if(getContainer().sendSides.contains(dir)) {
                accessed = RelativeSide.BOTTOM;
            } else {
                accessed = RelativeSide.NONE;
            }
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
        final RelativeSide side;//base side
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
            switch(selection) {
                case TOP:
                    selection = RelativeSide.BOTTOM;
                    break;
                case BOTTOM:
                    selection = RelativeSide.NONE;
                    break;
                default:
                    selection = RelativeSide.TOP;
            }
            setText(selection.getTranslationKey());
            getContainer().sendSlotChange(side, selection);
            getContainer().updateSides(side, selection);
        }

    }

}
