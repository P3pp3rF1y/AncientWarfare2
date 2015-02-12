package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockFinder {

    static int[][] offsets = new int[17][3];

    static {
        offsets[0] = new int[]{-1, 0, 0};
        offsets[1] = new int[]{+1, 0, 0};
        offsets[2] = new int[]{0, 0, -1};
        offsets[3] = new int[]{0, 0, +1};
        offsets[4] = new int[]{-1, 0, -1};
        offsets[5] = new int[]{-1, 0, +1};
        offsets[6] = new int[]{+1, 0, -1};
        offsets[7] = new int[]{+1, 0, +1};
        offsets[8] = new int[]{-1, 1, 0};
        offsets[9] = new int[]{+1, 1, 0};
        offsets[10] = new int[]{0, 1, -1};
        offsets[11] = new int[]{0, 1, +1};
        offsets[12] = new int[]{-1, 1, -1};
        offsets[13] = new int[]{-1, 1, +1};
        offsets[14] = new int[]{+1, 1, -1};
        offsets[15] = new int[]{+1, 1, +1};
        offsets[16] = new int[]{0, 1, 0};
    }

    public static void findConnectedSixWay(World world, int x, int y, int z, Block blockType, int metaValue, Set<BlockPosition> positions) {
        List<BlockPosition> openSet = new ArrayList<BlockPosition>();
        Set<BlockPosition> closedSet = new HashSet<BlockPosition>();
        Set<BlockPosition> neighborSet = new HashSet<BlockPosition>();
        Set<BlockPosition> foundPositions = new HashSet<BlockPosition>();

        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        BlockPosition pos = new BlockPosition(x, y, z);

        if (block == blockType && meta == metaValue) {
            openSet.add(pos);
        }
        while (!openSet.isEmpty()) {
            pos = openSet.remove(openSet.size() - 1);
            block = world.getBlock(pos.x, pos.y, pos.z);
            meta = world.getBlockMetadata(pos.x, pos.y, pos.z);
            if (block == blockType && meta == metaValue) {
                foundPositions.add(pos);
                findNeighborsSixWay(world, pos.x, pos.y, pos.z, neighborSet);
                for (BlockPosition p : neighborSet) {
                    if (!openSet.contains(p) && !foundPositions.contains(p) && !closedSet.contains(p)) {
                        openSet.add(p);
                    }
                }
                neighborSet.clear();
            } else {
                closedSet.add(pos);
            }
        }
        positions.addAll(foundPositions);
    }

    public static void findNeighborsSixWay(World world, int x, int y, int z, Set<BlockPosition> neighbors) {
        BlockPosition pos;
        ForgeDirection d;
        for (int i = 0; i < 6; i++) {
            d = ForgeDirection.getOrientation(i);
            pos = new BlockPosition(x + d.offsetX, y + d.offsetY, z + d.offsetZ);
            neighbors.add(pos);
        }
    }
}
