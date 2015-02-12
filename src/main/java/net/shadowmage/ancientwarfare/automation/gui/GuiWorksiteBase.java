package net.shadowmage.ancientwarfare.automation.gui;

import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteBase;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileWorksiteBoundedInventory;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

public abstract class GuiWorksiteBase extends GuiContainerBase {

    TileWorksiteBoundedInventory worksite;
    ContainerWorksiteBase container;

    public GuiWorksiteBase(ContainerBase par1Container) {
        super(par1Container, 178, 240, defaultBackground);
        worksite = ((ContainerWorksiteBase) inventorySlots).worksite;
        this.container = (ContainerWorksiteBase) par1Container;
        this.ySize = container.guiHeight + 12;
    }

    protected void addLabels() {
        Label label;
        if (container.topLabel > 0) {
            label = new Label(8, container.topLabel, "guistrings.inventory.side.top");
            addGuiElement(label);
        }
        if (container.frontLabel > 0) {
            label = new Label(8, container.frontLabel, "guistrings.inventory.front");
            addGuiElement(label);
        }
        if (container.bottomLabel > 0) {
            label = new Label(8, container.bottomLabel, "guistrings.inventory.bottom");
            addGuiElement(label);
        }
        if (container.rearLabel > 0) {
            label = new Label(8, container.rearLabel, "guistrings.inventory.rear");
            addGuiElement(label);
        }
        if (container.leftLabel > 0) {
            label = new Label(8, container.leftLabel, "guistrings.inventory.left");
            addGuiElement(label);
        }
        if (container.rightLabel > 0) {
            label = new Label(8, container.rightLabel, "guistrings.inventory.right");
            addGuiElement(label);
        }
        if (container.playerLabel > 0) {
            label = new Label(8, container.playerLabel, "guistrings.inventory.player");
            addGuiElement(label);
        }
    }

    protected void addSideSelectButton() {
        Button button = new Button(8, ySize - 8 - 12, 50, 12, "guistrings.inventory.setsides") {
            @Override
            protected void onPressed() {
                NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_INVENTORY_SIDE_ADJUST, container.worksite.xCoord, container.worksite.yCoord, container.worksite.zCoord);
            }
        };
        addGuiElement(button);
    }

    protected void addBoundsAdjustButton() {
        Button button = new Button(58, ySize - 8 - 12, 50, 12, "guistrings.automation.adjust_bounds") {
            @Override
            protected void onPressed() {
                NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_BOUNDS, container.worksite.xCoord, container.worksite.yCoord, container.worksite.zCoord);
            }
        };
        addGuiElement(button);
    }

    protected void addAltControlsButton() {
        Button button = new Button(108, ySize - 8 - 12, 50, 12, "guistrings.automation.alt_controls") {
            @Override
            protected void onPressed() {
                worksite.openAltGui(player);
            }
        };
        addGuiElement(button);
    }

}
