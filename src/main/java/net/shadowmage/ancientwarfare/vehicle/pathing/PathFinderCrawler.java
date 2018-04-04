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

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PathFinderCrawler {

	/**
	 * a list of all working-set nodes, both open and closed.  used to prevent spurious object creation
	 * as well as keep already visited but closed nodes scores valid and cached, as well as for pulling
	 * live nodes from the 'open-list' without having to manually synch them back in/update values
	 */
	private ArrayList<CrawlNode> allNodes = new ArrayList<CrawlNode>();

	private CrawlNode currentNode;

	LinkedList<Node> path = new LinkedList<Node>();
	/**
	 * start and target points
	 */
	int sx;
	int sy;
	int sz;
	int tx;
	int ty;
	int tz;
	int maxRange;
	PathWorldAccess world;

	public List<Node> findPath(PathWorldAccess world, int x, int y, int z, BlockPos target, int maxRange) {
		long t1 = System.nanoTime();
		this.world = world;
		this.sx = this.cx = x;
		this.sy = this.cy = y;
		this.sz = this.cz = z;
		this.tx = target.getX();
		this.ty = target.getY();
		this.tz = target.getZ();
		this.maxRange = maxRange;
		this.currentNode = getOrMakeNode(sx, sy, sz, null);
		this.path.clear();
		this.searchLoop();
		LinkedList<Node> foundPath = new LinkedList<Node>();
		Node p = null;
		Node n = null;
		boolean skip = false;
		for (int i = 0; i < this.path.size(); i++) {
			//loop through from the beginning to end
			//  loop through from end to current in outer loop
			//    if current in inner loop can see current in outer loop
			//      skip everything inbetween
			//    else add current node in outer loop to returned path
			n = this.path.get(i);
			for (int k = this.path.size() - 1; k > i; k--) {
				p = this.path.get(k);
				if (PathUtils.canPathStraightToTarget(world, n.x, n.y, n.z, new BlockPos(p.x, p.y, p.z))) {
					i = k - 1;//set it so next i run starts at where we found
					break;
				}
			}
			foundPath.add(new Node(n.x, n.y, n.z));
			//    Config.logDebug("crawler node: "+n.toString());
		}
		if (cx == tx && cy == ty && cz == tz) {
			foundPath.add(new Node(tx, ty, tz));
			//    Config.logDebug(foundPath.get(foundPath.size()-1).toString());
		}
		this.currentNode = null;
		this.world = null;
		this.allNodes.clear();
		this.path.clear();
		//ServerPerformanceMonitor.addPathfindingTime(System.nanoTime() - t1); TODO server perf monitoring?
		return foundPath;
	}

	int cx;
	int cy;
	int cz;
	int dx;//current move test x
	int dy;//current move test y
	int dz;//current move test z
	int pdx;
	int pdy;
	int pdz;
	int pcx;
	int pcy;
	int pcz;
	int gx;//goal direction x
	int gy;//goal direction y
	int gz;//goal direction z
	int xDiff;
	int yDiff;
	int zDiff;
	boolean followingWall = false;
	int turnDir = 1;//right turn, may toggle to left in the future

	/**
	 * needs to be integer based, only keeping a list of path found (for reference of breaking purposes)
	 * (doing it list-based is far too slow (e.g. lookups when creating nodes, etc))
	 * <p>
	 * if can see goal node, set found, add goal to path, link
	 * break;/return;
	 * else if can move towards goal in one axis or another, not crossing path, and turn direction to move towards goal is not opposite preferred direction
	 * if already moving in an axis, continue
	 * else set direction to largest axis difference
	 * else if was following a wall and there is now a break in that direction, turn in that direction and move (opposite preferred direction), set following wall to false
	 * if would cross path -- call findLastTurnablePoint
	 * else if can continue in current direction, do so
	 * if would cross path -- call findLastTurnablePoint
	 * else if can't move forward, turn in preferred direction, set following wall to true
	 * if would cross path -- call findLastTurnablePoint
	 * else
	 * call findLastTurnablePoint
	 * <p>
	 * if newPos==oldPos, break;
	 * else can move = true
	 * <p>
	 * if can move
	 * if move direction is not the only direction possible open direction, set hadTurns to true/add turn directions
	 * move
	 * link previous node to new node
	 * <p>
	 * findLastTurnablePoint:
	 * look at nodes in path, see which ones had 'turns' in them
	 * find most recent set next preferred turn to false if it is not already explored
	 * return new position and move direction, currentPosition for none;
	 */

	protected void setInitialDirection() {
		if (Math.abs(xDiff) > Math.abs(zDiff))//pick a single direction to move, no diagonals
		{
			dx = gx;
			dz = 0;
			if (!world.isWalkable(cx + dx, cy, cz) && !world.isWalkable(cx + dx, cy + 1, cz) && !world.isWalkable(cx, cy - 1, cz)) {
				dx = 0;
				dz = gz;
			}
		} else {
			dz = gz;
			dx = 0;
			if (!world.isWalkable(cx, cy, cz + dz) && !world.isWalkable(cx, cy + 1, cz + dz) && !world.isWalkable(cx, cy - 1, cz + dz)) {
				dz = 0;
				dx = gx;
			}
		}
	}

	protected void searchLoop() {
		calcTargetDirection();
		this.setInitialDirection();
		dy = 0;
		if (dx == 0 && dz == 0) {
			this.path.add(currentNode);
			return;
		}
		Node newNode = null;
		for (int i = 0; i < this.maxRange && (cx != tx || cy != ty || cz != tz); i++) {
			this.path.add(currentNode);
			pdx = dx;
			pdy = dy;
			pdz = dz;
			pcx = cx;
			pcy = cy;
			pcz = cz;
			if (tryPathDirectlyToTarget()) {
				break;
			}
			if (tryMoveTowardsGoal()) {
				continue;
			}
			if (tryFollowWall()) {
				continue;
			}
			dx = pdx;
			dz = pdz;
			dy = pdy;
			if (tryMoveStraight()) {
				continue;
			}
			if (tryTurn()) {
				continue;
			}
			if (!tryFindLastTurn()) {
				break;
			}
			if (dx == 0 && dy == 0 && dz == 0)//could not move/tried moving nowhere
			{
				break;
			}
			if (pcx == cx && pcy == cy && pcz == cz)//did not move
			{
				break;
			}
		}
		if (cx == tx && cy == ty && cz == tz) {
			path.add(new Node(tx, ty, tz));
			//    Config.logDebug("hit goal");
		}
	}

	protected boolean tryMoveTowardsGoal() {
		this.calcTargetDirection();
		if (Math.abs(xDiff) > Math.abs(zDiff))//pick a single direction to move, no diagonals
		{
			dx = gx;
			dz = 0;
			if (dx != 0) {
				if (tryMoveStraight()) {
					return true;
				}
			}
		} else {
			dz = gz;
			dx = 0;
			if (dz != 0) {
				if (tryMoveStraight()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * if following wall, try and turn opposite of preferred turn direction
	 *
	 * @return
	 */
	protected boolean tryFollowWall() {
		//  if(followingWall)
		//    {
		////    Config.logDebug("following wall");
		//    dx = pdx;
		//    dz = pdz;
		//    if(dx!=0)//collided on x, set x to 0, find z
		//      {
		//      dx = 0;
		//      dz = -getZforTurn(dz);
		//      if(tryMoveStraight())
		//        {
		//        followingWall = false;
		//        return true;
		//        }
		//      }
		//    else if(dz!=0)//collided on z, set z to 0, find x
		//      {
		//      dz = 0;
		//      dx = -getXforTurn(dx);
		//      if(tryMoveStraight())
		//        {
		//        followingWall = false;
		//        return true;
		//        }
		//      }
		//    }
		return false;
	}

	protected boolean tryMoveStraight() {
		if (world.isWalkable(cx, cy + 1, cz) && ty > cy) {
			dy = 1;
			cy += dy;
			this.currentNode = getOrMakeNode(cx, cy, cz, currentNode);
			return true;
		}
		if (world.isWalkable(cx, cy - 1, cz) && ty < cy) {
			dy = -1;
			cy += dy;
			this.currentNode = getOrMakeNode(cx, cy, cz, currentNode);
			return true;
		}
		if (world.isWalkable(cx + dx, cy, cz + dz)) {
			dy = 0;
			cx += dx;
			cz += dz;
			cy += dy;
			this.currentNode = getOrMakeNode(cx, cy, cz, currentNode);
			return true;
		}
		if (world.isWalkable(cx + dx, cy + 1, cz + dz) && !world.checkBlockBounds(cx, cy + 2, cz)) {
			dy = 1;
			cx += dx;
			cz += dz;
			cy += dy;
			this.currentNode = getOrMakeNode(cx, cy, cz, currentNode);
			return true;
		}
		//  Config.logDebug("trying straight - down");
		if (world.isWalkable(cx + dx, cy - 1, cz + dz) && !world.checkBlockBounds(cx + dx, cy + 1, cz + dz)) {
			dy = -1;
			cx += dx;
			cz += dz;
			cy += dy;
			this.currentNode = getOrMakeNode(cx, cy, cz, currentNode);
			return true;
		}
		return false;
	}

	protected boolean tryTurn() {
		dx = pdx;
		dz = pdz;
		if (dx != 0)//collided on x, set x to 0, find z
		{
			dx = 0;
			dz = getZforTurn(dz);
			if (tryMoveStraight()) {
				//      Config.logDebug("turning z");
				followingWall = true;
				return true;
			}
		} else if (dz != 0)//collided on z, set z to 0, find x
		{
			dz = 0;
			dx = getXforTurn(dx);
			if (tryMoveStraight()) {
				//      Config.logDebug("turning x");
				followingWall = true;
				return true;
			}
		}
		return false;
	}

	/**
	 * called when moving in Z direction to turn left/right
	 *
	 * @param dx
	 * @return
	 */
	protected int getXforTurn(int dx) {
		if (pcz < cz) {
			dx = turnDir;
		} else if (pcz > cz) {
			dx = -turnDir;
		} else {
			dx = 0;
		}
		return dx;
	}

	protected int getZforTurn(int dz) {
		if (pcx < cx) {
			dz = turnDir;
		} else if (pcx > cx) {
			dz = -turnDir;
		} else {
			dz = 0;
		}
		return dz;
	}

	protected boolean tryFindLastTurn() {
		return false;
	}

	protected boolean tryPathDirectlyToTarget() {
		//TODO post-path smoothing..or something..as it is too costly to check node -> goal at every move (3ms->11ms for 100x runs)
		//  if(PathUtils.canPathStraightToTarget(world, cx, cy, cz, tx, ty, tz))
		//    {
		//    currentNode = getOrMakeNode(tx, ty, tz, currentNode);//new Node(tx, ty, tz);
		//    return true;
		//    }
		return false;
	}

	protected void calcTargetDirection() {
		gx = tx - cx;
		gy = ty - cy;
		gz = tz - cz;
		xDiff = gx;
		yDiff = gy;
		zDiff = gz;
		gx = gx < 0 ? -1 : gx > 0 ? 1 : 0;
		gy = gy < 0 ? -1 : gy > 0 ? 1 : 0;
		gz = gz < 0 ? -1 : gz > 0 ? 1 : 0;
	}

	private CrawlNode getOrMakeNode(int x, int y, int z, Node p) {
		CrawlNode n = null;
		for (CrawlNode c : this.allNodes) {
			if (c.equals(x, y, z)) {
				return c;
			}
		}
		n = new CrawlNode(x, y, z);
		allNodes.add(n);
		return n;
	}

	private class CrawlNode extends Node {
		/**
		 * @param bX
		 * @param bY
		 * @param bZ
		 */
		public CrawlNode(int bX, int bY, int bZ) {
			super(bX, bY, bZ);
			// TODO Auto-generated constructor stub
		}

	}

}
