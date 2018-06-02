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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class NpcAIDoor extends EntityAIBase {
	protected final EntityLiving theEntity;
	private final boolean close;
	protected BlockPos doorPos = new BlockPos(0, 0, 0);
	protected IBlockState doorState;
	private int timer;
	private float interactionPosX, interactionPosZ;
	private boolean allDoors;

	public NpcAIDoor(EntityLiving living, boolean closeBehind) {
		this.theEntity = living;
		this.close = closeBehind;
	}

	public EntityAIBase enableAllDoors() {
		allDoors = true;
		return this;
	}

	@Override
	public final boolean shouldExecute() {
		PathNavigateGround pathnavigate = (PathNavigateGround) this.theEntity.getNavigator();
		if (!pathnavigate.getEnterDoors() || pathnavigate.noPath())
			return false;
		Path pathentity = pathnavigate.getPath();
		for (int i = 0; i < Math.min(pathentity.getCurrentPathIndex() + 2, pathentity.getCurrentPathLength()); ++i) {
			PathPoint pathpoint = pathentity.getPathPointFromIndex(i);

			if (this.theEntity.getDistanceSq(pathpoint.x, this.theEntity.posY, pathpoint.z) <= 2.25D) {
				this.doorPos = new BlockPos(pathpoint.x, pathpoint.y, pathpoint.z);
				if (findDoor()) {
					return true;
				}
				this.doorPos = this.doorPos.up();
				if (findDoor()) {
					return true;
				}
			}
		}

		if (!this.theEntity.collidedHorizontally)
			return false;
		this.doorPos = new BlockPos(MathHelper.floor(this.theEntity.posX), MathHelper.floor(this.theEntity.posY), MathHelper.floor(this.theEntity.posZ));
		if (findDoor()) {
			return true;
		}
		this.doorPos = this.doorPos.up();
		return findDoor();
	}

	@Override
	public final boolean shouldContinueExecuting() {
		return close && timer > 0;
	}

	@Override
	public final void startExecuting() {
		doDoorInteraction(true);
		this.timer = 20;
		this.interactionPosX = (float) ((double) (this.doorPos.getX() + 0.5F) - this.theEntity.posX);
		this.interactionPosZ = (float) ((double) (this.doorPos.getZ() + 0.5F) - this.theEntity.posZ);
	}

	@Override
	public final void updateTask() {
		this.timer--;
		float f = (float) ((double) (this.doorPos.getX() + 0.5F) - this.theEntity.posX);
		float f1 = (float) ((double) (this.doorPos.getZ() + 0.5F) - this.theEntity.posZ);
		float f2 = this.interactionPosX * f + this.interactionPosZ * f1;
		if (f2 < 0.0F) {
			this.timer = 0;
		}
	}

	@Override
	public final void resetTask() {
		if (this.close) {
			doDoorInteraction(false);
		}
	}

	protected boolean findDoor() {
		this.doorState = this.theEntity.world.getBlockState(this.doorPos);
		if (doorState.getBlock() instanceof BlockDoor) {
			return allDoors || doorState.getMaterial() == Material.WOOD;
		} else if (doorState.getBlock() instanceof BlockFenceGate) {
			return true;
		}
		this.doorState = null;
		return false;
	}

	protected void doDoorInteraction(boolean isOpening) {
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
		if (state.getValue(BlockFenceGate.FACING) == entityFacing.getOpposite()) {
			state = state.withProperty(BlockFenceGate.FACING, entityFacing);
		}

		state = state.withProperty(BlockFenceGate.OPEN, true);
		this.theEntity.world.setBlockState(pos, state, 10);
	}
}
