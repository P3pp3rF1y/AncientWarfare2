package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;

public class ItemBlockInfo extends Item implements IItemClickable {

    public ItemBlockInfo(String regName) {
        this.setUnlocalizedName(regName);
        this.setCreativeTab(AWStructuresItemLoader.structureTab);
        this.setTextureName("ancientwarfare:structure/block_info");
    }

    @Override
    public boolean onRightClickClient(EntityPlayer player, ItemStack stack) {
        return true;
    }

    @Override
    public boolean cancelRightClick(EntityPlayer player, ItemStack stack) {
        return true;
    }

    @Override
    public void onRightClick(EntityPlayer player, ItemStack stack) {
        BlockPosition pos = BlockTools.getBlockClickedOn(player, player.worldObj, false);
        if (pos != null) {
            Block block = player.worldObj.getBlock(pos.x, pos.y, pos.z);
            int meta = player.worldObj.getBlockMetadata(pos.x, pos.y, pos.z);
            AWLog.logDebug("block: " + BlockDataManager.INSTANCE.getNameForBlock(block) + " meta: " + meta);
        }
    }

    @Override
    public boolean onLeftClickClient(EntityPlayer player, ItemStack stack) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean cancelLeftClick(EntityPlayer player, ItemStack stack) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onLeftClick(EntityPlayer player, ItemStack stack) {
        // TODO Auto-generated method stub

    }


}
