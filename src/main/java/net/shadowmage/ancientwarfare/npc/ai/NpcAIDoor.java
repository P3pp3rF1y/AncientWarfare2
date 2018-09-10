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
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIDoor extends EntityAIBase {
	private final EntityLiving theEntity;
	private final boolean close;
	private BlockPos doorPos = new BlockPos(0, 0, 0);
	private IBlockState doorState;

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

		for (int i = 0; i < Math.min(path.getCurrentPathIndex() + 2, path.getCurrentPathLength()); ++i) {
			PathPoint pathpoint = path.getPathPointFromIndex(i);

			if (this.theEntity.getDistanceSq(pathpoint.x, this.theEntity.posY, pathpoint.z) <= 2.25D) {
				BlockPos potentialDoorPos = new BlockPos(pathpoint.x, pathpoint.y, pathpoint.z);
				if (findDoor(potentialDoorPos) || findDoor(potentialDoorPos.up())) {
					return true;
				}
			}
		}

		if (!this.theEntity.collidedHorizontally)
			return false;
		BlockPos potentialDoorPos = new BlockPos(MathHelper.floor(this.theEntity.posX), MathHelper.floor(this.theEntity.posY), MathHelper.floor(this.theEntity.posZ));
		return findDoor(potentialDoorPos) || findDoor(potentialDoorPos.up());
	}

	@Override
	public final boolean shouldContinueExecuting() {
		return isCloseToDoor() || isFriendlyInDoor();
	}

	private boolean isFriendlyInDoor() {
		return !theEntity.world.getEntitiesWithinAABB(NpcBase.class, new AxisAlignedBB(doorPos.add(-3, -3, -3), doorPos.add(3, 3, 3)),
				n -> n != null && !n.isHostileTowards(theEntity)).isEmpty();
	}

	private boolean isCloseToDoor() {
		return theEntity.getDistanceSq(doorPos) <= 2.25D;
	}

	@Override
	public final void startExecuting() {
		doDoorInteraction(true);
	}

	@Override
	public final void resetTask() {
		if (this.close) {
			doDoorInteraction(false);
		}
	}

	private boolean findDoor(BlockPos potentialDoorPos) {
		this.doorState = this.theEntity.world.getBlockState(potentialDoorPos);
		if (doorState.getBlock() instanceof BlockDoor) {
			if (doorState.getMaterial() == Material.WOOD) {
				this.doorPos = potentialDoorPos;
				return true;
			}
		} else if (doorState.getBlock() instanceof BlockFenceGate) {
			this.doorPos = potentialDoorPos;
			return true;
		}
		this.doorState = null;
		return false;
	}

	private void doDoorInteraction(boolean isOpening) {
		if (doorState.getBlock() instanceof BlockDoor) {
			((BlockDoor) doorState.getBlock()).toggleDoor(this.theEntity.world, this.doorPos, isOpening);
		} else if (doorState.getBlock() instanceof BlockFenceGate) {
			boolean fenceGateOpen = doorState.getValue(BlockFenceGate.OPEN);
			if (isOpening) {
				if (!fenceGateOpen) {
					EnumFacing entityFacing = EnumFacing.fromAngle((double) this.theEntity.rotationYaw);
					openFenceGate(doorState, doorPos, entityFacing);
					IBlockState state = this.theEntity.world.getBlockState(doorPos.up());
					if (state.getBlock() instanceof BlockFenceGate) {
						openFenceGate(state, doorPos.up(), entityFacing);
					}
				}
			} else {
				doorState = doorState.withProperty(BlockFenceGate.OPEN, false);
				this.theEntity.world.setBlockState(doorPos, doorState, 10);
				IBlockState state = this.theEntity.world.getBlockState(doorPos.up());
				if (state.getBlock() instanceof BlockFenceGate) {
					state = state.withProperty(BlockFenceGate.OPEN, false);
					this.theEntity.world.setBlockState(doorPos.up(), state, 10);
				}
			}
			this.theEntity.world.playEvent(null, doorState.getValue(BlockFenceGate.OPEN) ? 1008 : 1014, doorPos, 0);
		}
	}

	private void openFenceGate(IBlockState state, BlockPos pos, EnumFacing entityFacing) {
		IBlockState updatedState = state;
		if (updatedState.getValue(BlockFenceGate.FACING) == entityFacing.getOpposite()) {
			updatedState = updatedState.withProperty(BlockFenceGate.FACING, entityFacing);
		}

		updatedState = updatedState.withProperty(BlockFenceGate.OPEN, true);
		this.theEntity.world.setBlockState(pos, updatedState, 10);
	}
}
