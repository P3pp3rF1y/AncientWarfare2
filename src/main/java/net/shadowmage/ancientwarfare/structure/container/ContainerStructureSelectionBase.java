package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;

public class ContainerStructureSelectionBase extends ContainerBase {

    public String structureName;

    public ContainerStructureSelectionBase(EntityPlayer player) {
        super(player);
    }

    public void handleNameSelection(String name) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("structName", name);
        sendDataToServer(tag);
    }

}
