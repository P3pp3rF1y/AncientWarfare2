package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.util.IStringSerializable;

import java.util.HashMap;
import java.util.Map;

public enum WoodVariant implements IStringSerializable {
	OAK(0, "oak"),
	SPRUCE(1, "spruce"),
	BIRCH(2, "birch"),
	JUNGLE(3, "jungle"),
	ACACIA(4, "acacia"),
	DARK_OAK(5, "dark_oak");

	private int meta;
	private String name;

	WoodVariant(int meta, String name) {
		this.meta = meta;
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public int getMeta() {
		return meta;
	}

	private static final Map<Integer, WoodVariant> META_TO_VARIANT = new HashMap<>();
	private static final Map<String, WoodVariant> NAME_TO_VARIANT = new HashMap<>();

	static {
		for (WoodVariant variant : WoodVariant.values()) {
			META_TO_VARIANT.put(variant.meta, variant);
			NAME_TO_VARIANT.put(variant.name, variant);
		}
	}

	public static WoodVariant byMeta(int meta) {
		return META_TO_VARIANT.getOrDefault(meta, OAK);
	}

	public static WoodVariant byName(String name) {
		return NAME_TO_VARIANT.getOrDefault(name, OAK);
	}
}
