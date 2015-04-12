package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketGui;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings;

public abstract class ContainerSpawnerAdvancedBase extends ContainerBase {

    public SpawnerSettings settings;

    public ContainerSpawnerAdvancedBase(EntityPlayer player) {
        super(player);
    }

    public void sendSettingsToServer() {
        NBTTagCompound tag = new NBTTagCompound();
        settings.writeToNBT(tag);

        PacketGui pkt = new PacketGui();
        pkt.packetData.setTag("spawnerSettings", tag);
        NetworkHandler.sendToServer(pkt);
    }

}
