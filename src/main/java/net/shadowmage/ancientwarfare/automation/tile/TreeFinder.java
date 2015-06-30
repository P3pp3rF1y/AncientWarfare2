package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TreeFinder {

    static int[][] offsets = new int[21][3];

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
        offsets[17] = new int[]{-2, 0, 0};
        offsets[18] = new int[]{+2, 0, 0};
        offsets[19] = new int[]{0, 0, -2};
        offsets[20] = new int[]{0, 0, +2};
    }
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
