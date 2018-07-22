package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketGui;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedSpawner;

import java.util.Optional;

public class ContainerSpawnerAdvancedInventoryBlock extends ContainerSpawnerAdvancedInventoryBase {
	private static final String SPAWNER_SETTINGS_TAG = "spawnerSettings";
	private TileAdvancedSpawner spawner;

	public ContainerSpawnerAdvancedInventoryBlock(EntityPlayer player, int x, int y, int z) {
		super(player, x, y, z);

		Optional<TileAdvancedSpawner> te = WorldTools.getTile(player.world, new BlockPos(x, y, z), TileAdvancedSpawner.class);
		if (!player.world.isRemote && te.isPresent()) {
			spawner = te.get();
			settings = spawner.getSettings();
		} else {
			settings = SpawnerSettings.getDefaultSettings();
		}
		inventory = settings.getInventory();
		this.addSettingsInventorySlots();
		this.addPlayerSlots(8, 70, 8);
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return spawner.getDistanceSq(var1.posX, var1.posY, var1.posZ) <= 64D;
	}

	@Override
	public void sendInitData() {
		if (!player.world.isRemote) {
			sendSettingsToClient();
		}
	}

	private void sendSettingsToClient() {
		NBTTagCompound tag = new NBTTagCompound();
		settings.writeToNBT(tag);

		PacketGui pkt = new PacketGui();
		pkt.setTag(SPAWNER_SETTINGS_TAG, tag);
		NetworkHandler.sendToPlayer((EntityPlayerMP) player, pkt);
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey(SPAWNER_SETTINGS_TAG)) {
			if (player.world.isRemote) {
				settings.readFromNBT(tag.getCompoundTag(SPAWNER_SETTINGS_TAG));
				this.refreshGui();
			} else {
				spawner.readFromNBT(tag);
				BlockTools.notifyBlockUpdate(spawner);
			}
		}
	}

}
