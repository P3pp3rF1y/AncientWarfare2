package net.shadowmage.ancientwarfare.structure.template.build.validation.border;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class SmoothingPoint {
	private BlockPos worldPos;
	private IBlockState blockState;
	private boolean useStateForBlending = false;
	private double distanceToBorder = Double.MAX_VALUE;

	public Type getType() {
		return type;
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	public int getStructureBorderDistance() {
		return structureBorderDistance;
	}

	public SmoothingPoint getClosestBorderPoint() {
		return closestBorderPoint;
	}

	private Type type;
	private int x;
	private int z;
	private int structureBorderDistance = Integer.MAX_VALUE;
	private SmoothingPoint closestBorderPoint;

	public SmoothingPoint(int x, int z, BlockPos worldPos, Type type) {
		this.x = x;
		this.z = z;
		this.worldPos = worldPos;
		this.type = type;
	}

	public void setBlockState(IBlockState blockState) {
		this.blockState = blockState;
		useStateForBlending = true;
	}

	public double getDistanceToBorder() {
		return distanceToBorder;
	}

	public IBlockState getBlockState() {
		return blockState;
	}

	public boolean useStateForBlending() {
		return useStateForBlending;
	}

	public enum Type {
		REFERENCE_POINT("R"),
		OUTER_BORDER("O"),
		SMOOTHED_BORDER("S"),
		STRUCTURE_BORDER("B"),
		STRUCTURE_INSIDE("I");

		Type(String acronym) {
			this.acronym = acronym;
		}

		private String acronym;

		public String getAcronym() {
			return acronym;
		}
	}

	public BlockPos getSmoothedPos() {
		return worldPos;
	}

	public BlockPos getWorldPos() {
		return worldPos;
	}

	public boolean updateBorderCoordsIfCloser(int distance, SmoothingPoint coords) {
		if (distance < structureBorderDistance) {
			structureBorderDistance = distance;
			closestBorderPoint = coords;
			int xDiff = Math.abs(x - closestBorderPoint.x);
			int zDiff = Math.abs(z - closestBorderPoint.z);
			distanceToBorder = Math.sqrt(((double) xDiff * xDiff) + zDiff * zDiff);
			return true;
		}
		return false;
	}

	public boolean hasSmoothedPosSet() {
		return true;
	}
}
