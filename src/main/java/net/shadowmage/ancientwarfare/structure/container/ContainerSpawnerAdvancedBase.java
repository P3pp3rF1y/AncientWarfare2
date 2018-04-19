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
		NetworkHandler.sendToServer(getSettingPacket());
	}

	public PacketGui getSettingPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		settings.writeToNBT(tag);

		PacketGui pkt = new PacketGui();
		pkt.setTag("spawnerSettings", tag);
		return pkt;
	}

}
