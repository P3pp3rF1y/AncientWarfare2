package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.tile.TileStake;

import java.util.Optional;

public class ContainerStake extends ContainerBase {
	private TileStake stake;

	public ContainerStake(EntityPlayer player, int x, int y, int z) {
		super(player);
		Optional<TileStake> te = WorldTools.getTile(player.world, new BlockPos(x, y, z), TileStake.class);
		if (te.isPresent()) {
			stake = te.get();
		} else {
			throw new IllegalArgumentException("Stake not found");
		}
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		stake.readFromNBT(tag);
		stake.getWorld().checkLight(stake.getPos());
	}

	public void updateServer() {
		sendDataToServer(stake.writeToNBT(new NBTTagCompound()));
	}

	public TileStake getStake() {
		return stake;
	}
}
