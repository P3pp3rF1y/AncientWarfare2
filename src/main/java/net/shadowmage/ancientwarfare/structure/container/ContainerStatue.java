package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.tile.EntityStatueInfo;
import net.shadowmage.ancientwarfare.structure.tile.TileStatue;

import java.util.Optional;

public class ContainerStatue extends ContainerBase {
	private TileStatue statue;
	public ContainerStatue(EntityPlayer player, int x, int y, int z) {
		super(player);
		Optional<TileStatue> te = WorldTools.getTile(player.world, new BlockPos(x, y, z), TileStatue.class);
		if (!te.isPresent()) {
			throw new IllegalArgumentException("Statue not found");
		}
		statue = te.get();
	}

	public EntityStatueInfo getStatueInfo() {
		return statue.getEntityStatueInfo();
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		statue.getEntityStatueInfo().deserializeNBT(tag);
		statue.markDirty();
	}

	public void updateServer() {
		sendDataToServer(statue.getEntityStatueInfo().serializeNBT(new NBTTagCompound()));
	}
}
