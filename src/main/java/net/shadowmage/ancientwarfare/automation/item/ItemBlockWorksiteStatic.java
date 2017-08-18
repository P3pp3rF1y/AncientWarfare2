package net.shadowmage.ancientwarfare.automation.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IBoundedSite;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ItemBlockWorksiteStatic extends ItemBlock {

    public ItemBlockWorksiteStatic(Block p_i45328_1_) {
        super(p_i45328_1_);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        EnumFacing playerFacing = player.getHorizontalFacing();
        BlockPos pos1 = pos.offset(playerFacing).offset(playerFacing.rotateYCCW(), 2);
        BlockPos pos2 = pos.offset(playerFacing,4).offset(playerFacing.rotateY(), 4);
        /**
         * TODO validate that block is not inside work bounds of any other nearby worksites ??
         * TODO validate that worksite does not intersect any others
         */
        boolean val = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
        if (val) {
            TileEntity worksite = world.getTileEntity(pos);
            if (worksite instanceof IBoundedSite) {
                ((IBoundedSite) worksite).setBounds(pos1, pos2);
            }
            if (worksite instanceof IOwnable) {
                ((IOwnable) worksite).setOwner(player);
            }
            if (worksite instanceof IRotatableTile) {
                EnumFacing facing = BlockRotationHandler.getFaceForPlacement(player, (IRotatableBlock) block, side);
                ((IRotatableTile) worksite).setPrimaryFacing(facing);
            }
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
        return val;
    }

    @Override
    public int getDamage(ItemStack stack) {
        return 3;
    }

}
