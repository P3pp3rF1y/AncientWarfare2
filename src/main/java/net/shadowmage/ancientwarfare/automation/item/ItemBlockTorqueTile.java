package net.shadowmage.ancientwarfare.automation.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueBase;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;

public class ItemBlockTorqueTile extends ItemBlock {

    IRotatableBlock rotatable;

    public ItemBlockTorqueTile(Block p_i45328_1_) {
        super(p_i45328_1_);
        if (!(p_i45328_1_ instanceof IRotatableBlock)) {
            throw new IllegalArgumentException("Must be a rotatable block!!");
        }
        rotatable = (IRotatableBlock) p_i45328_1_;
        this.setHasSubtypes(true);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        boolean val = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
        if (val) {
            TileTorqueBase te = (TileTorqueBase) player.world.getTileEntity(pos);
            if (te instanceof IOwnable) {
                ((IOwnable) te).setOwner(player);
            }
            EnumFacing facing = BlockRotationHandler.getFaceForPlacement(player, rotatable, side);
            te.setPrimaryFacing(facing);
        }
        return val;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "." + stack.getItemDamage();
    }
}
