package net.shadowmage.ancientwarfare.core.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.owner.IOwnable;
import net.shadowmage.ancientwarfare.core.owner.Owner;

public class TileOwned extends TileUpdatable implements IOwnable {
	private Owner owner = Owner.EMPTY;

	@Override
	public final void setOwner(EntityPlayer player) {
		owner = new Owner(player);
	}

	@Override
	public final void setOwner(Owner owner) {
		this.owner = owner;
	}

	@Override
	public Owner getOwner() {
		return owner;
	}

	@Override
	public final boolean isOwner(EntityPlayer player) {
		return owner.isOwnerOrSameTeamOrFriend(player);
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		owner.serializeToNBT(tag);
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		owner = Owner.deserializeFromNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		owner = Owner.deserializeFromNBT(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		owner.serializeToNBT(tag);
		return tag;
	}
}
