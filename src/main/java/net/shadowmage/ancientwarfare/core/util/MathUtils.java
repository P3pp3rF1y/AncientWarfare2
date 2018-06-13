package net.shadowmage.ancientwarfare.core.util;

public class MathUtils {
	private MathUtils() {}

	public static boolean epsilonEquals(float valueA, float valueB) {
		return Math.abs(valueB - valueA) < 1.0E-5F;
	}

}
