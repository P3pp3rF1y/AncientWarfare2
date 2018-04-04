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
import java.util.PriorityQueue;

/**
 * going to be a theta-Star implementation
 * (pre-smoothed ASTAR paths), using line-of sight checks
 * at every node-link to recalc optimal g-costs from start-current
 * so that it uses straight-line from A->B directly
 * instead of doing all diagonal moves then all horizontal moves
 *
 * @author Shadowmage
 */
public class PathFinderThetaStar {
	/**
	 * A STAR PSUEDOCODE ********************************************************************
	 * LISTS : OPEN, CLOSED
	 * ADD START NODE TOP OPEN-LIST
	 * WHILE OPEN-LIST IS NOT EMPTY
	 *    CURRENT = OPEN-LIST.LOWEST-F
	 *    IF CURRENT == GOAL THEN END
	 *    ADD CURRENT TO CLOSED LIST
	 *    LIST NEIGHBORS = CURRENT.GETNEIGHBORS
	 *      FOR N IN NEIGHBORS
	 *        IF N.CLOSED && C.G + DIST(C,N) > N.G (new path to neighbor is longer than the neighbors current path)
	 *          CONTINUE
	 *        ELSE IF FRESH NODE OR C.G + DIST(C,N) < N.G (better path found to already-examined node)
	 *          N.G = C.G + DIST(C,N)
	 *          N.P = C
	 *          N.F = N.G + N.H(GOAL)
	 *          IF N.CLOSED
	 *            REMOVE N FROM CLOSED LIST
	 *          IF N NOT IN OPEN SET
	 *            ADD TO OPEN SET
	 * **************************************************************************************
	 */

	/**
	 * OPEN-LIST
	 */
	private PriorityQueue<Node> qNodes = new PriorityQueue<Node>();

	/**
	 * a list of all working-set nodes, both open and closed.  used to prevent spurious object creation
	 * as well as keep already visited but closed nodes scores valid and cached, as well as for pulling
	 * live nodes from the 'open-list' without having to manually synch them back in/update values
	 */
	private ArrayList<Node> allNodes = new ArrayList<Node>();

	/**
	 * current-node neighbors, just a cached list..
	 */
	private ArrayList<Node> searchNodes = new ArrayList<Node>();

	private Node currentNode;

	/**
	 * start and target points
	 */
	int sx;
	int sy;
	int sz;
	int tx;
	int ty;
	int tz;
	int minx;
	int miny;
	int minz;
	int maxx;
	int maxy;
	int maxz;
	int searchBufferRange = 40;
	int maxRange = 80;
	PathWorldAccess world;
	long startTime;
	long runTime;
	public long maxRunTime = 20000000l;//20ms, default time..public so may be overriden at run-time...must be reset between runs
	public long maxSearchIterations = 1200;

	private Node bestEndNode = null;
	private float bestPathLength = 0.f;
	private float bestPathDist = Float.POSITIVE_INFINITY;
	private int searchIteration;

	IPathableCallback caller = null;

	public void findPath(PathWorldAccess world, int x, int y, int z, BlockPos target, int maxRange, IPathableCallback caller, boolean instant) {
		this.allNodes.clear();
		this.qNodes.clear();
		this.searchNodes.clear();
		this.world = world;
		this.caller = caller;
		this.instantSearch = instant;
		this.isSearching = true;
		this.sx = x;
		this.sy = y;
		this.sz = z;
		this.tx = target.getX();
		this.ty = target.getY();
		this.tz = target.getZ();
		this.maxRange = maxRange;
		minx = x < tx ? x : tx;
		maxx = x < tx ? tx : x;
		miny = y < ty ? y : ty;
		maxy = y < ty ? ty : y;
		minz = z < tz ? z : tz;
		maxz = z < tz ? tz : z;
		minx -= searchBufferRange;
		maxx += searchBufferRange;
		miny -= searchBufferRange;
		maxy += searchBufferRange;
		minz -= searchBufferRange;
		maxz += searchBufferRange;
		this.startTime = System.nanoTime();
		this.currentNode = getOrMakeNode(sx, sy, sz, null);
		this.currentNode.g = 0;
		this.currentNode.f = this.currentNode.getH(tx, ty, tz);
		this.qNodes.offer(this.currentNode);
		this.bestEndNode = this.currentNode;
		this.bestPathLength = 0;
		this.bestPathDist = Float.POSITIVE_INFINITY;
		this.searchIteration = 0;
		this.runTime = 0;
	}

	protected void onPathFound() {
		LinkedList<Node> path = new LinkedList<Node>();
		Node n = this.currentNode;
		Node c = null;
		Node p = null;
		while (n != null) {
			p = c;
			c = new Node(n.x, n.y, n.z);
			c.parentNode = p;
			path.push(c);
			n = n.parentNode;
		}
		if (this.caller != null) {
			this.caller.onPathFound(path);
		}
		this.currentNode = null;
		this.world = null;
		this.bestEndNode = null;
		this.allNodes.clear();
		this.qNodes.clear();
		this.searchNodes.clear();
		this.isSearching = false;
	}

	public boolean isSearching = false;
	protected boolean instantSearch = false;

	public void doSearchIterations(int num) {
		this.startTime = System.nanoTime();
		if (!isSearching) {
			return;
		}
		Node n;
		for (int i = 0; i < num; i++) {
			if (this.searchLoop()) {
				this.onPathFound();
				this.isSearching = false;
				break;
			}
		}
		this.runTime += System.nanoTime() - this.startTime;
		//ServerPerformanceMonitor.addPathfindingTime(System.nanoTime() - this.startTime); TODO perf monitoring??
	}

	private boolean searchLoop() {
		boolean isDoor = false;
		boolean isPDoor = false;
		Node goalCache = new Node(tx, ty, tz);
		boolean goalWalkable = world.isWalkable(tx, ty, tz) && world.isWalkable(tx, ty + 1, tz);
		this.searchIteration++;
		if (this.qNodes.isEmpty()) {
			return true;
		}
		this.currentNode = this.qNodes.poll();
		this.allNodes.add(currentNode);
		if (currentNode.equals(tx, ty, tz)) {
			return true;
		} else if (!goalWalkable)//TODO hack to get around un-pathable target positions
		{
			if (currentNode.getDistanceFrom(tx, ty, tz) <= 2.d) {
				return true;
			}
		}
		if (shouldTerminateEarly()) {
			return true;
		}
		currentNode.closed = true;
		this.findNeighbors(currentNode);
		float tent;
		isDoor = world.isDoor(currentNode.getPos());
		isPDoor = currentNode.parentNode != null && world.isDoor(currentNode.parentNode.getPos());
		boolean isNDoor = false;
		for (Node n : this.searchNodes) {
			isNDoor = world.isDoor(n.getPos());
			//could test for goal here, and if found, set n.f =0, insert to priority q (force to head of line)
			tent = currentNode.g + currentNode.getDistanceFrom(n);
			if (n.closed && tent > n.g)//new path from current node to n (already examined node) is longer than n's current path, disregard
			{
				continue;
			}
			if (!qNodes.contains(n) || tent < n.g)//if we haven't seen n before, or if we have but the path through current to n is less than n's best known path
			{//update n's stats to path through current -> n
				//this is where it deviates from A*, we will check to see if n can see the parent of current.  if so
				//we calculate the path to n as if it went through the parent of current, skipping current completely.
				if (!isPDoor && !isDoor && !isNDoor && canSeeParent(n, currentNode.parentNode))//don't skip doors...
				{
					n.parentNode = currentNode.parentNode;
					n.g = n.parentNode.g + n.getDistanceFrom(n.parentNode);
					n.f = n.g + n.getH(tx, ty, tz);
				} else//else if we cannot skip nodes, link n to current and calc path length
				{
					n.parentNode = currentNode;
					n.g = tent;
					n.f = n.g + n.getH(tx, ty, tz);
				}
				if (!qNodes.contains(n))//if we're not already going to examine n, put it in line to be examined
				{
					qNodes.offer(n);
				}
				n.closed = false;//go ahead and set n to open again...I don't think this really matters....
			}
		}
		return false;
	}

	private boolean shouldTerminateEarly() {
		if (runTime > maxRunTime) {
			//    Config.logDebug("search time exceeded max of: "+(this.maxRunTime/1000000)+"ms, terminating search.");
			return true;
		}
		if (this.searchIteration > this.maxSearchIterations) {
			//    Config.logDebug("search iterations exceeded max of: "+this.maxSearchIterations+ " terminating search.");
			return true;
		}
		float dist = this.currentNode.getDistanceFrom(tx, ty, tz);
		float len = this.currentNode.getPathLength();
		if (dist < bestPathDist)//|| len > bestPathLength
		{
			this.bestEndNode = this.currentNode;
			this.bestPathDist = dist;
			this.bestPathLength = len;
		}
		if (len > maxRange) {
			//    Config.logDebug("search length exceeded max of: "+this.maxRange+", terminating search.");
			return true;
		}
		return false;
	}

	private boolean canSeeParent(Node n, Node p) {
		if (p == null || n == null) {
			return false;
		}
		return PathUtils.canPathStraightToTargetLevel(world, n.x, n.y, n.z, p.x, p.y, p.z);
	}

	private void findNeighbors(Node n) {
		this.searchNodes.clear();
		tryAddSearchNode(n.x - 1, n.y, n.z, n);
		tryAddSearchNode(n.x + 1, n.y, n.z, n);
		tryAddSearchNode(n.x, n.y, n.z - 1, n);
		tryAddSearchNode(n.x, n.y, n.z + 1, n);

		/**
		 * diagonals -- check to make sure the path on the crossing blocks is clear
		 */
		if (world.isWalkable(n.x, n.y, n.z + 1) && world.isWalkable(n.x - 1, n.y, n.z)) {
			tryAddSearchNode(n.x - 1, n.y, n.z + 1, n);
		}
		if (world.isWalkable(n.x, n.y, n.z + 1) && world.isWalkable(n.x + 1, n.y, n.z)) {
			tryAddSearchNode(n.x + 1, n.y, n.z + 1, n);
		}
		if (world.isWalkable(n.x + 1, n.y, n.z) && world.isWalkable(n.x, n.y, n.z - 1)) {
			tryAddSearchNode(n.x + 1, n.y, n.z - 1, n);
		}
		if (world.isWalkable(n.x - 1, n.y, n.z) && world.isWalkable(n.x, n.y, n.z - 1)) {
			tryAddSearchNode(n.x - 1, n.y, n.z - 1, n);
		}

		/**
		 * up/down (in case of ladder/water)
		 */
		tryAddSearchNode(n.x, n.y + 1, n.z, n);
		tryAddSearchNode(n.x, n.y - 1, n.z, n);

		/**
		 * and the NSEW Y+1/Y-1 jumpable blocks..(not diagonal jumps)
		 */
		tryAddSearchNode(n.x - 1, n.y + 1, n.z, n);
		tryAddSearchNode(n.x + 1, n.y + 1, n.z, n);
		tryAddSearchNode(n.x, n.y + 1, n.z - 1, n);
		tryAddSearchNode(n.x, n.y + 1, n.z + 1, n);

		tryAddSearchNode(n.x - 1, n.y - 1, n.z, n);
		tryAddSearchNode(n.x + 1, n.y - 1, n.z, n);
		tryAddSearchNode(n.x, n.y - 1, n.z - 1, n);
		tryAddSearchNode(n.x, n.y - 1, n.z + 1, n);

		/**
		 * add NSEW Y-2 jumpable-down blocks
		 */
		if (world.canDrop) {
			tryAddSearchNode(n.x - 1, n.y - 2, n.z, n);
			tryAddSearchNode(n.x + 1, n.y - 2, n.z, n);
			tryAddSearchNode(n.x, n.y - 2, n.z - 1, n);
			tryAddSearchNode(n.x, n.y - 2, n.z + 1, n);
		}
	}

	private void tryAddSearchNode(int x, int y, int z, Node p) {
		if (x < minx || x > maxx || y < miny || y > maxy || z < minz || z > maxz) {
			return;
		}
		if (world.isWalkable(x, y, z)) {
			if (p != null && p.y != y && (p.z != z || p.x != x))//moving from a different height, but not directly up/down
			{
				if (p.y > y)//moving down from parent, check y -> y +2
				{
					if (!world.checkBlockBounds(x, y + 2, z))//.isCube(x, y+2, z))
					{
						return;
					}
				} else if (p.y < y)//moving up from parent, check parent.y ->parent.y+2
				{
					if (world.isPartialBlock(p.getPos().down()))//check to make sure its not going to be too far to jump up
					{
						return;
					}
					if (!world.checkBlockBounds(p.x, p.y + 2, p.z)) {
						return;
					}
					//        if(world.isCube(p.x, p.y+2, p.z))
					//          {
					//          return;
					//          }
				}
			}
			searchNodes.add(getOrMakeNode(x, y, z, p));
		}
	}

	private Node getOrMakeNode(int x, int y, int z, Node p) {
		Node n = null;
		for (Node c : this.allNodes) {
			if (c.equals(x, y, z)) {
				return c;
			}
		}
		n = new Node(x, y, z);
		if (p != null) {
			n.travelCost = world.getTravelCost(new BlockPos(x, y, z));
			n.parentNode = p;
			n.g = p.g + n.getDistanceFrom(p) + n.travelCost;
			n.f = n.g + n.getDistanceFrom(tx, ty, tz);
		}
		allNodes.add(n);
		return n;
	}

}
