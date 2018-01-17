package net.shadowmage.ancientwarfare.automation.gui;

import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteBase;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileWorksiteBoundedInventory;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

public abstract class GuiWorksiteBase extends GuiContainerBase<ContainerWorksiteBase<TileWorksiteBoundedInventory>> {

    public GuiWorksiteBase(ContainerBase par1Container) {
        super(par1Container, 178, 240);
        this.ySize = getContainer().guiHeight + 12;
    }

    protected void addLabels() {
        Label label;
        if (getContainer().topLabel > 0) {
            label = new Label(8, getContainer().topLabel, "guistrings.inventory.side.top");
            addGuiElement(label);
        }
        if (getContainer().frontLabel > 0) {
            label = new Label(8, getContainer().frontLabel, "guistrings.inventory.front");
            addGuiElement(label);
        }
        if (getContainer().bottomLabel > 0) {
            label = new Label(8, getContainer().bottomLabel, "guistrings.inventory.bottom");
            addGuiElement(label);
        }
        if (getContainer().rearLabel > 0) {
            label = new Label(8, getContainer().rearLabel, "guistrings.inventory.rear");
            addGuiElement(label);
        }
        if (getContainer().leftLabel > 0) {
            label = new Label(8, getContainer().leftLabel, "guistrings.inventory.left");
            addGuiElement(label);
        }
        if (getContainer().rightLabel > 0) {
            label = new Label(8, getContainer().rightLabel, "guistrings.inventory.right");
            addGuiElement(label);
        }
        if (getContainer().playerLabel > 0) {
            label = new Label(8, getContainer().playerLabel, "guistrings.inventory.player");
            addGuiElement(label);
        }
    }

    protected void addSideSelectButton() {
        Button button = new Button(8, ySize - 8 - 12, 50, 12, "guistrings.inventory.setsides") {
            @Override
            protected void onPressed() {
                NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_INVENTORY_SIDE_ADJUST, getContainer().tileEntity.getPos());
            }
        };
        addGuiElement(button);
    }

    protected void addBoundsAdjustButton() {
        Button button = new Button(58, ySize - 8 - 12, 50, 12, "guistrings.automation.adjust_bounds") {
            @Override
            protected void onPressed() {
                NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_BOUNDS, getContainer().tileEntity.getPos());
            }
        };
        addGuiElement(button);
    }

    protected void addAltControlsButton() {
        Button button = new Button(108, ySize - 8 - 12, 50, 12, "guistrings.automation.alt_controls") {
            @Override
            protected void onPressed() {
                getContainer().tileEntity.openAltGui(player);
            }
        };
        addGuiElement(button);
    }

}
