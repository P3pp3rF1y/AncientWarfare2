package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerEntityBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class ContainerNpcBase<T extends NpcBase> extends ContainerEntityBase<T> {

    public ContainerNpcBase(EntityPlayer player, int x) {
        super(player, x);
    }

    public void repack() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("repack", true);
        sendDataToServer(tag);
    }

    public void setHome() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("setHome", true);
        sendDataToServer(tag);
    }

    public void clearHome() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("clearHome", true);
        sendDataToServer(tag);
    }
}
