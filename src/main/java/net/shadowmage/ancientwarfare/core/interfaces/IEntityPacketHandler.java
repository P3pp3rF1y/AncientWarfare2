package net.shadowmage.ancientwarfare.core.interfaces;

import net.minecraft.nbt.NBTTagCompound;

/*
 * blind entity packet handling
 * should be implemented by any entity that is a target of
 * network packets
 *
 * @author Shadowmage
 */
public interface IEntityPacketHandler {

	public void handlePacketData(NBTTagCompound tag);

}
