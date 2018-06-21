package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.stream.Collector;

public class StreamUtils {
	private StreamUtils() {}

	public static final Collector<NBTTagString, NBTTagList, NBTTagList> toNBTTagList = Collector.of(
			NBTTagList::new,
			NBTTagList::appendTag,
			(a, b) -> {
				b.forEach(a::appendTag);
				return a;
			},
			l -> l,
			Collector.Characteristics.UNORDERED);
}
