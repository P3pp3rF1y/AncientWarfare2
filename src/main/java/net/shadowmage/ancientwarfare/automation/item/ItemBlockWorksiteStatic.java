package net.shadowmage.ancientwarfare.automation.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IBoundedSite;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

public class ItemBlockWorksiteStatic extends ItemBlock {

    public ItemBlockWorksiteStatic(Block p_i45328_1_) {
        super(p_i45328_1_);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        int face = BlockTools.getPlayerFacingFromYaw(player.rotationYaw);
        BlockPosition pos1 = new BlockPosition(x, y, z).moveForward(face, 1).moveLeft(face, 2);
        BlockPosition pos2 = new BlockPosition(pos1).moveForward(face, 4).moveRight(face, 4);
        /**
         * TODO validate that block is not inside work bounds of any other nearby worksites ??
         * TODO validate that worksite does not intersect any others
         */
        int ormetadata = BlockRotationHandler.getMetaForPlacement(player, (IRotatableBlock) field_150939_a, side);

        boolean val = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
        if (val) {
            TileEntity worksite = world.getTileEntity(pos);
            if (worksite instanceof IBoundedSite) {
                ((IBoundedSite) worksite).setBounds(pos1, pos2);
            }
            if (worksite instanceof IOwnable) {
                ((IOwnable) worksite).setOwner(player);
            }
            if (worksite instanceof IRotatableTile) {
                EnumFacing o = EnumFacing.values()[ormetadata];
                ((IRotatableTile) worksite).setPrimaryFacing(o);
            }
            world.markBlockForUpdate(x, y, z);
        }
        return val;
    }

    @Override
    public int getDamage(ItemStack stack) {
        return 3;
    }

}
