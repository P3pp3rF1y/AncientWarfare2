package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedSpawner;

import java.util.Optional;

public class ContainerSpawnerAdvancedBlock extends ContainerSpawnerAdvancedBase {
	private static final String SPAWNER_SETTINGS_TAG = "spawnerSettings";
	private final TileAdvancedSpawner spawner;

	public ContainerSpawnerAdvancedBlock(EntityPlayer player, int x, int y, int z) {
		super(player);
		Optional<TileAdvancedSpawner> te = WorldTools.getTile(player.world, new BlockPos(x, y, z), TileAdvancedSpawner.class);
		if (te.isPresent()) {
			spawner = te.get();
			settings = spawner.getSettings();
		} else {
			throw new IllegalArgumentException("Spawner not found");
		}
	}

	@Override
	public void sendInitData() {
		if (!spawner.getWorld().isRemote) {
			NetworkHandler.sendToPlayer((EntityPlayerMP) player, getSettingPacket());
		}
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey(SPAWNER_SETTINGS_TAG)) {
			if (spawner.getWorld().isRemote) {
				settings.readFromNBT(tag.getCompoundTag(SPAWNER_SETTINGS_TAG));
				this.refreshGui();
			} else {
				spawner.getSettings().readFromNBT(tag.getCompoundTag(SPAWNER_SETTINGS_TAG));
				spawner.markDirty();
				BlockTools.notifyBlockUpdate(spawner);
			}
		}
	}
}
