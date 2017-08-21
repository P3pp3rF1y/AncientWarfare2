package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

public class ItemBlockRotatableMetaTile extends ItemBlock {

    IRotatableBlock rotatable;

    public ItemBlockRotatableMetaTile(Block block) {
        super(block);
        if (!(block instanceof IRotatableBlock)) {
            throw new IllegalArgumentException("Must be a rotatable block!!");
        }
        rotatable = (IRotatableBlock) block;
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, int side, float hitX, float hitY, float hitZ, int metadata) {
        boolean val = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
        if (val) {
            TileEntity te = player.world.getTileEntity(x, y, z);
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
