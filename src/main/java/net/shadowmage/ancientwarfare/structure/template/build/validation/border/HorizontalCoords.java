package net.shadowmage.ancientwarfare.structure.template.build.validation.border;

import com.google.common.collect.ImmutableSet;

import javax.annotation.concurrent.Immutable;
import java.util.Set;

@Immutable
public class HorizontalCoords {
	static final Set<HorizontalCoords> ADJACENT_OFFSETS = ImmutableSet.of(new HorizontalCoords(-1, 0), new HorizontalCoords(1, 0),
			new HorizontalCoords(0, -1), new HorizontalCoords(0, 1));
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

	public HorizontalCoords getOpposite() {
		return new HorizontalCoords(-x, -z);
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

	public Set<HorizontalCoords> getPerpendicular() {
		return ImmutableSet.of(new HorizontalCoords(z, x), new HorizontalCoords(z, -x));
	}
}
