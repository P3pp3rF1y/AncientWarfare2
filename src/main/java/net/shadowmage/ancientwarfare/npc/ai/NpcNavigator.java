package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ChunkCache;

import javax.annotation.Nullable;

public class NpcNavigator extends PathNavigateGround {
	private final EntityLiving entity;
	private final WalkNodeProcessor nodeProcessor = new NpcWalkNodeProcessor();

	public NpcNavigator(EntityLiving living) {
		super(living, living.world);
		entity = living;
	}

	@Override
	public void setCanSwim(boolean value) {
		super.setCanSwim(value);
		nodeProcessor.setCanSwim(value);
	}

	@Override
	public void setEnterDoors(boolean value) {
		super.setEnterDoors(value);
		nodeProcessor.setCanEnterDoors(value);
	}

	public void onWorldChange() {
		world = entity.world;
	}

	@Nullable
	@Override
	public Path getPathToPos(BlockPos pos) {
		return !canNavigate() ? null : pathToXYZ(pos);
	}

	@Nullable
	@Override
	public Path getPathToEntityLiving(Entity target) {
		return !canNavigate() ? null : pathToEntity(target);
	}

	@Override
	public boolean setPath(@Nullable Path path, double speed) {
		if (hasMount()) {
			//noinspection ConstantConditions
			((EntityLiving) entity.getRidingEntity()).getNavigator().setPath(path, speed);
		}
		return super.setPath(path, speed);
	}

	@Override
	public void clearPath() {
		if (hasMount()) {
			//noinspection ConstantConditions
			((EntityLiving) entity.getRidingEntity()).getNavigator().clearPath();
		}
		super.clearPath();
	}

	@Override
	public void onUpdateNavigation() {
		super.onUpdateNavigation();
		if (!noPath() && hasMount()) {
			//noinspection ConstantConditions
			((EntityLiving) entity.getRidingEntity()).getNavigator().onUpdateNavigation();
		}
	}

	private boolean hasMount() {
		return entity.getRidingEntity() instanceof EntityLiving;
	}

	private EntityLiving mountOrEntity() {
		//noinspection ConstantConditions
		return hasMount() ? (EntityLiving) entity.getRidingEntity() : entity;
	}

	@Nullable
	private Path pathToEntity(Entity target) {
		ChunkCache chunkcache = cachePath(1, 16);
		Path pathentity = (new PathFinder(nodeProcessor).findPath(chunkcache, mountOrEntity(), target, getPathSearchRange()));
		world.profiler.endSection();
		return pathentity;
	}

	@Nullable
	private Path pathToXYZ(BlockPos pos) {
		ChunkCache chunkcache = cachePath(0, 8);
		Path pathentity = (new PathFinder(nodeProcessor).findPath(chunkcache, mountOrEntity(), pos, getPathSearchRange()));
		world.profiler.endSection();
		return pathentity;
	}

	private ChunkCache cachePath(int h, int r) {
		world.profiler.startSection("pathfind");
		int i = MathHelper.floor(entity.posX);
		int j = MathHelper.floor(entity.posY + h);
		int k = MathHelper.floor(entity.posZ);
		int l = (int) (getPathSearchRange() + r);
		return new ChunkCache(world, new BlockPos(i - l, j - l, k - l), new BlockPos(i + l, j + l, k + l), 0);
	}

	/*
	 * Whether pathing can be done
	 */
	@Override
	protected boolean canNavigate() {
		return super.canNavigate() || hasMount();
	}

	/*
	 * Returns true when an entity could stand at a position, including solid blocks under the entire entity.
	 */
	@Override
	protected boolean isSafeToStandAt(int xOffset, int yOffset, int zOffset, int xSize, int ySize, int zSize, Vec3d origin, double vecX, double vecZ) {
		int k1 = xOffset - xSize / 2;
		int l1 = zOffset - zSize / 2;

		if (!isPositionClear(k1, yOffset, l1, xSize, ySize, zSize, origin, vecX, vecZ)) {
			return false;
		} else {
			for (int i2 = k1; i2 < k1 + xSize; ++i2) {
				for (int j2 = l1; j2 < l1 + zSize; ++j2) {
					double d2 = (double) i2 + 0.5D - origin.x;
					double d3 = (double) j2 + 0.5D - origin.z;

					if (d2 * vecX + d3 * vecZ >= 0.0D) {
						Material material = world.getBlockState(new BlockPos(i2, yOffset - 1, j2)).getMaterial();

						if (material == Material.AIR || material == Material.LAVA || material == Material.FIRE || material == Material.CACTUS) {
							return false;
						}

						if (material == Material.WATER && !entity.isInWater()) {
							return false;
						}
					}
				}
			}

			return true;
		}
	}

	@Override
	public String toString() {
		String result;
		if (noPath()) {
			result = "No Path " + (getPath() != null ? getPath().getCurrentPathLength() : "");
		} else {
			result = "Path to " + getPath().getPathPointFromIndex(getPath().getCurrentPathIndex()).toString();
		}
		if (hasMount() && !((EntityLiving) entity.getRidingEntity()).getNavigator().noPath()) {
			Path path = ((EntityLiving) entity.getRidingEntity()).getNavigator().getPath();
			result += "AND Mount path to " + path.getPathPointFromIndex(path.getCurrentPathIndex()).toString();
		}
		return result;
	}
}
