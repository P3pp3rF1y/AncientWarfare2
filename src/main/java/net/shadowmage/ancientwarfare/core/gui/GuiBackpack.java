package net.shadowmage.ancientwarfare.core.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBackpack;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;

public class GuiBackpack extends GuiContainerBase {
    ContainerBackpack container;

    public GuiBackpack(ContainerBase container) {
        super(container, 178, 192, defaultBackground);
        this.container = (ContainerBackpack) container;
        this.ySize = this.container.guiHeight;
    }

    @Override
    public void initElements() {
    }

    @Override
    public void setupElements() {
    }

    @Override
    public void handleKeyboardInput() {
        // TODO Auto-generated method stub
        super.handleKeyboardInput();
    }

    @Override
    protected boolean checkHotbarKeys(int keyCode)//this code handles whether to allow the backpack to be moved from its slot via the number keys
    {
        boolean callSuper = true;
        for (int slot = 0; slot < 9; slot++) {
            if (keyCode == this.mc.gameSettings.keyBindsHotbar[slot].getKeyCode() && slot == container.backpackSlotIndex) {
                callSuper = false;
            }
        }
        return callSuper && super.checkHotbarKeys(keyCode);
    }

}
