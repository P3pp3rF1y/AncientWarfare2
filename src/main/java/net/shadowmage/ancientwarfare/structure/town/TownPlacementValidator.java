package net.shadowmage.ancientwarfare.structure.town;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureEntry;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureMap;
import net.shadowmage.ancientwarfare.structure.gamedata.TownMap;

import java.util.ArrayList;
import java.util.Collection;

public class TownPlacementValidator {
	private TownPlacementValidator() {}

	private static int maxSize = 21;
	private static final int MIN_STRUCTURE_DISTANCE = 16;

	/*
	 * input a single X, Y, Z coordinate to examine the nearby area for potential town generation.<br>
	 * First examines the chunk that was at the input coordinates, and expands out from there attempting
	 * to create the largest town-bounding area that it can.
	 *
	 * @param x world x to search from
	 * @param z world z to search from
	 * @return maximal bounding area for a town, or null if no acceptable area was found starting in the specified chunk
	 */
	static TownBoundingArea findGenerationPosition(World world, int x, int z) {
		if (world == null) {
			return null;
		}

		if (isTownClose(world, x, z)) {
			return null;
		}

		int cx = x >> 4;
		int cz = z >> 4;

		int height = getTopFilledHeight(world.getChunkFromChunkCoords(cx, cz), x, z);
		if (height <= 0) {
			return null;
		}

		int minY = Math.max(0, world.getSeaLevel() - 3);

		TownBoundingArea area = new TownBoundingArea();
		area.minY = Math.max(minY, height - 2);
		area.maxY = Math.min(255, area.minY + 17);
		area.chunkMinX = cx;
		area.chunkMaxX = cx;
		area.chunkMinZ = cz;
		area.chunkMaxZ = cz;

		StructureMap sm = AWGameData.INSTANCE.getPerWorldData(world, StructureMap.class);
		Collection<StructureEntry> structureList = sm.getEntriesNear(world, x, z, maxSize + 2, true, new ArrayList<>());

		if (isStructureInside(structureList, x, z, area.minY, area.maxY)) {
			return null;
		}

		expandBoundingArea(world, area, structureList);

        /*
		 * Shrink town along x or z axis if ratio of sizes is > 2:1
         */
		int cw = area.getChunkWidth();
		int cl = area.getChunkLength();
		if (cw > cl * 2) {
			int diff = cw - (cl * 2);
			while (diff > 0) {
				area.chunkMaxX--;
				diff--;
				if (diff > 0) {
					area.chunkMinX++;
					diff--;
				}
			}
		}
		if (cl > cw * 2) {
			int diff = cl - (cw * 2);
			while (diff > 0) {
				area.chunkMaxZ--;
				diff--;
				if (diff > 0) {
					area.chunkMinZ++;
					diff--;
				}
			}
		}
		return area;
	}

	private static boolean isStructureInside(Collection<StructureEntry> structureList, int chunkX, int chunkZ, int minY, int maxY) {
		for (StructureEntry structure : structureList) {
			if (structure.getBB().crossWith((chunkX >> 16) - MIN_STRUCTURE_DISTANCE, minY, (chunkZ >> 16) - MIN_STRUCTURE_DISTANCE,
					((chunkX + 1) >> 16) + MIN_STRUCTURE_DISTANCE, maxY, ((chunkZ + 1) >> 16) + MIN_STRUCTURE_DISTANCE)) {
				return true;
			}
		}

		return false;
	}

	private static boolean isTownClose(World world, int x, int z) {
		TownMap tm = AWGameData.INSTANCE.getPerWorldData(world, TownMap.class);
		if (tm == null) {
			return false;
		}
		int minDist = AWStructureStatics.townClosestDistance * 16;
		float dist = tm.getClosestTown(x, z, minDist * 2);
		return dist < minDist;
	}

	private static void expandBoundingArea(World world, TownBoundingArea area, Collection<StructureEntry> structureList) {
		boolean xneg = true;//if should try and expand on this direction next pass, once set to false it never checks that direction again
		boolean xpos = true;
		boolean zneg = true;
		boolean zpos = true;
		boolean didExpand = false;//set to true if any expansion occurred on that pass.  if false at end of pass, will break out of loop as no more expansion is possible
		do {
			didExpand = false;
			if (xneg && area.getChunkWidth() <= maxSize) {
				xneg = tryExpandXNeg(world, area, structureList);
				didExpand = didExpand || xneg;
			}
			if (xpos && area.getChunkWidth() <= maxSize) {
				xpos = tryExpandXPos(world, area, structureList);
				didExpand = didExpand || xpos;
			}
			if (zneg && area.getChunkLength() <= maxSize) {
				zneg = tryExpandZNeg(world, area, structureList);
				didExpand = didExpand || zneg;
			}
			if (zpos && area.getChunkLength() <= maxSize) {
				zpos = tryExpandZPos(world, area, structureList);
				didExpand = didExpand || zpos;
			}
		} while (didExpand && (area.getChunkWidth() <= maxSize || area.getChunkLength() <= maxSize));
	}

	private static boolean tryExpandXNeg(World world, TownBoundingArea area, Collection<StructureEntry> structureList) {
		int cx = area.chunkMinX - 1;
		for (int z = area.chunkMinZ; z <= area.chunkMaxZ; z++) {
			if (!isAverageHeightWithin(world, cx, z, area.minY, area.maxY) || isStructureInside(structureList, cx, z, area.minY, area.maxY)) {
				return false;
			}
		}
		area.chunkMinX = cx;
		return true;
	}

	private static boolean tryExpandXPos(World world, TownBoundingArea area, Collection<StructureEntry> structureList) {
		int cx = area.chunkMaxX + 1;
		for (int z = area.chunkMinZ; z <= area.chunkMaxZ; z++) {
			if (!isAverageHeightWithin(world, cx, z, area.minY, area.maxY) || isStructureInside(structureList, cx, z, area.minY, area.maxY)) {
				return false;
			}
		}
		area.chunkMaxX = cx;
		return true;
	}

	private static boolean tryExpandZNeg(World world, TownBoundingArea area, Collection<StructureEntry> structureList) {
		int cz = area.chunkMinZ - 1;
		for (int x = area.chunkMinX; x <= area.chunkMaxX; x++) {
			if (!isAverageHeightWithin(world, x, cz, area.minY, area.maxY) || isStructureInside(structureList, x, cz, area.minY, area.maxY)) {
				return false;
			}
		}
		area.chunkMinZ = cz;
		return true;
	}

	private static boolean tryExpandZPos(World world, TownBoundingArea area, Collection<StructureEntry> structureList) {
		int cz = area.chunkMaxZ + 1;
		for (int x = area.chunkMinX; x <= area.chunkMaxX; x++) {
			if (!isAverageHeightWithin(world, x, cz, area.minY, area.maxY) || isStructureInside(structureList, x, cz, area.minY, area.maxY)) {
				return false;
			}
		}
		area.chunkMaxZ = cz;
		return true;
	}

	private static boolean isAverageHeightWithin(World world, int cx, int cz, int min, int max) {
		Chunk chunk = world.getChunkFromChunkCoords(cx, cz);
		int val;
		int total = 0;
		for (int x = (cx << 4); x < ((cx << 4) + 16); x++) {
			for (int z = (cz << 4); z < ((cz << 4) + 16); z++) {
				val = getTopFilledHeight(chunk, x, z);
				if (val < 0) {
					return false;
				}//exit out if a non-proper block-type is detected
				total += val;
			}
		}
		total /= 256; //make it the average top-height of all blocks in chunk
		return total >= min && total <= max;
	}

	/*
	 * return the highest Y that has a solid non-skipped block in it<br>
	 * This implementation skips water, air, and any blocks on the world-gen skippable blocks list (trees, plants, etc)
	 *
	 * @return top solid block height, or -1 for invalid top block or no top block found (void, bedrock...)
	 */
	private static int getTopFilledHeight(Chunk chunk, int x, int z) {
		int maxY = chunk.getTopFilledSegment() + 15;
		Block block;
		for (int y = maxY; y > 0; y--) {
			IBlockState state = chunk.getBlockState(new BlockPos(x, y, z));
			block = state.getBlock();
			if (AWStructureStatics.isSkippable(state)) {
				continue;
			}
			if (state.getMaterial().isLiquid()) {
				if (y >= 56) {
					continue;
				}// >=56 is fillable through underfill/border settings.  below that is too deep for a proper gradient on the border.
				return -1;//return invalid Y if liquid block is too low
			}
			if (!AWStructureStatics.isValidTargetBlock(state)) {
				AncientWarfareStructure.LOG.debug("rejecting town chunk for non-target block: " + block + " :: " + chunk.x + ":" + chunk.z);
				return -1;
			}
			return y;//if not skippable and is valid target block, return that y-level
		}
		return -1;
	}
}
