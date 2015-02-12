package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;

public class GuiWorksiteAutoCrafting extends GuiContainerBase {

    public GuiWorksiteAutoCrafting(ContainerBase par1Container) {
        super(par1Container, 176, 216, defaultBackground);
    }

    @Override
    public void initElements() {
        ItemSlot bookSlotIcon = new ItemSlot(8, 8, new ItemStack(AWItems.researchBook), this);
        bookSlotIcon.setRenderTooltip(false).setHighlightOnMouseOver(false).setRenderSlotBackground(false).setRenderItemQuantity(false);
        addGuiElement(bookSlotIcon);

        Button button = new Button(143 - 18, 44, 36, 12, "guistrings.automation.craft") {
            @Override
            protected void onPressed() {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setBoolean("craft", true);
                sendDataToContainer(tag);
            }
        };
        addGuiElement(button);
    }

    @Override
    public void setupElements() {

    }

}
