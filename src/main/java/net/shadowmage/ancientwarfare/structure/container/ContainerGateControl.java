package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerEntityBase;
import net.shadowmage.ancientwarfare.core.owner.Owner;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;

public class ContainerGateControl extends ContainerEntityBase<EntityGate> {
	private static final String OWNER_TAG = "owner";

	public ContainerGateControl(EntityPlayer player, int x, int y, int z) {
		super(player, x);
	}

	@Override
	public void sendInitData() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString(OWNER_TAG, entity.getOwner().getName());
		sendDataToClient(tag);
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey("repack")) {
			entity.repackEntity();
		} else if (tag.hasKey(OWNER_TAG)) {
			String owner = tag.getString(OWNER_TAG);
			entity.setOwner(owner.isEmpty() ? Owner.EMPTY : new Owner(entity.world, tag.getString(OWNER_TAG)));
			refreshGui();
		}
	}

	public void repackGate() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("repack", true);
		sendDataToServer(tag);
	}

	public void updateOwner(String newOwner) {
		if (!entity.getOwner().getName().equals(newOwner)) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString(OWNER_TAG, newOwner);
			sendDataToServer(tag);
		}
	}
}
