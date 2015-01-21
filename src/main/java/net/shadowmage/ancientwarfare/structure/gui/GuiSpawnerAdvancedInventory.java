package net.shadowmage.ancientwarfare.structure.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.structure.container.ContainerSpawnerAdvancedInventoryBase;

public class GuiSpawnerAdvancedInventory extends GuiContainerBase {

    public GuiSpawnerAdvancedInventory(ContainerBase par1Container) {
        super(par1Container, 256, 240, defaultBackground);
    }

    @Override
    public void initElements() {

    }

    @Override
    public void setupElements() {

    }

    @Override
    protected boolean onGuiCloseRequested() {
        ((ContainerSpawnerAdvancedInventoryBase) inventorySlots).sendSettingsToServer();
        return true;
    }

}
