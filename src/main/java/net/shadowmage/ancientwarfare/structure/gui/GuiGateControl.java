package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;

public class GuiGateControl extends GuiContainerBase {

    public GuiGateControl(ContainerBase par1Container) {
        super(par1Container, 55 + 8 + 8, 12 + 8 + 8, defaultBackground);
    }

    @Override
    public void initElements() {
        Button button = new Button(8, 8, 55, 12, "guistrings.gate.repack") {
            @Override
            protected void onPressed() {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setBoolean("repack", true);
                sendDataToContainer(tag);
                closeGui();
            }
        };
        addGuiElement(button);
    }

    @Override
    public void setupElements() {

    }

}
