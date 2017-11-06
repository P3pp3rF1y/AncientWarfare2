package net.shadowmage.ancientwarfare.core.util;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class BlockFinder {
    private final World world;
    //TODO see if there's good way to change this to IBlockState instead
    private final Block blockType;
    private final int metaValue;
    private final List<BlockPos> positions;
    public BlockFinder(World worldIn, Block type, int meta, int size){
        world = worldIn;
        blockType = type;
        metaValue = meta;
        positions = new ArrayList<>(size);
    }

    /*
     * Collect block type in a cross pattern
     * @param center the center of the cross pattern
     * @param max the arms max length of the cross pattern
     * @return the corners of the box containing the cross
     */
    public Pair<BlockPos, BlockPos> cross(BlockPos initial) {
        List<BlockPos> allConnected = getAllConnectedBlocks(initial);

        int minX = initial.getX();
        int maxX = initial.getX();
        int minY = initial.getY();
        int maxY = initial.getY();
        int minZ = initial.getZ();
        int maxZ = initial.getZ();

        for(BlockPos pos : allConnected) {
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
            maxX = Math.max(maxX, pos.getX());
            maxY = Math.max(maxY, pos.getY());
            maxZ = Math.max(maxZ, pos.getZ());
        }
        return Pair.of(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ));
    }

    /*
     * Collect blocks between corners in a box pattern
     * Fail fast if block type doesn't apply
     * @param corners Bottom North West corner and Upper South East corner
     * @return true if all blocks between corners apply conditions
     */
    public boolean box(Pair<BlockPos, BlockPos> corners){
        BlockPos temp;
        for(int x = corners.getLeft().getX(); x <= corners.getRight().getX(); x++){
            for(int y = corners.getLeft().getY(); y <= corners.getRight().getY(); y++){
                for(int z = corners.getLeft().getZ(); z <= corners.getRight().getZ(); z++){
                    temp = new BlockPos(x, y, z);
                    if(!positions.contains(temp)){
                        if(isTypeAt(temp))
                            positions.add(temp);
                        else
                            return false;
                    }
                }
            }
        }
        return true;
    }

    /*
     * Collect all blocks that apply
     * @param corner Bottom North West corner
     * @param limit the max size parameters
     */
    public void connect(BlockPos corner, BlockPos limit){
        BlockPos temp;
        for(int i = 0; i < limit.getX(); i++){
            for(int j = 0; j < limit.getY(); j++){
                for(int k = 0; k < limit.getZ(); k++){
                    temp = corner.add(i, j, k);
                    if(!positions.contains(temp) && isTypeAt(temp)){
                        positions.add(temp);
                    }
                }
            }
        }
    }

    /*
     * The conditions applied on the block type
     */
    private boolean isTypeAt(int x, int y, int z){
        return isTypeAt(new BlockPos(x, y, z));
    }

    private boolean isTypeAt(BlockPos pos){
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() == blockType && state.getBlock().getMetaFromState(state) == metaValue;
    }

    /*
     * The collected block positions
     */
    public List<BlockPos> getPositions() {
        return positions;
    }

    public List<BlockPos> getAllConnectedBlocks(BlockPos initialPos) {
        int maxRadius = 20;
        List<BlockPos> connectedBlocks = Lists.newArrayList();
        List<BlockPos> searchedPositions = Lists.newArrayList();

        connectedBlocks.add(initialPos);
        searchedPositions.add(initialPos);

        getConnectedBlocks(maxRadius, connectedBlocks, searchedPositions, initialPos, initialPos);

        return connectedBlocks;
    }

    private void getConnectedBlocks(int maxRadius, List<BlockPos> connectedBlocks, List<BlockPos> searchedPositions, BlockPos currentPos, BlockPos initialPos) {
        if(currentPos.getDistance(initialPos.getX(), initialPos.getY(), initialPos.getZ()) < maxRadius) {
            for(EnumFacing facing : EnumFacing.VALUES) {
                BlockPos offsetPos = currentPos.offset(facing);
                IBlockState state = world.getBlockState(offsetPos);
                if (!searchedPositions.contains(offsetPos) && state.getBlock() == blockType && state.getBlock().getMetaFromState(state) == metaValue) {
                    connectedBlocks.add(offsetPos);
                    searchedPositions.add(offsetPos);
                    getConnectedBlocks(maxRadius, connectedBlocks, searchedPositions, offsetPos, initialPos);
                }
            }
        }
    }
}
