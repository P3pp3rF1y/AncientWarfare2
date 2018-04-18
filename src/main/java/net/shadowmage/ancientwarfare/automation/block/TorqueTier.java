package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.util.IStringSerializable;

public enum TorqueTier implements IStringSerializable {
	LIGHT(0), MEDIUM(1), HEAVY(2);

	private int meta;

	TorqueTier(int meta) {
		this.meta = meta;
	}

	@Override
	public String getName() {
		return name().toLowerCase();
	}

	public int getMeta() {
		return meta;
	}

	public static TorqueTier byMetadata(int meta) {
		return values()[meta];
	}
}
