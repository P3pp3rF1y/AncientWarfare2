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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class EntityPath {

	private LinkedList<Node> path = new LinkedList<Node>();

	public EntityPath() {

	}

	public void setPath(List<Node> pathNodes) {
		this.clearPath();
		this.path.addAll(pathNodes);
	}

	public void clearPath() {
		this.path.clear();
	}

	/**
	 * adds nodes onto a path, towards a new/updated target, checks new path vs old path and removes un-needed nodes
	 *
	 * @param pathNodes
	 */
	public void addPath(PathWorldAccess world, List<Node> pathNodes) {
		//check current path from the (current) start, try to 'see' the start node in new path, if so, remove the rest of current path
		Node n = null;
		Node start = null;
		if (pathNodes.size() > 0) {
			start = pathNodes.get(0);
		}
		if (start == null) {
			return;
		}
		Iterator<Node> it = this.path.iterator();
		boolean couldSee = false;
		while (it.hasNext()) {
			n = it.next();
			if (!couldSee) {
				if (PathUtils.canPathStraightToTarget(world, n.x, n.y, n.z, start.getPos())) {
					couldSee = true;
				}
			} else {
				it.remove();
				//      Config.logDebug("removing uneeded node: "+n);
			}
		}
		this.path.addAll(pathNodes);
	}

	public boolean containsPoint(int x, int y, int z) {
		for (Node n : this.path) {
			if (n.equals(x, y, z)) {
				return true;
			}
		}
		return false;
	}

	public Node getEndNode() {
		return this.path.peekLast();
	}

	/**
	 * return the first node (does not remove)
	 *
	 * @return
	 */
	public Node getFirstNode() {
		return this.path.peekFirst();
	}

	/**
	 * return and remove the first node
	 *
	 * @return
	 */
	public Node claimNode() {
		return this.path.poll();
	}

	public int getActivePathSize() {
		return this.path.size();
	}

	public float getActivePathLength() {
		if (this.path.isEmpty()) {
			return 0;
		}
		return this.path.get(path.size() - 1).getPathLength();
	}

	public List<Node> getActivePath() {
		return path;
	}

}
