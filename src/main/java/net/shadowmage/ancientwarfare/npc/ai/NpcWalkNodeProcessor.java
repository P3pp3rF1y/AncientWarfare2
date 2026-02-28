package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

public class NpcWalkNodeProcessor extends WalkNodeProcessor {

	public NpcWalkNodeProcessor() {
		setCanOpenDoors(true);
		setCanEnterDoors(true);
	}

	@Override
	public int findPathOptions(PathPoint[] pathOptions, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
		int i = 0;
		int j = 0;
		PathNodeType pathnodetype = this.getPathNodeType(this.entity, currentPoint.x, currentPoint.y + 1, currentPoint.z);

		if (this.entity.getPathPriority(pathnodetype) >= 0.0F) {
			j = MathHelper.floor(Math.max(1.0F, this.entity.stepHeight));
		}

		BlockPos blockpos = (new BlockPos(currentPoint.x, currentPoint.y, currentPoint.z)).down();
		double d0 = (double) currentPoint.y - (1.0D - this.blockaccess.getBlockState(blockpos).getBoundingBox(this.blockaccess, blockpos).maxY);
		PathPoint southPoint = this.getSafePoint(currentPoint.x, currentPoint.y, currentPoint.z + 1, j, d0, EnumFacing.SOUTH);
		PathPoint westPoint = this.getSafePoint(currentPoint.x - 1, currentPoint.y, currentPoint.z, j, d0, EnumFacing.WEST);
		PathPoint eastPoint = this.getSafePoint(currentPoint.x + 1, currentPoint.y, currentPoint.z, j, d0, EnumFacing.EAST);
		PathPoint northPoint = this.getSafePoint(currentPoint.x, currentPoint.y, currentPoint.z - 1, j, d0, EnumFacing.NORTH);

		if (southPoint != null && southPoint.nodeType != PathNodeType.BLOCKED && !southPoint.visited && southPoint.distanceTo(targetPoint) < maxDistance) {
			pathOptions[i++] = southPoint;
		}

		if (westPoint != null && westPoint.nodeType != PathNodeType.BLOCKED && !westPoint.visited && westPoint.distanceTo(targetPoint) < maxDistance) {
			pathOptions[i++] = westPoint;
		}

		if (eastPoint != null && eastPoint.nodeType != PathNodeType.BLOCKED && !eastPoint.visited && eastPoint.distanceTo(targetPoint) < maxDistance) {
			pathOptions[i++] = eastPoint;
		}

		if (northPoint != null && northPoint.nodeType != PathNodeType.BLOCKED && !northPoint.visited && northPoint.distanceTo(targetPoint) < maxDistance) {
			pathOptions[i++] = northPoint;
		}

		boolean flag = northPoint == null || northPoint.nodeType == PathNodeType.OPEN || (northPoint.nodeType != PathNodeType.BLOCKED && northPoint.costMalus != 0.0F);
		boolean flag1 = southPoint == null || southPoint.nodeType == PathNodeType.OPEN || (southPoint.nodeType != PathNodeType.BLOCKED && southPoint.costMalus != 0.0F);
		boolean flag2 = eastPoint == null || eastPoint.nodeType == PathNodeType.OPEN || (eastPoint.nodeType != PathNodeType.BLOCKED && eastPoint.costMalus != 0.0F);
		boolean flag3 = westPoint == null || westPoint.nodeType == PathNodeType.OPEN || (westPoint.nodeType != PathNodeType.BLOCKED && westPoint.costMalus != 0.0F);

		if (flag && flag3) {
			PathPoint pathpoint4 = this.getSafePoint(currentPoint.x - 1, currentPoint.y, currentPoint.z - 1, j, d0, EnumFacing.NORTH);

			if (pathpoint4 != null && !pathpoint4.visited && pathpoint4.distanceTo(targetPoint) < maxDistance) {
				pathOptions[i++] = pathpoint4;
			}
		}

		if (flag && flag2) {
			PathPoint pathpoint5 = this.getSafePoint(currentPoint.x + 1, currentPoint.y, currentPoint.z - 1, j, d0, EnumFacing.NORTH);

			if (pathpoint5 != null && !pathpoint5.visited && pathpoint5.distanceTo(targetPoint) < maxDistance) {
				pathOptions[i++] = pathpoint5;
			}
		}

		if (flag1 && flag3) {
			PathPoint pathpoint6 = this.getSafePoint(currentPoint.x - 1, currentPoint.y, currentPoint.z + 1, j, d0, EnumFacing.SOUTH);

			if (pathpoint6 != null && !pathpoint6.visited && pathpoint6.distanceTo(targetPoint) < maxDistance) {
				pathOptions[i++] = pathpoint6;
			}
		}

		if (flag1 && flag2) {
			PathPoint pathpoint7 = this.getSafePoint(currentPoint.x + 1, currentPoint.y, currentPoint.z + 1, j, d0, EnumFacing.SOUTH);

			if (pathpoint7 != null && !pathpoint7.visited && pathpoint7.distanceTo(targetPoint) < maxDistance) {
				pathOptions[i++] = pathpoint7;
			}
		}

		return i;
	}

	/**
	 * Returns a point that the entity can safely move to
	 */
	@Nullable
	private PathPoint getSafePoint(int x, int y, int z, int p_186332_4_, double p_186332_5_, EnumFacing facing) {
		PathPoint pathpoint = null;
		BlockPos blockpos = new BlockPos(x, y, z);
		BlockPos blockpos1 = blockpos.down();
		double d0 = (double) y - (1.0D - this.blockaccess.getBlockState(blockpos1).getBoundingBox(this.blockaccess, blockpos1).maxY);

		if (d0 - p_186332_5_ > 1.125D) {
			return null;
		} else {
			PathNodeType pathnodetype = this.getPathNodeType(this.entity, x, y, z);
			float pathPriority = this.entity.getPathPriority(pathnodetype);
			double d1 = (double) this.entity.width / 2.0D;

			if (pathnodetype == PathNodeType.BLOCKED || pathPriority >= 0.0F) {
				pathpoint = this.openPoint(x, y, z);
				pathpoint.nodeType = pathnodetype;
				pathpoint.costMalus = Math.max(pathpoint.costMalus, pathPriority);
			}

			if (pathnodetype == PathNodeType.WALKABLE) {
				return pathpoint;
			} else {
				if ((pathpoint == null || pathnodetype == PathNodeType.BLOCKED) && p_186332_4_ > 0 && pathnodetype != PathNodeType.FENCE && pathnodetype != PathNodeType.TRAPDOOR) {
					pathpoint = this.getSafePoint(x, y + 1, z, p_186332_4_ - 1, p_186332_5_, facing);

					if (pathpoint != null && (pathpoint.nodeType == PathNodeType.OPEN || pathpoint.nodeType == PathNodeType.WALKABLE) && this.entity.width < 1.0F) {
						double d2 = (double) (x - facing.getFrontOffsetX()) + 0.5D;
						double d3 = (double) (z - facing.getFrontOffsetZ()) + 0.5D;
						AxisAlignedBB axisalignedbb = new AxisAlignedBB(d2 - d1, (double) y + 0.001D, d3 - d1, d2 + d1, (double) ((float) y + this.entity.height), d3 + d1);
						AxisAlignedBB axisalignedbb1 = this.blockaccess.getBlockState(blockpos).getBoundingBox(this.blockaccess, blockpos);
						AxisAlignedBB axisalignedbb2 = axisalignedbb.expand(0.0D, axisalignedbb1.maxY - 0.002D, 0.0D);

						if (this.entity.world.collidesWithAnyBlock(axisalignedbb2)) {
							pathpoint = null;
						}
					}
				}

				if (pathnodetype == PathNodeType.OPEN) {
					AxisAlignedBB axisalignedbb3 = new AxisAlignedBB((double) x - d1 + 0.5D, (double) y + 0.001D, (double) z - d1 + 0.5D, (double) x + d1 + 0.5D, (double) ((float) y + this.entity.height), (double) z + d1 + 0.5D);

					if (this.entity.world.collidesWithAnyBlock(axisalignedbb3)) {
						return null;
					}

					if (this.entity.width >= 1.0F) {
						PathNodeType pathnodetype1 = this.getPathNodeType(this.entity, x, y - 1, z);

						if (pathnodetype1 == PathNodeType.BLOCKED) {
							pathpoint = this.openPoint(x, y, z);
							pathpoint.nodeType = PathNodeType.WALKABLE;
							pathpoint.costMalus = Math.max(pathpoint.costMalus, pathPriority);
							return pathpoint;
						}
					}

					int i = 0;

					while (y > 0 && pathnodetype == PathNodeType.OPEN) {
						--y;

						if (i++ >= this.entity.getMaxFallHeight()) {
							return null;
						}

						pathnodetype = this.getPathNodeType(this.entity, x, y, z);
						pathPriority = this.entity.getPathPriority(pathnodetype);

						if (pathnodetype != PathNodeType.OPEN && pathPriority >= 0.0F) {
							pathpoint = this.openPoint(x, y, z);
							pathpoint.nodeType = pathnodetype;
							pathpoint.costMalus = Math.max(pathpoint.costMalus, pathPriority);
							break;
						}

						if (pathPriority < 0.0F) {
							return null;
						}
					}
				}

				return pathpoint;
			}
		}
	}

	private PathNodeType getPathNodeType(EntityLiving entitylivingIn, int x, int y, int z) {
		return this.getPathNodeType(this.blockaccess, x, y, z, entitylivingIn, this.entitySizeX, this.entitySizeY, this.entitySizeZ, this.getCanOpenDoors(), this.getCanEnterDoors());
	}

	@Override
	protected PathNodeType getPathNodeTypeRaw(IBlockAccess world, int x, int y, int z) {
		BlockPos blockpos = new BlockPos(x, y, z);
		IBlockState iblockstate = world.getBlockState(blockpos);
		Block block = iblockstate.getBlock();

		if (block instanceof BlockFenceGate) {
			return Boolean.TRUE.equals(iblockstate.getValue(BlockFenceGate.OPEN)) ? PathNodeType.DOOR_OPEN : PathNodeType.DOOR_WOOD_CLOSED;
		} else if (block instanceof BlockDoor && iblockstate.getMaterial() == Material.IRON && Boolean.FALSE.equals(iblockstate.getValue(BlockDoor.OPEN))
				&& iblockstate.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER && lowerHalfOpen(world, blockpos, iblockstate.getBlock()) //fixes vanilla issue where top half of iron door isn't marked as open when the door is open
		) {
			return PathNodeType.DOOR_OPEN;
		}

		return super.getPathNodeTypeRaw(world, x, y, z);
	}

	private boolean lowerHalfOpen(IBlockAccess world, BlockPos blockpos, Block block) {
		IBlockState lowerState = world.getBlockState(blockpos.down());
		return lowerState.getBlock() == block && lowerState.getValue(BlockDoor.OPEN);
	}
}
