package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TreeFinder {

    private static final int[][] offsets = new int[][]{
            {-1, 0, 0}, {+1, 0, 0}, {0, 0, -1}, {0, 0, +1},
            {-1, 0, -1}, {-1, 0, +1}, {+1, 0, -1}, {+1, 0, +1},
            {-1, 1, 0}, {+1, 1, 0}, {0, 1, -1}, {0, 1, +1},
            {-1, 1, -1}, {-1, 1, +1}, {+1, 1, -1}, {+1, 1, +1}, {0, 1, 0},
            {-2, 0, 0}, {+2, 0, 0}, {0, 0, -2}, {0, 0, +2}
    };

    public static final TreeFinder DEFAULT = new TreeFinder();
    private final int max;
    public TreeFinder(int size){
        if(size<=offsets.length)
            max = size;
        else
            max = offsets.length;
    }

    private TreeFinder(){
        max = offsets.length;
    }

    public void findAttachedTreeBlocks(Block blockType, World world, BlockPosition pos, Set<BlockPosition> addTo) {
        LinkedList<BlockPosition> openList = new LinkedList<BlockPosition>();
        List<BlockPosition> badNodes = new ArrayList<BlockPosition>();
        List<BlockPosition> foundNodes = new ArrayList<BlockPosition>();
        BlockPosition node = new BlockPosition(pos);
        openList.add(node);

        while (!openList.isEmpty()) {
            node = openList.poll();
            foundNodes.add(node);
            addNeighborNodes(world, node.x, node.y, node.z, blockType, openList, badNodes, foundNodes);
        }

        addTo.addAll(foundNodes);
    }

    private void addNeighborNodes(World world, int x, int y, int z, Block blockType, List<BlockPosition> openList, List<BlockPosition> badNodes, List<BlockPosition> foundNodes) {

        for (int i = 0; i < max; i++) {
            int[] offset = offsets[i];
            BlockPosition n = new BlockPosition(x + offset[0], y + offset[1], z + offset[2]);
            if (!badNodes.contains(n) && !openList.contains(n) && !foundNodes.contains(n)) {
                if (isTree(world, n, blockType)) {
                    openList.add(n);
                } else {
                    badNodes.add(n);
                }
            }
        }
    }

    private static boolean isTree(World world, BlockPosition pos, Block blockType) {
        return world.getBlock(pos.x, pos.y, pos.z) == blockType;
    }

}
