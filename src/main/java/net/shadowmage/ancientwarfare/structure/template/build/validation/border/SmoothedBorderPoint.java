package net.shadowmage.ancientwarfare.structure.template.build.validation.border;

import net.minecraft.util.math.BlockPos;

public class SmoothedBorderPoint extends SmoothingPoint {
	private BlockPos smoothedPos;
	private boolean smoothedPosSet = false;

	public SmoothedBorderPoint(int x, int z, BlockPos pos) {
		super(x, z, pos, Type.SMOOTHED_BORDER);
		smoothedPos = pos;
	}

	public void setSmoothedPos(BlockPos smoothedPos) {
		this.smoothedPos = smoothedPos;
		smoothedPosSet = true;
	}

	@Override
	public BlockPos getSmoothedPos() {
		return smoothedPos;
	}

	@Override
	public boolean hasSmoothedPosSet() {
		return smoothedPosSet;
	}
}
