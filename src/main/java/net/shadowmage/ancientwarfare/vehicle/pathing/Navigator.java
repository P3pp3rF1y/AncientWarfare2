package net.shadowmage.ancientwarfare.vehicle.pathing;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import net.shadowmage.ancientwarfare.structure.tile.TEGateProxy;
import net.shadowmage.ancientwarfare.vehicle.entity.IPathableEntity;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import java.util.List;
import java.util.Random;

public class Navigator implements IPathableCallback {
	private static final int DOOR_OPEN_MAX = 15;
	private static final int DOOR_CHECK_TICKS_MAX = 5;

	private PathFinderThetaStar pathFinder = new PathFinderThetaStar();

	protected IPathableEntity owner;
	protected Entity entity;
	protected PathWorldAccess world;
	protected EntityPath path;
	private final Node finalTarget = new Node(0, 0, 0);
	private Node currentTarget;
	protected Random rng = new Random();

	protected EntityGate gate = null;
	private boolean hasDoor = false;
	private BlockPos doorPos = BlockPos.ORIGIN;
	private int doorOpenTicks = 0;
	private int doorCheckTicks = 0;
	private Vec3d stuckCheckPosition = Vec3d.ZERO;
	private int stuckCheckTicks = 40;
	private int stuckCheckTicksMax = 40;

	private PathFinderCrawler testCrawler;

	/**
	 * TODO fallen detection
	 * TODO better handling of when can't find path
	 * (remember last request(s), if same request/similar and no path, try what??)
	 * TODO crawler needs to test for vertical clearance when moving up/down (see theta)
	 */
	public Navigator(VehicleBase owner) {
		this.owner = owner;
		this.entity = owner.getEntity();
		this.world = owner.worldAccess;
		this.path = new EntityPath();
		finalTarget.reassign(MathHelper.floor(entity.posX), MathHelper.floor(entity.posY), MathHelper.floor(entity.posZ));
		this.stuckCheckPosition = new Vec3d(entity.posX, entity.posY, entity.posZ);
		this.testCrawler = new PathFinderCrawler();
	}

	public void setStuckCheckTicks(int ticks) {
		if (ticks > 0) {
			this.stuckCheckTicksMax = ticks;
		}
	}

	public void setMoveToTarget(BlockPos pos) {
		//TODO 1.6.4 AW has logic in npcbase that uses this - readd?
		if (!entity.world.isBlockLoaded(pos)) {
			return;
		}
		this.sendToClients(pos);
		int ex = MathHelper.floor(entity.posX);
		int ey = MathHelper.floor(entity.posY);
		int ez = MathHelper.floor(entity.posZ);
		if (entity.posY % 1.f > 0.75 && !world.isWalkable(ex, ey, ez)) {
			ey++;
		}
		if (this.shouldCalculatePath(pos)) {
			this.finalTarget.reassign(pos);
			this.calculatePath(ex, ey, ez, pos);
		}
	}

	public void onMovementUpdate() {
		this.updateMoveHelper();
		this.detectStuck();
		this.claimNode();
		if (this.currentTarget != null) {
			if (this.world.canUseLaders) {
				this.handleLadderMovement();
			}
			if (this.world.canOpenDoors) {
				this.doorInteraction();
			}
			owner.setMoveTo(currentTarget.x + 0.5d, currentTarget.y, currentTarget.z + 0.5d, owner.getDefaultMoveSpeed());
		}
	}

	private void handleLadderMovement() {
		if (owner.isPathableEntityOnLadder()) {
			if (currentTarget.y < (int) entity.posY) {
				entity.motionY = -0.125f;
			} else if (currentTarget.y > (int) entity.posY) {
				entity.motionY = 0.125f;
			}
		}
	}

	private void updateMoveHelper() {
		this.pathFinder.doSearchIterations(10);
		if (this.doorOpenTicks > 0) {
			this.doorOpenTicks--;
		}
		if (this.hasDoor && this.doorOpenTicks <= 0) {
			this.hasDoor = false;
			this.interactWithDoor(doorPos, false);
		}
		if (this.gate != null && this.doorOpenTicks <= 0) {
			this.interactWithGate(false);
			this.gate = null;
		}
	}

	private void detectStuck() {
		if (this.stuckCheckTicks <= 0) {
			this.stuckCheckTicks = this.stuckCheckTicksMax;
			if (this.currentTarget != null && entity.getDistance(stuckCheckPosition.x, stuckCheckPosition.y, stuckCheckPosition.z) < 1.5d) {
				this.owner.onStuckDetected();
				this.clearPath();
				this.currentTarget = null;
			}
			stuckCheckPosition = new Vec3d(entity.posX, entity.posY, entity.posZ);
		} else {
			this.stuckCheckTicks--;
		}
	}

	private boolean isNewTargetClose(BlockPos target) {
		float dist = (float) entity.getDistance(finalTarget.x, finalTarget.y, finalTarget.z);
		float tDist = Trig.getDistance(finalTarget.x, finalTarget.y, finalTarget.z, target.getX(), target.getY(), target.getZ());
		return tDist < dist * 0.1f;
	}

	private boolean isNewTarget(BlockPos target) {//
		return !isNewTargetClose(target) && !this.finalTarget.equals(target.getX(), target.getY(), target.getZ());
	}

	private boolean isAtTarget(BlockPos pos) {
		return entity.getDistance(pos.getX() + 0.5d, pos.getY(), pos.getZ() + 0.5d) < entity.width;
	}

	private boolean isPathEmpty() {
		return this.path.getActivePathSize() <= 0;
	}

	private boolean shouldCalculatePath(BlockPos target) {
		return isNewTarget(target) || (isPathEmpty() && !isAtTarget(target) && currentTarget == null && !pathFinder.isSearching);
	}

	private void calculatePath(int ex, int ey, int ez, BlockPos target) {
		this.path.clearPath();
		this.currentTarget = null;
		if (PathUtils.canPathStraightToTarget(world, ex, ey, ez, target)) {
			this.currentTarget = new Node(target);
		} else {
			this.path.setPath(testCrawler.findPath(world, ex, ey, ez, target, 8));
			Node end = this.path.getEndNode();
			if (end != null && (end.x != target.getX() || end.y != target.getY() || end.z != target.getZ())) {
				this.pathFinder.findPath(world, end.x, end.y, end.z, target, 60, this, false);
			}
		}
		this.stuckCheckTicks = this.stuckCheckTicksMax;
		stuckCheckPosition = new Vec3d(entity.posX, entity.posY, entity.posZ);
		Node start = this.path.getFirstNode();
		if (start != null && (getEntityDistance(start) < 0.8f && start.y == ey)) {
			this.path.claimNode();//skip the first node because it is probably behind you, move onto next
		}
		this.claimNode();
	}

	private void doorInteraction() {
		if (this.doorCheckTicks <= 0) {
			this.doorCheckTicks = DOOR_CHECK_TICKS_MAX;
			if (this.entity.collidedHorizontally && checkForDoors(entity.getPosition())) {
				if (this.hasDoor) {
					this.interactWithDoor(doorPos, true);
					this.doorOpenTicks = DOOR_OPEN_MAX;
				} else if (gate != null) {
					this.interactWithGate(true);
					this.doorOpenTicks = DOOR_OPEN_MAX;
				}
			}
		} else {
			this.doorCheckTicks--;
		}
	}

	private boolean checkForDoors(BlockPos entityPos) {
		IBlockState state = entity.world.getBlockState(entityPos);
		Block block = state.getBlock();
		if ((block instanceof BlockDoor && state.getMaterial() == Material.WOOD) || block instanceof BlockFenceGate) {
			if (hasDoor && !doorPos.equals(entityPos)) {
				this.interactWithDoor(doorPos, false);
			}
			doorPos = entityPos;
			hasDoor = true;
			return true;
		}
		if (block == AWStructureBlocks.GATE_PROXY) {
			WorldTools.getTile(entity.world, entityPos, TEGateProxy.class).ifPresent(proxy -> {
				interactWithGate(false);
				gate = proxy.getOwner().orElse(null);
			});
			return true;
		}
		float yaw = entity.rotationYaw;
		while (yaw < 0) {
			yaw += 360.f;
		}
		while (yaw >= 360.f) {
			yaw -= 360.f;
		}
		int x = entityPos.getX();
		int y = entityPos.getY();
		int z = entityPos.getZ();
		if (yaw >= 360 - 45 || yaw < 45)//south, check z+
		{
			z++;
		} else if (yaw >= 45 && yaw < 45 + 90)//west, check x+
		{
			x--;
		} else if (yaw >= 180 - 45 && yaw < 180 + 45)//north
		{
			z--;
		} else//east
		{
			x++;
		}
		state = entity.world.getBlockState(new BlockPos(x, y, z));
		block = state.getBlock();
		if ((block instanceof BlockDoor && state.getMaterial() == Material.WOOD) || block instanceof BlockFenceGate) {
			if (hasDoor && !doorPos.equals(entityPos)) {
				this.interactWithDoor(doorPos, false);
			}
			doorPos = entityPos;
			hasDoor = true;
			return true;
		}
		if (block == AWStructureBlocks.GATE_PROXY) {
			WorldTools.getTile(entity.world, new BlockPos(x, y, z), TEGateProxy.class).ifPresent(proxy -> {
				interactWithGate(false);
				gate = proxy.getOwner().orElse(null);
			});
			return true;
		}
		return false;
	}

	private void interactWithGate(boolean open) {
		if ((gate.edgePosition > 0 && !open) || (gate.edgePosition == 0 && open)) {
			gate.activateGate();
		}
		if (!open) {
			this.gate = null;
		}
	}

	private void interactWithDoor(BlockPos doorPos, boolean open) {
		IBlockState state = entity.world.getBlockState(doorPos);
		Block block = state.getBlock();
		if (block instanceof BlockDoor && state.getMaterial() == Material.WOOD) {
			((BlockDoor) block).toggleDoor(entity.world, doorPos, open);
		} else if (block instanceof BlockFenceGate && open != state.getValue(BlockFenceGate.OPEN)) {
			if (open && !state.getValue(BlockFenceGate.OPEN)) {
				entity.world.setBlockState(doorPos, state.withProperty(BlockFenceGate.OPEN, true), 2);
				entity.world.playEvent(null, 1008, doorPos, 0);
			} else if (!open && state.getValue(BlockFenceGate.OPEN)) {
				entity.world.setBlockState(doorPos, state.withProperty(BlockFenceGate.OPEN, false), 2);
				entity.world.playEvent(null, 1014, doorPos, 0);
			}
		}
	}

	private void claimNode() {
		if (this.currentTarget == null || this.getEntityDistance(currentTarget) < entity.width) {
			this.currentTarget = this.path.claimNode();
			while (this.currentTarget != null && this.getEntityDistance(currentTarget) < entity.width) {
				this.currentTarget = this.path.claimNode();
			}
			this.stuckCheckTicks = this.stuckCheckTicksMax;
			stuckCheckPosition = new Vec3d(entity.posX, entity.posY, entity.posZ);
		}

	}

	private float getEntityDistance(Node n) {
		return entity == null ? 0.f : n == null ? 0.f : (float) entity.getDistance(n.x + 0.5d, n.y, n.z + 0.5d);
	}

	private void sendToClients(BlockPos pos) {
		//  if(Config.DEBUG && !world.isRemote() && owner.getEntity() instanceof NpcBase)//relay to client, force client-side to find path as well (debug rendering of path)
		//    {
		//    NBTTagCompound tag = new NBTTagCompound();
		//    tag.setInteger("tx", x);
		//    tag.setInteger("ty", y);
		//    tag.setInteger("tz", z);
		//    Packet04Npc pkt = new Packet04Npc();
		//    pkt.setParams(entity);
		//    pkt.setPathTarget(tag);
		//    pkt.sendPacketToAllTrackingClients(entity);
		//    }
	}

	@Override
	public void onPathFound(List<Node> pathNodes) {
		this.path.addPath(world, pathNodes);
	}

	public void clearPath() {
		this.path.clearPath();
		this.currentTarget = null;
	}

	public void forcePath(List<Node> n) {
		this.path.setPath(n);
		this.claimNode();
	}

	public void setCanGoOnLand(boolean land) {
		this.world.setCanGoOnLand(land);
	}
}
