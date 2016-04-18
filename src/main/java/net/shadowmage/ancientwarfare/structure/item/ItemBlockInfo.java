package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;

public class ItemBlockInfo extends Item {

    public ItemBlockInfo(String regName) {
        this.setUnlocalizedName(regName);
        this.setCreativeTab(AWStructuresItemLoader.structureTab);
        this.setTextureName("ancientwarfare:structure/block_info");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if(!world.isRemote) {
            BlockPosition pos = BlockTools.getBlockClickedOn(player, player.worldObj, false);
            if (pos != null) {
                Block block = world.getBlock(pos.x, pos.y, pos.z);
                int meta = world.getBlockMetadata(pos.x, pos.y, pos.z);
                AWLog.logDebug("block: " + BlockDataManager.INSTANCE.getNameForBlock(block) + ", meta: " + meta);
                if(block.hasTileEntity(meta)){
                    AWLog.logDebug("tile: " + world.getTileEntity(pos.x, pos.y, pos.z).getClass());
                }
            }
        }
        return stack;
    }
}
