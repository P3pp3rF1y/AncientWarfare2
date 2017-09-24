package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;

import static net.shadowmage.ancientwarfare.core.render.BlockRenderProperties.FACING;

public class ItemBlockOwnedRotatable extends ItemBlockBase {

    IRotatableBlock rotatable;

    public <T extends Block & IRotatableBlock> ItemBlockOwnedRotatable(T block) {
        super(block);
        rotatable = block;
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        EnumFacing facing = BlockRotationHandler.getFaceForPlacement(player, rotatable, side);
        newState = newState.withProperty(FACING, facing);
        boolean val = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
        if (val) {
            TileEntity te = player.world.getTileEntity(pos);
            if (te instanceof IOwnable) {
                ((IOwnable) te).setOwner(player);
            }
        }
        return val;
    }
}
