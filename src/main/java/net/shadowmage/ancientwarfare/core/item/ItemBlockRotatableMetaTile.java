package net.shadowmage.ancientwarfare.core.item;

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
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

public class ItemBlockRotatableMetaTile extends ItemBlock {

    private IRotatableBlock rotatable;

    public ItemBlockRotatableMetaTile(Block block) {
        super(block);
        if (!(block instanceof IRotatableBlock)) {
            throw new IllegalArgumentException("Must be a rotatable block!!");
        }
        rotatable = (IRotatableBlock) block;
        setRegistryName(block.getRegistryName());
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        boolean val = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
        if (val) {
            TileEntity te = player.world.getTileEntity(pos);
            if (te instanceof IOwnable) {
                ((IOwnable) te).setOwner(player);
            }
            if (te instanceof IRotatableTile) {
                ((IRotatableTile) te).setPrimaryFacing(BlockRotationHandler.getFaceForPlacement(player, rotatable, side));
            }
            BlockTools.notifyBlockUpdate(world, pos);
        }
        return val;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "." + stack.getItemDamage();
    }

}
