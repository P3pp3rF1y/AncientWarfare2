package net.shadowmage.ancientwarfare.structure.util;

import net.minecraft.util.EnumFacing;
import net.shadowmage.ancientwarfare.core.util.Trig;

public class RotationLimit {
	public static final RotationLimit NO_LIMIT = new RotationLimit() {
		@Override
		public boolean isWithinLimit(float rotationYaw) {
			return true;
		}
	};
	private float min = 0;
	private float max = 0;

	private RotationLimit() {}

	public RotationLimit(float min, float max) {
		this.min = min;
		this.max = max;
	}

	public boolean isWithinLimit(float rotationYaw) {
		return Trig.isAngleBetween(rotationYaw, min, max);
	}

	public float restrictToLimit(float rotationYaw) {
		float minDiff = Math.abs(Trig.getAngleDiffSigned(rotationYaw, min));
		float maxDiff = Math.abs(Trig.getAngleDiffSigned(rotationYaw, max));
		return minDiff < maxDiff ? min : max;
	}

	public float getMidPoint() {
		return min + ((max - min) / 2);
	}

	public static class FacingThreeQuarters extends RotationLimit {
		public FacingThreeQuarters(EnumFacing facing) {
			super(facing.getHorizontalAngle() - 135, facing.getHorizontalAngle() + 135);
		}
	}

	public static class FacingQuarter extends RotationLimit {
		public FacingQuarter(EnumFacing facing) {
			super(facing.getHorizontalAngle() - 45, facing.getHorizontalAngle() + 45);
		}
	}
}
