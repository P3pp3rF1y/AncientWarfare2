package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class BlockFinder {
    private final World world;
    private final Block blockType;
    private final int metaValue;
    public BlockFinder(World worldIn, Block type, int meta){
        world = worldIn;
        blockType = type;
        metaValue = meta;
    }

    public Pair<BlockPosition, BlockPosition> cross(BlockPosition center, BlockPosition max, List<BlockPosition> positions) {
        positions.add(center);
        int minX = center.x - 1;
        for (;center.x - minX <= max.x && isTypeAt(minX, center.y, center.z); minX--){
            positions.add(new BlockPosition(minX, center.y, center.z));
        }
        minX++;
        int maxX = center.x + 1;
        for (;maxX - minX <= max.x && isTypeAt(maxX, center.y, center.z); maxX++){
            positions.add(new BlockPosition(maxX, center.y, center.z));
        }
        maxX--;
        int minY = center.y - 1;
        for (;center.y - minY <= max.y && isTypeAt(center.x, minY, center.z); minY--){
            positions.add(new BlockPosition(center.x, minY, center.z));
        }
        minY++;
        int maxY = center.y + 1;
        for (;maxY - minY <= max.y && isTypeAt(center.x, maxY, center.z); maxY++){
            positions.add(new BlockPosition(center.x, maxY, center.z));
        }
        maxY--;
        int minZ = center.z - 1;
        for (;center.z - minZ <= max.z && isTypeAt(center.x, center.y, minZ); minZ--){
            positions.add(new BlockPosition(center.x, center.y, minZ));
        }
        minZ++;
        int maxZ = center.z + 1;
        for (;maxZ - minZ <= max.z && isTypeAt(center.x, center.y, maxZ); maxZ++){
            positions.add(new BlockPosition(center.x, center.y, maxZ));
        }
        maxZ--;
        return Pair.of(new BlockPosition(minX, minY, minZ), new BlockPosition(maxX, maxY, maxZ));
    }

    public boolean box(Pair<BlockPosition, BlockPosition> corners, List<BlockPosition> positions){
        BlockPosition temp;
        for(int x = corners.getLeft().x; x <= corners.getRight().x; x++){
            for(int y = corners.getLeft().y; y <= corners.getRight().y; y++){
                for(int z = corners.getLeft().z; z <= corners.getRight().z; z++){
                    temp = new BlockPosition(x, y, z);
                    if(!positions.contains(temp)){
                        if(isTypeAt(x, y, z))
                            positions.add(temp);
                        else
                            return false;
                    }
                }
            }
        }
        return true;
    }

    public void connect(BlockPosition center, List<BlockPosition> positions, ForgeDirection... directions){
        if(isTypeAt(center.x, center.y, center.z)) {
            if (!positions.contains(center))
                positions.add(center);
            for (ForgeDirection direction : directions) {
                connect(center.offset(direction.offsetX, direction.offsetY, direction.offsetZ), positions, directions);
            }
        }
    }

    public boolean isTypeAt(int x, int y, int z){
        return world.getBlock(x, y, z) == blockType && world.getBlockMetadata(x, y, z) == metaValue;
    }
}
