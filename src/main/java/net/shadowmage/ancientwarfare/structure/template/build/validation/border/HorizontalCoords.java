package net.shadowmage.ancientwarfare.structure.template.build.validation.border;

import jdk.nashorn.internal.ir.annotations.Immutable;

@Immutable
public class HorizontalCoords {
	private int x;
	private int z;

	public HorizontalCoords(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	public HorizontalCoords add(HorizontalCoords offset) {
		return new HorizontalCoords(this.x + offset.x, this.z + offset.z);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		HorizontalCoords that = (HorizontalCoords) o;

		return x == that.x && z == that.z;
	}

	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + z;
		return result;
	}
}
