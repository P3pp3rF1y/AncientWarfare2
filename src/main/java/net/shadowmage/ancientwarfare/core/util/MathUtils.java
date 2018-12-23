package net.shadowmage.ancientwarfare.core.util;

import com.google.common.collect.AbstractIterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

public class MathUtils {
	private MathUtils() {}

	private static final int NUM_X_BITS = 1 + MathHelper.log2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
	private static final int NUM_Z_BITS = NUM_X_BITS;
	private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
	private static final int Y_SHIFT = NUM_Z_BITS;
	private static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;
	private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
	private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
	private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;

	public static boolean epsilonEquals(float valueA, float valueB) {
		return Math.abs(valueB - valueA) < 1.0E-5F;
	}

	public static Iterable<Vec3i> getAllVecsInBox(Vec3i from, Vec3i to) {
		return getAllVecsInBox(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()), Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
	}

	private static Iterable<Vec3i> getAllVecsInBox(final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
		return () ->
				new AbstractIterator<Vec3i>() {
					private boolean first = true;
					private int lastPosX;
					private int lastPosY;
					private int lastPosZ;

					protected Vec3i computeNext() {
						if (this.first) {
							this.first = false;
							this.lastPosX = x1;
							this.lastPosY = y1;
							this.lastPosZ = z1;
							return new BlockPos(x1, y1, z1);
						} else if (this.lastPosX == x2 && this.lastPosY == y2 && this.lastPosZ == z2) {
							return this.endOfData();
						} else {
							if (this.lastPosX < x2) {
								++this.lastPosX;
							} else if (this.lastPosY < y2) {
								this.lastPosX = x1;
								++this.lastPosY;
							} else if (this.lastPosZ < z2) {
								this.lastPosX = x1;
								this.lastPosY = y1;
								++this.lastPosZ;
							}

							return new Vec3i(this.lastPosX, this.lastPosY, this.lastPosZ);
						}
					}
				};
	}

	public static Vec3i fromLong(long serialized) {
		int i = (int) (serialized << 64 - X_SHIFT - NUM_X_BITS >> 64 - NUM_X_BITS);
		int j = (int) (serialized << 64 - Y_SHIFT - NUM_Y_BITS >> 64 - NUM_Y_BITS);
		int k = (int) (serialized << 64 - NUM_Z_BITS >> 64 - NUM_Z_BITS);
		return new Vec3i(i, j, k);
	}

	public static long toLong(Vec3i vector) {
		return ((long) vector.getX() & X_MASK) << X_SHIFT | ((long) vector.getY() & Y_MASK) << Y_SHIFT | ((long) vector.getZ() & Z_MASK);
	}

	public static int[] toIntArray(short[] array) {
		int[] ret = new int[array.length];
		for (int i = 0; i < array.length; i++) {
			ret[i] = array[i];
		}
		return ret;
	}

	public static short[] toShortArray(int[] array) {
		short[] ret = new short[array.length];
		for (int i = 0; i < array.length; i++) {
			ret[i] = (short) array[i];
		}
		return ret;
	}
}
