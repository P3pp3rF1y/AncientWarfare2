package net.shadowmage.ancientwarfare.core.interfaces;

import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.gui.elements.Tooltip;

public interface ITooltipRenderer {

    public void handleItemStackTooltipRender(ItemStack itemStack, int mouseX, int mouseY);

    public void handleElementTooltipRender(Tooltip tooltip, int mouseX, int mouseY);

}
