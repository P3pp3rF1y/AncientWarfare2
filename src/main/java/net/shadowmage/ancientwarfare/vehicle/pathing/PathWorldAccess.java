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
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.block.AWStructuresBlocks;
import shadowmage.ancient_warfare.common.block.BlockLoader;
import shadowmage.ancient_warfare.common.gates.TEGateProxy;

public class PathWorldAccess {

	public boolean canOpenDoors;
	public boolean canSwim;
	public boolean canDrop;
	public boolean canUseLaders;
	public boolean canGoOnLand = true;

	World world;

	public PathWorldAccess(World world) {
		this.world = world;
	}

	public void setCanGoOnLand(boolean val) {
		this.canGoOnLand = val;
		if (!val) {
			this.canSwim = true;
		}
	}

	public Block getBlock(BlockPos pos) {
		return world.getBlockState(pos).getBlock();
	}

	public int getTravelCost(int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		if ((id == Block.waterMoving.blockID || id == Block.waterStill.blockID))//can't swim check
		{
			if (!canGoOnLand) {
				return 10;
			}
			return 30;
		}
		if (!canGoOnLand) {
			return 30;
		}
		return 10;
	}

	/**
	 * checks the collision bounds of the block at x,y,z to make sure it is <= 0.5 tall (pathable)
	 * @param x
	 * @param y
	 * @param z
	 * @return true if it is a pathable block, false if it fails bounds checks
	 */
	public boolean checkBlockBounds(int x, int y, int z) {
		Block block;
		int id = world.getBlockId(x, y, z);
		if (id == Block.waterMoving.blockID || id == Block.waterStill.blockID) {
			return true;
		} else if (id == Block.trapdoor.blockID) {
			int meta = world.getBlockMetadata(x, y, z);
			return BlockTrapDoor.isTrapdoorOpen(meta);
		}
		block = Block.blocksList[id];
		if (block != null) {
			AxisAlignedBB bb = block.getCollisionBoundingBoxFromPool(world, x, y, z);
			if (bb == null) {
				return true;
			}
			block.setBlockBoundsBasedOnState(world, x, y, z);
			if (block.getBlockBoundsMaxY() >= 0.5d) {
				return false;
			}
		}
		return true;
	}

	public boolean isWalkable2(int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		int id2 = world.getBlockId(x, y - 1, z);
		int id3 = world.getBlockId(x, y + 1, z);
		boolean cube = !checkBlockBounds(x, y, z);
		boolean cube2 = !checkBlockBounds(x, y - 1, z);
		boolean cube3 = !checkBlockBounds(x, y + 1, z);
		if (isFence(id2) || (isDoor(x, y - 1, z) && isDoor(x, y,
				z)) || (id == Block.cactus.blockID || id2 == Block.cactus.blockID || id3 == Block.cactus.blockID)) {
			return false;
		}
		if (canGoOnLand) {
			if (canUseLaders && isLadder(id)) {
				return true;
			}
			if (canOpenDoors && isDoor(x, y, z) && cube2) {
				return true;
			}
			if (!cube && !cube3 && (cube2 || canSupport(id, x, y, z)))//finally, check if block and blockY+1 are clear and blockY-1 is solid
			{
				return true;
			}
		}
		if (canSwim && isWater(id) && id3 == 0) {
			return true;
		}
		return false;
	}

	public boolean isPartialBlock(int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		Block block;
		block = Block.blocksList[id];
		if (block != null) {
			AxisAlignedBB bb = block.getCollisionBoundingBoxFromPool(world, x, y, z);
			if (bb == null) {
				return false;
			}
			block.setBlockBoundsBasedOnState(world, x, y, z);
			if (block.getBlockBoundsMaxY() <= 0.75d) {
				if (block.getBlockBoundsMinX() < 0.35 && block.getBlockBoundsMaxX() > 0.65 && block.getBlockBoundsMinZ() < 0.35 && block
						.getBlockBoundsMaxZ() > 0.65) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean canSupport(int id, int x, int y, int z) {
		Block block;
		block = Block.blocksList[id];
		if (block != null) {
			if (block.blockID == Block.trapdoor.blockID) {
				int meta = world.getBlockMetadata(x, y, z);
				return !BlockTrapDoor.isTrapdoorOpen(meta) && (meta & 8) == 0;
			}
			AxisAlignedBB bb = block.getCollisionBoundingBoxFromPool(world, x, y, z);
			if (bb == null) {
				return false;
			}
			block.setBlockBoundsBasedOnState(world, x, y, z);
			if (block.getBlockBoundsMaxY() <= 0.5d) {
				if (block.getBlockBoundsMinX() < 0.35 && block.getBlockBoundsMaxX() > 0.65 && block.getBlockBoundsMinZ() < 0.35 && block
						.getBlockBoundsMaxZ() > 0.65) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isFence(int id) {
		return id == Block.fence.blockID || id == Block.fenceGate.blockID || id == Block.cobblestoneWall.blockID;
	}

	public boolean isWalkable(int x, int y, int z) {
		return isWalkable2(x, y, z);
		//  int id = world.getBlock(x, y, z);
		//  int id2 = world.getBlock(x, y-1, z);
		//  int id3 = world.getBlock(x, y+1, z);
		//  boolean cube = !checkBlockBounds(x, y, z);//isSolidBlock(id);
		//  boolean cube2 = !checkBlockBounds(x, y-1, z);//isSolidBlock(id2);
		//  boolean cube3 = !checkBlockBounds(x, y+1, z);//isSolidBlock(id3);
		//  /**
		//   * check basic early out
		//   * check for doors
		//   * check for gates
		//   * check for ladders
		//   * check for water
		//   * check block bounds
		//   */
		//  if(!isPathable(id))//solid unpassable block, or lava
		//    {
		//    return false;
		//    }
		//  else if(isGate(id2))
		//    {
		//    return false;
		//    }
		//  else if(id==0 && cube2 && id3==0)//early out check for the most basic of pathable areas
		//    {
		//    return true;
		//    }
		//  else if(id==BlockLoader.gateProxy.blockID)
		//    {
		//    if(!canOpenDoors)//if can't open doors, auto fail
		//      {
		//      return false;
		//      }
		//    else if(!cube2 || (cube3 && id3!=BlockLoader.gateProxy.blockID) || id2==BlockLoader.gateProxy.blockID || id3==0)
		//      {
		//      /**
		//       * else fail out if block below is not solid, or block above IS solid but not a gate block
		//       * or block below is a gate block (dont' walk in a gate block ON a gate block)
		//       * (allow gate blocks because they are generally tall...)
		//       */
		//      return false;
		//      }
		//    else
		//      {
		//      TEGateProxy proxy = (TEGateProxy)world.getBlockTileEntity(x, y, z);
		//      if(proxy.owner==null || !proxy.owner.getGateType().canSoldierActivate() || proxy.owner.wasPowered)
		//        {
		//        return false;
		//        }
		//      }
		//    }
		//  else if(isDoor(x, y, z))
		//    {
		//    if(!canOpenDoors)
		//      {
		//      return false;
		//      }
		//    }
		//  else if(isWater(id))//can't swim check
		//    {
		//    if(!canSwim)
		//      {
		//      return false;
		//      }
		//    else if(id3!=0)
		//      {
		//      return false;
		//      }
		//    }
		//  else if(isLadder(id))
		//    {
		//    if(!canUseLaders && !cube2)//ladder use check -- if block is a ladder with air below it
		//      {
		//      return false;
		//      }
		//    }
		//  else if(!cube2 && !isLadder(id2) && !isLadder(id))//or if air below and not a ladder
		//    {
		//    return false;
		//    }
		//  else if(cube || cube3)//no room to move
		//    {
		//    return false;
		//    }
		//  return true;
	}

	public boolean isGate(int id) {
		return id == Block.fence.blockID || id == Block.fenceGate.blockID || id == Block.cobblestoneWall.blockID;
	}

	public boolean isWater(int id) {
		return id == Block.waterMoving.blockID || id == Block.waterStill.blockID;
	}

	public boolean isDoor(BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block == AWStructuresBlocks.gateProxy) {
			TEGateProxy proxy = (TEGateProxy) world.getBlockTileEntity(x, y, z);
			if (proxy.owner == null || !proxy.owner.getGateType().canSoldierActivate() || proxy.owner.isLocked) {
				return false;
			}
			return true;
		}
		return id == Block.doorWood.blockID || id == Block.fenceGate.blockID;
	}

	protected boolean isCube(int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		return isSolidBlock(id);
	}

	protected boolean isSolidBlock(int id) {
		Block block = Block.blocksList[id];
		if (block == null) {
			return false;
		}
		if (block.isOpaqueCube() || block.renderAsNormalBlock()) {
			return true;
		} else if (id == Block.fence.blockID || id == Block.fenceIron.blockID || id == Block.cobblestoneWall.blockID || id == Block.tilledField.blockID || id == Block.glass.blockID || id == Block.thinGlass.blockID) {
			return true;
		} else if (id == Block.woodSingleSlab.blockID || id == Block.stoneSingleSlab.blockID || id == Block.chest.blockID || id == Block.chestTrapped.blockID || id == Block.slowSand.blockID) {
			return true;
		} else if (id == BlockLoader.gateProxy.blockID) {
			return true;
		} else if (block instanceof BlockStairs || block instanceof BlockLeaves) {
			return true;
		}
		return false;
	}

	protected boolean isPathable(int id) {
		if (id == Block.lavaMoving.blockID || id == Block.lavaStill.blockID || id == Block.fence.blockID || id == Block.fenceIron.blockID || id == Block.cobblestoneWall.blockID || id == Block.thinGlass.blockID) {
			return false;
		}
		return true;
	}

	protected boolean checkColidingEntities(int x, int y, int z) {
		return false;
	}

	protected boolean isLadder(BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		return block == Blocks.LADDER || block == Blocks.VINE;
	}

	public boolean isWalkable(int x, int y, int z, Node src) {
		return this.isWalkable(x, y, z);
	}

	public boolean isRemote() {
		return false;
	}

}
