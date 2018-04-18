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
import net.shadowmage.ancientwarfare.core.util.Trig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PathUtils {

	//sources of a few line-plotting algorithms...most..are..weird
	//http://playtechs.blogspot.com/2007/03/raytracing-on-grid.html
	//http://xnawiki.com/index.php/Voxel_traversal
	//http://www.cse.yorku.ca/~amana/research/grid.pdf

	public static Node getClosestPathableTo(PathWorldAccess world, int x, int y, int z, int maxH, int maxV, int ox, int oy, int oz) {
		Node closest = new Node(x, y, z);
		float closeDist = Float.POSITIVE_INFINITY;
		float tcloseDist = Float.POSITIVE_INFINITY;
		float dist;
		float tdist;
		for (int y1 = y - maxV; y1 <= y + maxV; y1++) {
			for (int x1 = x - maxH; x1 <= x + maxH; x1++) {
				for (int z1 = z - maxH; z1 <= z + maxH; z1++) {
					if (world.isWalkable(x1, y1, z1)) {
						dist = Trig.getDistance(x, y, z, x1, y1, z1);
						tdist = Trig.getDistance(ox, oy, oz, x1, y1, z1);
						if (dist < closeDist && tdist < tcloseDist) {
							closeDist = dist;
							tcloseDist = tdist;
							closest.reassign(x1, y1, z1);
						}
					}
				}
			}
		}
		return closest;

		//  for(int xo = 0; xo <= maxH; xo++)
		//    {
		//    for(int zo = 0; zo <=maxH; zo++)
		//      {
		//      for(int yo = 0; yo <=maxV; yo++)
		//        {
		//        int x1 = x+xo;
		//        int y1 = y+yo;
		//        int z1 = z+zo;
		//        int x2 = x-xo;
		//        int y2 = y-yo;
		//        int z2 = z-zo;
		////        Config.logDebug(String.format("checking: %s,%s,%s", x1,y1,z1));
		////        Config.logDebug(String.format("checking: %s,%s,%s", x2,y2,z2));
		//        if(world.isWalkable(x+xo, y+yo, z+zo))
		//          {
		//          return new Node(x+xo, y+yo, z+zo);
		//          }
		//        else if(world.isWalkable(x-xo, y-yo, z-zo))
		//          {
		//          return new Node(x-xo, y-yo, z-zo);
		//          }
		//        }
		//      }
		//    }
		//  return new Node(x,y,z);
	}

	/**
	 * uhh..yah...its freaky...but it finds paths (most of the time), FAST (all of the time)
	 *
	 * @param world
	 * @param ex
	 * @param ey
	 * @param ez
	 * @param tx
	 * @param ty
	 * @param tz
	 * @param numOfNodes
	 * @param rng
	 * @return
	 */
	public static List<Node> guidedCrawl(PathWorldAccess world, int ex, int ey, int ez, int tx, int ty, int tz, int numOfNodes, Random rng) {
		List<Node> nodes = new ArrayList<Node>();
		/**
		 * get initial move direction towards target.
		 * add start node to path
		 * for i < nodes
		 *   find next move direction from current move direction, based on prev. move and dir to target
		 *   add move vector to current
		 *   add new node
		 */
		int cx = ex;
		int cy = ey;
		int cz = ez;
		int mx = tx - ex;
		int mz = tz - ez;
		mx = mx < 0 ? -1 : mx > 1 ? 1 : mx;
		mz = mz < 0 ? -1 : mz > 1 ? 1 : mz;
		int moveDir = 8;
		int my = 0;
		int dx = mx;
		int dz = mz;
		int dy = my;
		nodes.add(new Node(cx, cy, cz));
		for (int i = 0; i < numOfNodes; i++) {
			if (tx < cy && world.isWalkable(cx, cy - 1, cz)) {
				cy--;
				nodes.add(new Node(cx, cy, cz));
			} else if (tx > cy && world.isWalkable(cx, cy + 1, cz)) {
				cy++;
				nodes.add(new Node(cx, cy, cz));
			} else if (world.isWalkable(cx + mx, cy + dy, cz + mz)) {
				dx = mx;
				dz = mz;
				cx += dx;
				cy += dy;
				cz += dz;
				nodes.add(new Node(cx, cy, cz));
			} else if (world.isWalkable(cx + dx, cy + dy, cz + dz)) {
				cx += dx;
				cy += dy;
				cz += dz;
				nodes.add(new Node(cx, cy, cz));
			} else if (world.isWalkable(cx + dx, cy, cz + dz)) {
				dy = 0;
				cx += dx;
				cy += dy;
				cz += dz;
				nodes.add(new Node(cx, cy, cz));
			} else if (world.isWalkable(cx + dx, cy - 1, cz + dz)) {
				dy = -1;
				cx += dx;
				cy += dy;
				cz += dz;
				nodes.add(new Node(cx, cy, cz));
			} else if (world.isWalkable(cx + dx, cy + 1, cz + dz)) {
				dy = 1;
				cx += dx;
				cy += dy;
				cz += dz;
				nodes.add(new Node(cx, cy, cz));
			} else {
				int turn = getRotationTowardTarget(dx, dz, mx, mz);
				int offset[];
				if (turn <= 0) {
					offset = getRotatedMoveDelta(dx, dz, -1);
				} else {
					offset = getRotatedMoveDelta(dx, dz, 1);
				}
				if (world.isWalkable(cx + offset[0], cy, cz + offset[1])) {
					dx = offset[0];
					dz = offset[1];
					dy = 0;
					cx += dx;
					cz += dz;
					cy += dy;
					nodes.add(new Node(cx, cy, cz));
				} else if (world.isWalkable(cx + offset[0], cy - 1, cz + offset[1])) {
					dx = offset[0];
					dz = offset[1];
					dy = -1;
					cx += dx;
					cz += dz;
					cy += dy;
					nodes.add(new Node(cx, cy, cz));
				} else if (world.isWalkable(cx + offset[0], cy + 1, cz + offset[1])) {
					dx = offset[0];
					dz = offset[1];
					dy = 1;
					cx += dx;
					cz += dz;
					cy += dy;
					nodes.add(new Node(cx, cy, cz));
				}
			}
			mx = tx - cx;
			mz = tz - cz;
			mx = mx < 0 ? -1 : mx > 1 ? 1 : mx;
			mz = mz < 0 ? -1 : mz > 1 ? 1 : mz;
			if (cx == tx && cy == ty && cz == tz) {
				//      Config.logDebug("crawl hit goal");
				break;
			}
		}
		return nodes;
	}

	private static int getRotationAmount(int amt, int base) {
		while (amt < 0) {
			amt += 8;
		}
		return amt;
	}

	private static int getRotationTowardTarget(int dx, int dz, int mx, int mz) {
		boolean foundCurrent = false;
		boolean foundBase = false;
		int currentOffset = 0;
		int baseOffset = 0;
		int offset[];
		for (int i = 0; i < offsets.length; i++) {
			offset = offsets[i];
			if (offset[0] == dx && offset[1] == dz) {
				currentOffset = i;
				foundCurrent = true;
			}
			if (offset[0] == mx && offset[1] == mz) {
				baseOffset = i;
				foundBase = true;
			}
			if (foundBase && foundCurrent) {
				break;
			}
		}
		return currentOffset - baseOffset;
	}

	private static int[] getRotatedMoveDelta(int dx, int dz, int turnAmt) {
		while (turnAmt < 0) {
			turnAmt += 8;
		}
		int rightTurns = turnAmt;
		int offsetNum = 0;
		int[] offset;
		for (int i = 0; i < offsets.length; i++) {
			offset = offsets[i];
			if (offset[0] == dx && offset[1] == dz) {
				offsetNum = i;
				break;
			}
		}
		for (int i = 0; i < rightTurns; i++) {
			offsetNum++;
			if (offsetNum >= offsets.length) {
				offsetNum = 0;
			}
		}
		if (offsetNum < 0) {
			offsetNum = 0;
		} else if (offsetNum >= offsets.length) {
			offsetNum = 0;
		}
		return offsets[offsetNum];
	}

	private static int[][] offsets = new int[8][2];

	static {
		offsets[0] = new int[] {0, 1};
		offsets[1] = new int[] {-1, 1};
		offsets[2] = new int[] {-1, 0};
		offsets[3] = new int[] {-1, -1};
		offsets[4] = new int[] {0, -1};
		offsets[5] = new int[] {1, -1};
		offsets[6] = new int[] {1, 0};
		offsets[7] = new int[] {1, 1};
	}

	public static boolean canPathStraightToTargetLevel(PathWorldAccess worldAccess, int x0, int ey, int z0, int x1, int ty, int z1) {
		if (ey != ty) {
			return false;
		}
		int dx = (int) Math.abs(x1 - x0);
		int dz = (int) Math.abs(z1 - z0);
		int sx = x0 < x1 ? 1 : -1;
		int sz = z0 < z1 ? 1 : -1;
		int err = dx - dz;
		int e2;
		boolean swim = worldAccess.canSwim;
		worldAccess.canSwim = false;
		boolean canPath = true;
		for (int i = 0; i < dx + dz; i++) {
			BlockPos pos = new BlockPos(x0, ey, z0);
			if (worldAccess.isDoor(pos)) {
				canPath = false;
				break;
			} else if (worldAccess.isLadder(pos)) {
				canPath = false;
				break;
			} else if (!worldAccess.isWalkable(x0, ey, z0)) {
				canPath = false;
				break;
			}
			if (x0 == x1 && z0 == z1) {
				break;//finished
			}
			e2 = 2 * err;
			if (e2 > -dz) {
				err = err - dz;
				x0 = x0 + sx;
			}
			if (e2 < dx) {
				err = err + dx;
				z0 = z0 + sz;
			}
		}
		worldAccess.canSwim = swim;
		return canPath;
	}

	public static boolean canPathStraightToTarget(PathWorldAccess worldAccess, int ex, int ey, int ez, BlockPos target) {
		int yOffset = target.getY() - ey;
		int currentY = ey;
		if (Math.abs(yOffset) > 1) {
			return false;
		}
		int dx = Math.abs(target.getX() - ex);
		int dz = Math.abs(target.getZ() - ez);
		int px = ex;
		int pz = ez;
		int sx = ex < target.getX() ? 1 : -1;
		int sz = ez < target.getZ() ? 1 : -1;
		int err = dx - dz;
		int e2;
		int cy = ey;
		boolean swim = worldAccess.canSwim;
		worldAccess.canSwim = false;
		boolean canPath = true;
		for (int i = 0; i < dx + dz; i++) {
			//test hit here..., break/return false
			if (worldAccess.isDoor(new BlockPos(ex, cy, ez))) {
				canPath = false;
				break;
			} else if (worldAccess.isLadder(new BlockPos(ex, cy, ey))) {
				canPath = false;
				break;
			}
			if (worldAccess.isWalkable(ex, cy - 1, ez) && !worldAccess.checkBlockBounds(ex, cy + 1, ez)) {
				cy--;
			} else if (worldAccess.isWalkable(ex, cy, ez)) {

			} else if (worldAccess.isWalkable(ex, cy + 1, ez) && !worldAccess.checkBlockBounds(px, cy + 2, pz)) {
				cy++;
			} else {
				canPath = false;
				break;
			}
			if (Math.abs(cy - target.getY()) > 1) {
				canPath = false;
				break;
			}
			if (ex == target.getX() && ez == target.getZ()) {
				break;//finished
			}
			e2 = 2 * err;
			px = ex;
			pz = ez;
			if (e2 > -dz) {
				err = err - dz;
				ex = ex + sx;
			}
			if (e2 < dx) {
				err = err + dx;
				ez = ez + sz;
			}
		}
		worldAccess.canSwim = swim;
		return canPath;
	}

	/**
	 * works....converted to pure integer math up above..not sure on the speedup/lack of
	 *
	 * @param x0
	 * @param z0
	 * @param x1
	 * @param z1
	 * @return
	 */
	public static List<BlockPos> getPositionsBetween2(float x0, float z0, float x1, float z1) {
		//http://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
		/**
		 * function line(x0, y0, x1, y1)
		 dx := abs(x1-x0)
		 dy := abs(y1-y0)
		 if x0 < x1 then sx := 1 else sx := -1
		 if y0 < y1 then sy := 1 else sy := -1
		 err := dx-dy

		 loop
		 setPixel(x0,y0)
		 if x0 = x1 and y0 = y1 exit loop
		 e2 := 2*err
		 if e2 > -dy then
		 err := err - dy
		 x0 := x0 + sx
		 end if
		 if e2 <  dx then
		 err := err + dx
		 y0 := y0 + sy
		 end if
		 end loop
		 */
		List<BlockPos> blocks = new ArrayList<BlockPos>();
		float dx = (int) Math.abs(x1 - x0);
		float dz = (int) Math.abs(z1 - z0);
		float sx = x0 < x1 ? 1 : -1;
		float sz = z0 < z1 ? 1 : -1;
		float err = dx - dz;
		float e2;
		for (int i = 0; i < dx + dz; i++) {
			blocks.add(new BlockPos(x0, 0, z0));
			//    Config.logDebug("hit: "+x0+","+z0);
			if (x0 == x1 && z0 == z1) {
				break;//finished
			}
			e2 = 2 * err;
			if (e2 > -dz) {
				err = err - dz;
				x0 = x0 + sx;
			}
			if (e2 < dx) {
				err = err + dx;
				z0 = z0 + sz;
			}
		}
		//Config.logDebug("hit: "+x0+","+z0);
		return blocks;
	}

	/**
	 * mostly works, returns a few 'odd' hits...
	 *
	 * @param x0
	 * @param y0
	 * @param z0
	 * @param x1
	 * @param y1
	 * @param z1
	 * @return
	 */
	public static List<BlockPos> getPositionsBetween(float x0, float y0, float z0, float x1, float y1, float z1) {//4-connected line alg...from..somewhere online (stack overflow post)
		/**
		 * void drawLine(int x0, int y0, int x1, int y1) {
		 int dx = abs(x1 - x0);
		 int dy = abs(y1 - y0);
		 int sgnX = x0 < x1 ? 1 : -1;
		 int sgnY = y0 < y1 ? 1 : -1;
		 int e = 0;
		 for (int i=0; i < dx+dy; i++) {
		 drawPixel(x0, y0);
		 int e1 = e + dy;
		 int e2 = e - dx;
		 if (abs(e1) < abs(e2)) {
		 x0 += sgnX;
		 e = e1;
		 } else {
		 y0 += sgnY;
		 e = e2;
		 }
		 }
		 }
		 */
		List<BlockPos> blocks = new ArrayList<BlockPos>();
		int dx = (int) Math.abs(x1 - x0);
		int dz = (int) Math.abs(z1 - z0);
		int sx = x0 < x1 ? 1 : -1;
		int sz = z0 < z1 ? 1 : -1;
		int e = 0;
		int e1;
		int e2;
		for (int i = 0; i < dx + dz; i++) {
			blocks.add(new BlockPos(x0, y0, z0));
			//    Config.logDebug("hit: "+x0+","+y0+","+z0);
			e1 = e + dz;
			e2 = e - dx;
			if (Math.abs(e1) < Math.abs(e2)) {
				x0 += sx;
				e = e1;
			} else {
				z0 += sz;
				e = e2;
			}
		}
		blocks.add(new BlockPos(x0, y0, z0));
		//  Config.logDebug("hit: "+x0+","+y0+","+z0);
		return blocks;
	}

}
