package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

import java.util.Set;

public class ItemConstructionToolLakes extends Item implements IItemClickable {

    public ItemConstructionToolLakes(String itemName) {
        this.setUnlocalizedName(itemName);
        this.setCreativeTab(AWStructuresItemLoader.structureTab);
        this.setTextureName("ancientwarfare:structure/" + "construction_tool");
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
        BlockPosition pos = BlockTools.getBlockClickedOn(player, player.worldObj, true);
        if (pos == null) {
            return;
        }
        Block block = player.worldObj.getBlock(pos.x, pos.y, pos.z);
        if (block != Blocks.air) {
            return;
        }
        FloodFillPathfinder pf = new FloodFillPathfinder(player.worldObj, pos.x, pos.y, pos.z, block, 0, false, true);
        Set<BlockPosition> blocks = pf.doFloodFill();
        for (BlockPosition p : blocks) {
            player.worldObj.setBlock(p.x, p.y, p.z, Blocks.flowing_water);
        }
        if (!player.capabilities.isCreativeMode) {
            if (stack.stackSize == 1) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            } else {
                stack.stackSize--;
            }
        }
    }

    @Override
    public boolean onLeftClickClient(EntityPlayer player, ItemStack stack) {
        return false;
    }

    @Override
    public boolean cancelLeftClick(EntityPlayer player, ItemStack stack) {
        return false;
    }

    @Override
    public void onLeftClick(EntityPlayer player, ItemStack stack) {
    }

}
