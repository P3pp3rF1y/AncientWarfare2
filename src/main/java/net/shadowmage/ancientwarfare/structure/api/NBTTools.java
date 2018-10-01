package net.shadowmage.ancientwarfare.structure.api;

public class NBTTools {
	private NBTTools() {}

	private static int[] parseIntArray(String csv) {
		String[] splits = csv.split(",");
		int[] array = new int[splits.length];
		for (int i = 0; i < splits.length; i++) {
			array[i] = Integer.parseInt(splits[i].trim());
		}
		return array;
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
}
