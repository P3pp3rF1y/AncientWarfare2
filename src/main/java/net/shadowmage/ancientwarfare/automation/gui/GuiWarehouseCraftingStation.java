package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;

public class GuiWarehouseCraftingStation extends GuiContainerBase {

    public GuiWarehouseCraftingStation(ContainerBase par1Container) {
        super(par1Container, 176, 154, defaultBackground);
    }

    @Override
    public void initElements() {
        ItemSlot bookSlotIcon = new ItemSlot(8, 8, new ItemStack(AWItems.researchBook), this);
        bookSlotIcon.setRenderTooltip(false).setHighlightOnMouseOver(false).setRenderSlotBackground(false).setRenderItemQuantity(false);
        addGuiElement(bookSlotIcon);
    }

    @Override
    public void setupElements() {

    }

}
