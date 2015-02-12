package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;

public class ItemBlockOwnedRotatable extends ItemBlock {

    IRotatableBlock rotatable;

    public ItemBlockOwnedRotatable(Block p_i45328_1_) {
        super(p_i45328_1_);
        if (!(p_i45328_1_ instanceof IRotatableBlock)) {
            throw new IllegalArgumentException("Must be a rotatable block!!");
        }
        rotatable = (IRotatableBlock) p_i45328_1_;
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        metadata = BlockRotationHandler.getMetaForPlacement(player, rotatable, side);
        boolean val = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
        if (val) {
            String name = player.getCommandSenderName();
            TileEntity te = player.worldObj.getTileEntity(x, y, z);
            if (te instanceof IOwnable) {
                ((IOwnable) te).setOwnerName(name);
            }
        }
        return val;
    }

    @Override
    public int getDamage(ItemStack stack) {
        RotationType t = rotatable.getRotationType();
        if (t == RotationType.SIX_WAY) {
            return 0;
        }
        return 3;
    }

}
