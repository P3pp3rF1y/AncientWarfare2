package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
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

public class NpcAIDoor extends NpcAI<NpcBase> {
	private final boolean close;
	private final Set<BlockPos> doorPositions = new HashSet<>();
	private static final int RECHECK_INTERVAL = 40;
	private int doorCheckCooldown = RECHECK_INTERVAL;

	public NpcAIDoor(NpcBase npc, boolean closeBehind) {
		super(npc);
		close = closeBehind;
	}

	@Override
	public final boolean shouldExecute() {
		if (!super.shouldExecute()) {
			return false;
		}

		if (!doorPositions.isEmpty()) {
			return true;
		}

		PathNavigateGround pathnavigate = (PathNavigateGround) npc.getNavigator();
		if (!pathnavigate.getEnterDoors() || pathnavigate.noPath()) {
			return false;
		}

		Path path = pathnavigate.getPath();
		if (path == null) {
			return false;
		}

		if (addDoorCloseOnThePath(path)) {
			return true;
		}

		if (!npc.collidedHorizontally) {
			return false;
		}
		BlockPos potentialDoorPos = new BlockPos(MathHelper.floor(npc.posX), MathHelper.floor(npc.posY), MathHelper.floor(npc.posZ));
		return findDoor(potentialDoorPos) || findDoor(potentialDoorPos.up());
	}

	private boolean addDoorCloseOnThePath(Path path) {
		for (int i = Math.max(path.getCurrentPathIndex() - 1, 0); i < Math.min(path.getCurrentPathIndex() + 2, path.getCurrentPathLength()); ++i) {
			PathPoint pathpoint = path.getPathPointFromIndex(i);

			if (npc.getDistanceSq(pathpoint.x + 0.5D, npc.posY, pathpoint.z + 0.5D) <= 1.5D) {
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
		Path path = npc.getNavigator().getPath();
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

	private boolean isFriendlyInDoor(BlockPos doorPos) {
		Vec3d doorCenter = new Vec3d(doorPos).addVector(0.5D, 0.5D, 0.5D);
		return !npc.world.getEntitiesWithinAABB(NpcBase.class,
				new AxisAlignedBB(doorCenter.x, doorCenter.y, doorCenter.z, doorCenter.x, doorCenter.y, doorCenter.z).grow(2.1D),
				n -> n != null && !n.isHostileTowards(npc)).isEmpty();
	}

	private boolean isCloseToDoor(BlockPos doorPos) {
		return doorPos.distanceSqToCenter(npc.posX, npc.posY, npc.posZ) <= 2D;
	}

	private boolean isDoor(BlockPos potentialDoorPos) {
		IBlockState doorState = npc.world.getBlockState(potentialDoorPos);
		if (doorState.getBlock() instanceof BlockDoor) {
			return doorState.getMaterial() == Material.WOOD;
		} else {
			return doorState.getBlock() instanceof BlockFenceGate;
		}
	}

	private boolean findDoor(BlockPos potentialDoorPos) {
		if (isDoor(potentialDoorPos) && !doorPositions.contains(potentialDoorPos)) {
			doorPositions.add(potentialDoorPos);
			return true;
		}
		return false;
	}

	private void interactWithDoor(BlockPos doorPos, boolean isOpening) {
		IBlockState doorState = npc.world.getBlockState(doorPos);
		if (doorState.getBlock() instanceof BlockDoor) {
			((BlockDoor) doorState.getBlock()).toggleDoor(npc.world, doorPos, isOpening);
		} else if (doorState.getBlock() instanceof BlockFenceGate) {
			interactWithFenceGate(doorPos, isOpening, doorState);
		}
	}

	private void interactWithFenceGate(BlockPos doorPos, boolean isOpening, IBlockState doorState) {
		boolean fenceGateOpen = doorState.getValue(BlockFenceGate.OPEN);
		if (isOpening) {
			if (!fenceGateOpen) {
				EnumFacing entityFacing = EnumFacing.fromAngle(npc.rotationYaw);
				openFenceGate(doorState, doorPos, entityFacing);
				IBlockState state = npc.world.getBlockState(doorPos.up());
				if (state.getBlock() instanceof BlockFenceGate) {
					openFenceGate(state, doorPos.up(), entityFacing);
				}
			}
		} else {
			doorState = doorState.withProperty(BlockFenceGate.OPEN, false);
			npc.world.setBlockState(doorPos, doorState, 10);
			IBlockState state = npc.world.getBlockState(doorPos.up());
			if (state.getBlock() instanceof BlockFenceGate) {
				state = state.withProperty(BlockFenceGate.OPEN, false);
				npc.world.setBlockState(doorPos.up(), state, 10);
			}
		}
		npc.world.playEvent(null, Boolean.TRUE.equals(doorState.getValue(BlockFenceGate.OPEN)) ? 1008 : 1014, doorPos, 0);
	}

	private void openFenceGate(IBlockState state, BlockPos pos, EnumFacing entityFacing) {
		IBlockState updatedState = state;
		if (updatedState.getValue(BlockHorizontal.FACING) == entityFacing.getOpposite()) {
			updatedState = updatedState.withProperty(BlockHorizontal.FACING, entityFacing);
		}

		updatedState = updatedState.withProperty(BlockFenceGate.OPEN, true);
		npc.world.setBlockState(pos, updatedState, 10);
	}
}
