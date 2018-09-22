package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class NpcAIDoor extends EntityAIBase {
	private final EntityLiving theEntity;
	private final boolean close;
	private Set<BlockPos> doorPositions = new HashSet<>();
	private static final int RECHECK_INTERVAL = 40;
	private int doorCheckCooldown = RECHECK_INTERVAL;

	public NpcAIDoor(EntityLiving living, boolean closeBehind) {
		this.theEntity = living;
		this.close = closeBehind;
	}

	@Override
	public final boolean shouldExecute() {
		PathNavigateGround pathnavigate = (PathNavigateGround) this.theEntity.getNavigator();
		if (!pathnavigate.getEnterDoors() || pathnavigate.noPath())
			return false;

		Path path = pathnavigate.getPath();
		if (path == null) {
			return false;
		}

		if (addDoorCloseOnThePath(path))
			return true;

		if (!this.theEntity.collidedHorizontally)
			return false;
		BlockPos potentialDoorPos = new BlockPos(MathHelper.floor(this.theEntity.posX), MathHelper.floor(this.theEntity.posY), MathHelper.floor(this.theEntity.posZ));
		return findDoor(potentialDoorPos) || findDoor(potentialDoorPos.up());
	}

	private boolean addDoorCloseOnThePath(Path path) {
		for (int i = Math.max(path.getCurrentPathIndex() - 1, 0); i < Math.min(path.getCurrentPathIndex() + 2, path.getCurrentPathLength()); ++i) {
			PathPoint pathpoint = path.getPathPointFromIndex(i);

			if (this.theEntity.getDistanceSq(pathpoint.x + 0.5D, this.theEntity.posY, pathpoint.z + 0.5D) <= 1D) {
				BlockPos potentialDoorPos = new BlockPos(pathpoint.x, pathpoint.y, pathpoint.z);
				if (findDoor(potentialDoorPos)) {
					interactWithDoor(potentialDoorPos, true);
					return true;
				} else if (findDoor(potentialDoorPos.up())) {
					interactWithDoor(potentialDoorPos.up(), true);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void updateTask() {
		super.updateTask();

		closeTooFarAwayDoor();
		recheckDoorOpen();
		addDoorCloseOnThePath();
	}

	private void closeTooFarAwayDoor() {
		Iterator<BlockPos> it = doorPositions.iterator();
		while (it.hasNext()) {
			BlockPos doorPos = it.next();
			if (!(isCloseToDoor(doorPos) || isFriendlyInDoor(doorPos))) {
				it.remove();
				if (close) {
					interactWithDoor(doorPos, false);
				}
			}
		}
	}

	private void addDoorCloseOnThePath() {
		Path path = this.theEntity.getNavigator().getPath();
		if (path == null) {
			return;
		}
		addDoorCloseOnThePath(path);
	}

	private void recheckDoorOpen() {
		if (doorCheckCooldown <= 0) {
			doorCheckCooldown = RECHECK_INTERVAL;
			doorPositions.forEach(doorPos -> interactWithDoor(doorPos, true));
		} else {
			doorCheckCooldown--;
		}
	}

	@Override
	public final boolean shouldContinueExecuting() {
		return !doorPositions.isEmpty();
	}

	private boolean isFriendlyInDoor(BlockPos doorPos) {
		return !theEntity.world.getEntitiesWithinAABB(NpcBase.class,
				new AxisAlignedBB(new Vec3d(doorPos).addVector(0.5D, 0.5D, 0.5D), new Vec3d(doorPos).addVector(0.5D, 0.5D, 0.5D)).grow(1.1D),
				n -> n != null && !n.isHostileTowards(theEntity)).isEmpty();
	}

	private boolean isCloseToDoor(BlockPos doorPos) {
		return doorPos.distanceSqToCenter(theEntity.posX, theEntity.posY, theEntity.posZ) <= 1D;
	}

	private boolean isDoor(BlockPos potentialDoorPos) {
		IBlockState doorState = theEntity.world.getBlockState(potentialDoorPos);
		if (doorState.getBlock() instanceof BlockDoor) {
			if (doorState.getMaterial() == Material.WOOD) {
				return true;
			}
		} else if (doorState.getBlock() instanceof BlockFenceGate) {
			return true;
		}
		return false;
	}

	private boolean findDoor(BlockPos potentialDoorPos) {
		if (isDoor(potentialDoorPos) && !doorPositions.contains(potentialDoorPos)) {
			doorPositions.add(potentialDoorPos);
			return true;
		}
		return false;
	}

	private void interactWithDoor(BlockPos doorPos, boolean isOpening) {
		IBlockState doorState = theEntity.world.getBlockState(doorPos);
		if (doorState.getBlock() instanceof BlockDoor) {
			((BlockDoor) doorState.getBlock()).toggleDoor(theEntity.world, doorPos, isOpening);
		} else if (doorState.getBlock() instanceof BlockFenceGate) {
			interactWithFenceGate(doorPos, isOpening, doorState);
		}
	}

	private void interactWithFenceGate(BlockPos doorPos, boolean isOpening, IBlockState doorState) {
		boolean fenceGateOpen = doorState.getValue(BlockFenceGate.OPEN);
		if (isOpening) {
			if (!fenceGateOpen) {
				EnumFacing entityFacing = EnumFacing.fromAngle((double) theEntity.rotationYaw);
				openFenceGate(doorState, doorPos, entityFacing);
				IBlockState state = theEntity.world.getBlockState(doorPos.up());
				if (state.getBlock() instanceof BlockFenceGate) {
					openFenceGate(state, doorPos.up(), entityFacing);
				}
			}
		} else {
			doorState = doorState.withProperty(BlockFenceGate.OPEN, false);
			theEntity.world.setBlockState(doorPos, doorState, 10);
			IBlockState state = theEntity.world.getBlockState(doorPos.up());
			if (state.getBlock() instanceof BlockFenceGate) {
				state = state.withProperty(BlockFenceGate.OPEN, false);
				theEntity.world.setBlockState(doorPos.up(), state, 10);
			}
		}
		theEntity.world.playEvent(null, doorState.getValue(BlockFenceGate.OPEN) ? 1008 : 1014, doorPos, 0);
	}

	private void openFenceGate(IBlockState state, BlockPos pos, EnumFacing entityFacing) {
		IBlockState updatedState = state;
		if (updatedState.getValue(BlockFenceGate.FACING) == entityFacing.getOpposite()) {
			updatedState = updatedState.withProperty(BlockFenceGate.FACING, entityFacing);
		}

		updatedState = updatedState.withProperty(BlockFenceGate.OPEN, true);
		theEntity.world.setBlockState(pos, updatedState, 10);
	}
}
