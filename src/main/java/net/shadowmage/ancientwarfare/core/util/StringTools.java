package net.shadowmage.ancientwarfare.core.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.StringJoiner;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class StringTools {
	private StringTools() {}

	public static String getCSVStringForArray(int[] values) {
		return StringUtils.join(ArrayUtils.toObject(values), ",");
	}

	public static String getCSVValueFor(Set<String> values) {
		return String.join(",", values);
	}

	/*
	 * splits test at regex, returns parsed int array from csv value of remaining string
	 * returns size 1 int array if no valid split is found
	 */
	public static int[] safeParseIntArray(String regex, String test) {
		String[] splits = test.split(regex);
		if (splits.length > 1) {
			return parseIntArray(splits[1]);
		}
		return new int[0];
	}

	public static int[] parseIntArray(String csv) {
		if (csv.trim().isEmpty()) {
			return new int[0];
		}
		String[] splits = csv.split(",");
		int[] array = new int[splits.length];
		for (int i = 0; i < splits.length; i++) {
			array[i] = Integer.parseInt(splits[i].trim());
		}
		return array;
	}

	public static short[] parseShortArray(String csv) {
		String[] splits = csv.split(",");
		short[] array = new short[splits.length];
		for (int i = 0; i < splits.length; i++) {
			array[i] = Short.parseShort(splits[i].trim());
		}
		return array;
	}

	/*
	 * returns the value after a split at regex, or false
	 */
	public static boolean safeParseBoolean(String regex, String test) {
		String[] split = test.split(regex);
		return split.length > 1 && Boolean.parseBoolean(split[1].trim());
	}

	public static float safeParseFloat(String regex, String test) {
		String[] split = test.trim().split(regex);
		if (split.length > 1) {
			return safeParseFloat(split[1]);
		}
		return 0;
	}

	public static float safeParseFloat(String val) {
		try {
			return Float.parseFloat(val.trim());
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}

	/*
	 * returns a value after a split at regex, or an empty string
	 */
	public static String safeParseString(String regex, String test) {
		String[] split = test.split(regex);
		if (split.length > 1) {
			return split[1];
		}
		return "";
	}

	/*
	 * returns a value after a split at regex, or zero (0)
	 */
	public static int safeParseInt(String regex, String test) {
		String[] split = test.split(regex);
		if (split.length > 1) {
			return Integer.parseInt(split[1].trim());
		}
		return 0;
	}

	public static void writeString(ByteBuf out, String string) {
		byte[] nameBytes = string.getBytes();
		out.writeShort(nameBytes.length);
		out.writeBytes(nameBytes);
	}

	public static String readString(ByteBuf in) {
		short len = in.readShort();
		byte[] nameBytes = new byte[len];
		in.readBytes(nameBytes);
		return new String(nameBytes);
	}

	public static String formatPos(BlockPos pos) {
		return String.format("X:%d Y:%d Z:%d", pos.getX(), pos.getY(), pos.getZ());
	}

	public static Set<String> parseStringSet(String valueString) {
		return Collections.list(new StringTokenizer(valueString, ",")).stream().map(token -> (String) token).collect(Collectors.toSet());
	}

	public static String joinElements(String delimiter, Collection<String> stringCollection) {
		StringJoiner joiner = new StringJoiner(delimiter);
		stringCollection.forEach(joiner::add);
		return joiner.toString();
	}
}
