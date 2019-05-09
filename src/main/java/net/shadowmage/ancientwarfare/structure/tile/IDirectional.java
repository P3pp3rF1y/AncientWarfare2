package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.util.EnumFacing;

public interface IDirectional {
	EnumFacing getFacing();

	void setFacing(EnumFacing facing);
}
