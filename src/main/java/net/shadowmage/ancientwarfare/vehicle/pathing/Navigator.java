/**
 * Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
 * This software is distributed under the terms of the GNU General Public License.
 * Please see COPYING for precise license information.
 * <p>
 * This file is part of Ancient Warfare.
 * <p>
 * Ancient Warfare is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Ancient Warfare is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.shadowmage.ancientwarfare.vehicle.pathing;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.shadowmage.ancientwarfare.vehicle.entity.IPathableEntity;
import shadowmage.ancient_warfare.common.block.BlockLoader;
import shadowmage.ancient_warfare.common.gates.EntityGate;
import shadowmage.ancient_warfare.common.gates.TEGateProxy;
import shadowmage.ancient_warfare.common.interfaces.IEntityNavigator;
import shadowmage.ancient_warfare.common.pathfinding.EntityPath;
import shadowmage.ancient_warfare.common.pathfinding.IPathableCallback;
import shadowmage.ancient_warfare.common.pathfinding.PathFinderCrawler;
import shadowmage.ancient_warfare.common.pathfinding.PathFinderThetaStar;
import shadowmage.ancient_warfare.common.pathfinding.PathUtils;
import shadowmage.ancient_warfare.common.utils.BlockPosition;
import shadowmage.ancient_warfare.common.utils.Trig;
import shadowmage.ancient_warfare.common.utils.Vec3d;

import java.util.List;
import java.util.Random;

public class Navigator implements IEntityNavigator, IPathableCallback {

	PathFinderThetaStar pathFinder = new PathFinderThetaStar();

	protected IPathableEntity owner;
	protected Entity entity;
	protected PathWorldAccess world;
	protected EntityPath path;
	protected final Node finalTarget = new Node(0, 0, 0);
	public Node currentTarget;
	protected Random rng = new Random();

	protected EntityGate gate = null;
	protected boolean hasDoor = false;
	protected BlockPosition doorPos = new BlockPosition(0, 0, 0);
	protected int doorOpenTicks = 0;
	protected int doorCheckTicks = 0;
	protected int doorOpenMax = 15;
	protected int doorCheckTicksMax = 5;
	protected final Vec3d stuckCheckPosition = new Vec3d(0, 0, 0);
	protected int stuckCheckTicks = 40;
	protected int stuckCheckTicksMax = 40;

	private PathFinderCrawler testCrawler;

	/**
	 * TODO fallen detection
	 * TODO better handling of when can't find path
	 *    (remember last request(s), if same request/similar and no path, try what??)
	 * TODO crawler needs to test for vertical clearance when moving up/down (see theta)
	 *
	 */
	public Navigator(IPathableEntity owner) {
		this.owner = owner;
		this.entity = owner.getEntity();
		this.world = owner.getWorldAccess();
		this.path = new EntityPath();
		finalTarget.reassign(MathHelper.floor(entity.posX), MathHelper.floor(entity.posY), MathHelper.floor(entity.posZ));
		this.stuckCheckPosition.setup(entity.posX, entity.posY, entity.posZ);
		this.testCrawler = new PathFinderCrawler();
	}

	public void setStuckCheckTicks(int ticks) {
		if (ticks > 0) {
			this.stuckCheckTicksMax = ticks;
		}
	}

	@Override
	public void setMoveToTarget(int x, int y, int z) {
		if (!entity.world.blockExists(x, y, z)) {
			return;
		}
		this.sendToClients(x, y, z);
		int ex = MathHelper.floor(entity.posX);
		int ey = MathHelper.floor(entity.posY);
		int ez = MathHelper.floor(entity.posZ);
		if (entity.posY % 1.f > 0.75 && !world.isWalkable(ex, ey, ez)) {
			ey++;
		}
		if (this.shouldCalculatePath(ex, ey, ez, x, y, z)) {
			this.finalTarget.reassign(x, y, z);
			this.calculatePath(ex, ey, ez, x, y, z);
		} else {
			//    Config.logDebug("skipping path calc...");
		}
	}

	@Override
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
			//    Config.logDebug("setting move to: "+currentTarget);
			owner.setMoveTo(currentTarget.x + 0.5d, currentTarget.y, currentTarget.z + 0.5d, owner.getDefaultMoveSpeed());
		}
	}

	protected void handleLadderMovement() {
		if (owner.isPathableEntityOnLadder()) {
			if (currentTarget.y < (int) entity.posY) {
				entity.motionY = -0.125f;
			} else if (currentTarget.y > (int) entity.posY) {
				entity.motionY = 0.125f;
			}
		}
	}

	protected void updateMoveHelper() {
		this.pathFinder.doSearchIterations(10);
		if (this.doorOpenTicks > 0) {
			this.doorOpenTicks--;
		}
		if (this.hasDoor) {
			if (this.doorOpenTicks <= 0) {
				this.hasDoor = false;
				this.interactWithDoor(doorPos, false);
			}
		}
		if (this.gate != null && this.doorOpenTicks <= 0) {
			this.interactWithGate(false);
			this.gate = null;
		}
	}

	protected void detectStuck() {
		if (this.stuckCheckTicks <= 0) {
			this.stuckCheckTicks = this.stuckCheckTicksMax;
			if (this.currentTarget != null) {
				if (entity.getDistance(stuckCheckPosition.x, stuckCheckPosition.y, stuckCheckPosition.z) < 1.5d) {
					this.owner.onStuckDetected();
					//        Config.logDebug("detecting stuck, clearing path");
					this.clearPath();
					this.currentTarget = null;
				}
			}
			this.stuckCheckPosition.setup(entity.posX, entity.posY, entity.posZ);
		} else {
			this.stuckCheckTicks--;
		}
	}

	protected boolean isNewTargetClose(int tx, int ty, int tz) {
		float dist = (float) entity.getDistance(finalTarget.x, finalTarget.y, finalTarget.z);
		float tDist = Trig.getDistance(finalTarget.x, finalTarget.y, finalTarget.z, tx, ty, tz);
		if (tDist < dist * 0.1f) {
			//    Config.logDebug("returning target was close enough to not recalc");
			return true;
		}
		return false;
	}

	protected boolean isNewTarget(int tx, int ty, int tz) {//
		return !isNewTargetClose(tx, ty, tz) && !this.finalTarget.equals(tx, ty, tz);
	}

	protected boolean isAtTarget(int x, int y, int z) {
		return entity.getDistance(x + 0.5d, y, z + 0.5d) < entity.width;
	}

	protected boolean isPathEmpty() {
		return this.path.getActivePathSize() <= 0;
	}

	protected boolean shouldCalculatePath(int ex, int ey, int ez, int tx, int ty, int tz) {
		//  Config.logDebug("new target: "+isNewTarget(tx, ty, tz));
		//  Config.logDebug("path empty: "+isPathEmpty());
		//  Config.logDebug("current target: " + (currentTarget==null));
		//  Config.logDebug("at target: " + !isAtTarget(tx, ty, tz));
		//  Config.logDebug("searching already: " + !pathFinder.isSearching);
		return isNewTarget(tx, ty, tz) || (isPathEmpty() && !isAtTarget(tx, ty, tz) && currentTarget == null && !pathFinder.isSearching);
	}

	protected void calculatePath(int ex, int ey, int ez, int tx, int ty, int tz) {
		//  Config.logDebug("calculating path..");
		//  Config.logDebug("checking path from: "+ex+","+ey+","+ez+" to: "+tx+","+ty+","+tz);
		this.path.clearPath();
		this.currentTarget = null;
		if (PathUtils.canPathStraightToTarget(world, ex, ey, ez, tx, ty, tz)) {
			//    Config.logDebug("can path straight...");
			this.currentTarget = new Node(tx, ty, tz);
		} else {
			this.path.setPath(testCrawler.findPath(world, ex, ey, ez, tx, ty, tz, 8));
			Node end = this.path.getEndNode();
			//    Config.logDebug("crawler path end node: "+end);
			if (end != null && (end.x != tx || end.y != ty || end.z != tz)) {
				//      Config.logDebug("crawler did not return complete path...");
				this.pathFinder.findPath(world, end.x, end.y, end.z, tx, ty, tz, 60, this, false);
			}
		}
		this.stuckCheckTicks = this.stuckCheckTicksMax;
		this.stuckCheckPosition.setup(entity.posX, entity.posY, entity.posZ);
		Node start = this.path.getFirstNode();
		if (start != null && (getEntityDistance(start) < 0.8f && start.y == ey)) {
			this.path.claimNode();//skip the first node because it is probably behind you, move onto next
		}
		this.claimNode();
	}

	protected void doorInteraction() {
		int ex = MathHelper.floor(entity.posX);
		int ey = MathHelper.floor(entity.posY);
		int ez = MathHelper.floor(entity.posZ);
		if (this.doorCheckTicks <= 0) {
			this.doorCheckTicks = this.doorCheckTicksMax;
			if (this.entity.isCollidedHorizontally && checkForDoors(ex, ey, ez)) {
				if (this.hasDoor) {
					this.interactWithDoor(doorPos, true);
					this.doorOpenTicks = this.doorOpenMax;
				} else if (gate != null) {
					this.interactWithGate(true);
					this.doorOpenTicks = this.doorOpenMax;
				}
			}
		} else {
			this.doorCheckTicks--;
		}
	}

	protected boolean checkForDoors(int ex, int ey, int ez) {
		int doorId = Block.doorWood.blockID;
		int gateId = Block.fenceGate.blockID;
		int id;
		id = entity.world.getBlockId(ex, ey, ez);
		if (id == doorId || id == gateId) {
			if (hasDoor && (doorPos.x != ex || doorPos.y != ey || doorPos.z != ez)) {
				this.interactWithDoor(doorPos, false);
			}
			doorPos.x = ex;
			doorPos.y = ey;
			doorPos.z = ez;
			hasDoor = true;
			return true;
		}
		if (id == BlockLoader.gateProxy.blockID) {
			TEGateProxy proxy = (TEGateProxy) entity.world.getBlockTileEntity(ex, ey, ez);
			if (this.gate != null) {
				this.interactWithGate(false);
			}
			this.gate = proxy.owner;
			return true;
		}
		float yaw = entity.rotationYaw;
		while (yaw < 0) {
			yaw += 360.f;
		}
		while (yaw >= 360.f) {
			yaw -= 360.f;
		}
		int x = ex;
		int y = ey;
		int z = ez;
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
		id = entity.world.getBlockId(x, y, z);
		if (id == doorId || id == gateId) {
			if (hasDoor && (doorPos.x != x || doorPos.y != y || doorPos.z != z)) {
				this.interactWithDoor(doorPos, false);
			}
			doorPos.x = x;
			doorPos.y = y;
			doorPos.z = z;
			hasDoor = true;
			return true;
		}
		if (id == BlockLoader.gateProxy.blockID) {
			TEGateProxy proxy = (TEGateProxy) entity.world.getBlockTileEntity(x, y, z);
			if (this.gate != null) {
				this.interactWithGate(false);
			}
			this.gate = proxy.owner;
			return true;
		}
		return false;
	}

	protected void interactWithGate(boolean open) {
		if (gate.edgePosition > 0 && !open) {
			gate.activateGate();
		} else if (gate.edgePosition == 0 && open) {
			gate.activateGate();
		}
		if (!open) {
			this.gate = null;
		}
	}

	protected void interactWithDoor(BlockPosition doorPos, boolean open) {
		Block block = Block.blocksList[entity.world.getBlockId(doorPos.x, doorPos.y, doorPos.z)];
		if (block == null) {
			return;
		} else if (block.blockID == Block.doorWood.blockID) {
			((BlockDoor) block).onPoweredBlockChange(entity.world, doorPos.x, doorPos.y, doorPos.z, open);
		} else if (block.blockID == Block.fenceGate.blockID) {
			int meta = entity.world.getBlockMetadata(doorPos.x, doorPos.y, doorPos.z);
			boolean gateopen = BlockFenceGate.isFenceGateOpen(meta);
			if (open != gateopen) {
				int x = doorPos.x;
				int y = doorPos.y;
				int z = doorPos.z;
				if (open && !BlockFenceGate.isFenceGateOpen(meta)) {
					entity.world.setBlockMetadataWithNotify(x, y, z, meta | 4, 2);
					entity.world.playAuxSFXAtEntity((EntityPlayer) null, 1003, x, y, z, 0);
				} else if (!open && BlockFenceGate.isFenceGateOpen(meta)) {
					entity.world.setBlockMetadataWithNotify(x, y, z, meta & -5, 2);
					entity.world.playAuxSFXAtEntity((EntityPlayer) null, 1003, x, y, z, 0);
				}
			}
		}
	}

	protected void claimNode() {
		if (this.currentTarget == null || this.getEntityDistance(currentTarget) < entity.width) {
			//    Config.logDebug("attempting to claim node..");
			this.currentTarget = this.path.claimNode();
			while (this.currentTarget != null && this.getEntityDistance(currentTarget) < entity.width) {
				this.currentTarget = this.path.claimNode();
				//      Config.logDebug("new move target: "+this.currentTarget);
			}
			this.stuckCheckTicks = this.stuckCheckTicksMax;
			this.stuckCheckPosition.setup(entity.posX, entity.posY, entity.posZ);
		}

	}

	protected int floorX() {
		return MathHelper.floor(entity.posX);
	}

	protected int floorY() {
		return MathHelper.floor(entity.posY);
	}

	protected int floorZ() {
		return MathHelper.floor(entity.posZ);
	}

	protected float getEntityDistance(Node n) {
		return entity == null ? 0.f : n == null ? 0.f : (float) entity.getDistance(n.x + 0.5d, n.y, n.z + 0.5d);
	}

	protected void sendToClients(int x, int y, int z) {
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
	public void setCanSwim(boolean swim) {
		if (this.world != null) {
			this.world.canSwim = swim;
		}
	}

	@Override
	public void setCanOpenDoors(boolean doors) {
		if (this.world != null) {
			this.world.canOpenDoors = true;
		}
	}

	@Override
	public void setCanUseLadders(boolean ladders) {
		if (this.world != null) {
			this.world.canUseLaders = ladders;
		}
	}

	@Override
	public void onPathFound(List<Node> pathNodes) {
		//  Config.logDebug("full path request returned length: "+pathNodes.size());
		if (pathNodes.size() > 0) {
			Node n = pathNodes.get(pathNodes.size() - 1);
		}
		//  for(Node n : pathNodes)
		//    {
		//    Config.logDebug("n:" +n);
		//    }
		this.path.addPath(world, pathNodes);
	}

	@Override
	public void clearPath() {
		this.path.clearPath();
		this.currentTarget = null;
	}

	@Override
	public void forcePath(List<Node> n) {
		this.path.setPath(n);
		this.claimNode();
	}

	@Override
	public List<Node> getCurrentPath() {
		return path.getActivePath();
	}

	@Override
	public void setCanGoOnLand(boolean land) {
		this.world.setCanGoOnLand(land);
	}

}
