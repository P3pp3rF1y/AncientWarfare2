package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TreeFinder {

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
        ArrayList<BlockPosition> openList = new ArrayList<BlockPosition>(max*2);
        HashSet<BlockPosition> badNodes = new HashSet<BlockPosition>();
        openList.add(pos);
        while (!openList.isEmpty()){
            pos = openList.remove(openList.size() - 1);
            addTo.add(pos);
            addNeighborNodes(world, pos, blockType, openList, badNodes, addTo);
        }
    }

    private void addNeighborNodes(World world, BlockPosition pos, Block blockType, List<BlockPosition> openList, Set<BlockPosition> badNodes, Set<BlockPosition> foundNodes) {

        for (int i = 0; i < max; i++) {
            BlockPosition n = pos.offset(offsets[i][0], offsets[i][1], offsets[i][2]);
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
