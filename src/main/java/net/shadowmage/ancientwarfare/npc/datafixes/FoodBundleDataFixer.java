package net.shadowmage.ancientwarfare.npc.datafixes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;

public class FoodBundleDataFixer implements IFixableData {
	@Override
	public int getFixVersion() {
		return 7;
	}

	private static final String COMPONENT_NAME = "ancientwarfare:component";

	@Override
	public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
		if (COMPONENT_NAME.equals(compound.getString("id")) && compound.getShort("Damage") == 100) {
			compound.setString("id", "ancientwarfarenpc:food_bundle");
			compound.setShort("Damage", (short) 0);
		}
		return compound;
	}
}
