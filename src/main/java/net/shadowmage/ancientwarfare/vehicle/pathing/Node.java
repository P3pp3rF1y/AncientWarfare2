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
import net.minecraft.util.math.MathHelper;

public class Node implements Comparable {

	public float goalLenght;
	public float travelCost = 10;

	public Node parentNode = null;
	public float g = Float.POSITIVE_INFINITY;
	public float f;

	public int x;
	public int y;
	public int z;
	public boolean obstacle = false;
	;
	public boolean closed = false;

	public Node(BlockPos pos) {
		this(pos.getX(), pos.getY(), pos.getZ());
	}

	public Node(int bX, int bY, int bZ) {
		this.x = bX;
		this.y = bY;
		this.z = bZ;
	}

	public Node(int x, int y, int z, Node parent, Node goal, float g) {
		this(x, y, z);
		this.parentNode = parent;
		this.g = g;
		this.f = this.getH(goal) + this.g;
	}

	public Node reassign(BlockPos pos) {
		return reassign(pos.getX(), pos.getY(), pos.getZ());
	}

	public Node reassign(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	/**
	 * calc travel cost of this node, and set to obstacle if completely unpathable (solid)
	 *
	 * @param world
	 */
	public void calcTraveCost(PathWorldAccess world, Node parentNode) {
		if (world == null) {
			return;
		}
		this.obstacle = false;
		this.travelCost = 10;
		this.obstacle = !world.isWalkable(x, y, z, parentNode);
	}

	protected float getH(Node b) {
		return getDistanceFrom(b) * 10 + travelCost;
	}

	protected float getH(int tx, int ty, int tz) {
		return getDistanceFrom(tx, ty, tz) * 10 + travelCost;
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof Node) {
			Node other = (Node) o;
			float thisVal = f;
			float otherVal = other.f;
			if (thisVal < otherVal) // lower cost = smaller natural value
			{
				return -1;
			} else if (thisVal > otherVal) // higher cost = higher natural value
			{
				return 1;
			}
		}
		return 0;
	}

	@Override
	public boolean equals(Object checkagainst) {
		if (checkagainst instanceof Node) {
			Node check = (Node) checkagainst;
			if (check.x == x && check.y == y && check.z == z) {
				return true;
			}
		}
		return false;
	}

	public boolean equals(int x, int y, int z) {
		return this.x == x && this.y == y && this.z == z;
	}

	public float getDistanceFrom(Node node) {
		if (node == null) {
			return 0;
		}
		float x = this.x - node.x;
		float y = this.y - node.y;
		float z = this.z - node.z;
		return MathHelper.sqrt(x * x + y * y + z * z);
	}

	public float getDistanceFrom(int x, int y, int z) {
		float x1 = this.x - x;
		float y1 = this.y - y;
		float z1 = this.z - z;
		return MathHelper.sqrt(x1 * x1 + y1 * y1 + z1 * z1);
	}

	public float getPathLength() {
		float len = 0;
		if (this.parentNode != null) {
			len += this.getDistanceFrom(parentNode);
			len += this.parentNode.getPathLength();
		}
		return len;
	}

	public BlockPos getPos() {
		return new BlockPos(x, y, z);
	}

	@Override
	public int hashCode() {
		return (x << 16) ^ z ^ (y << 24);
	}

	@Override
	public String toString() {
		return "Node: " + x + "," + y + "," + z + " TC: " + travelCost + " F: " + f;
	}

}
