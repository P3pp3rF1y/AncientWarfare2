package net.shadowmage.ancientwarfare.structure.render.property;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.IStringSerializable;

import java.util.Map;

public enum TopBottomPart implements IStringSerializable {
	TOP("top", 0),
	BOTTOM("bottom", 1);

	private String name;
	private int meta;

	TopBottomPart(String name, int meta) {
		this.name = name;
		this.meta = meta;
	}

	@Override
	public String getName() {
		return name;
	}

	public int getMeta() {
		return meta;
	}

	private static final Map<Integer, TopBottomPart> META_TO_PART;

	static {
		ImmutableMap.Builder<Integer, TopBottomPart> builder = new ImmutableMap.Builder<>();
		for (TopBottomPart part : values()) {
			builder.put(part.getMeta(), part);
		}
		META_TO_PART = builder.build();
	}

	public static TopBottomPart byMeta(int meta) {
		return META_TO_PART.get(meta);
	}
}
