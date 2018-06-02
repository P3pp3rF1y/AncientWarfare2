package net.shadowmage.ancientwarfare.core.datafixes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;

public class VehicleOwnerFixer implements IFixableData {
	@Override
	public int getFixVersion() {
		return 1;
	}

	@Override
	public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
		if (compound.getString("id").equals("ancientwarfarevehicle:vehicle")) {
			compound.setLong("ownerIdLeast", compound.getLong("ownerUuidLeast"));
			compound.setLong("ownerIdMost", compound.getLong("ownerUuidMost"));
		}
		return compound;
	}
}
