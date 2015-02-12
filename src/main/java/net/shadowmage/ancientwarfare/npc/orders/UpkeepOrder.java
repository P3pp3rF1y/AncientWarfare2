package net.shadowmage.ancientwarfare.npc.orders;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;

public class UpkeepOrder extends NpcOrders {

    BlockPosition upkeepPosition;
    int upkeepDimension;
    int blockSide;
    Block block;
    int blockMeta;
    int upkeepAmount = 6000;

    public UpkeepOrder() {

    }

    public void changeBlockSide() {
        blockSide++;
        if (blockSide > 5) {
            blockSide = 0;
        }
    }

    public void removeUpkeepPoint() {
        upkeepPosition = null;
        block = null;
        blockMeta = 0;
        blockSide = 0;
        upkeepDimension = 0;
        upkeepAmount = 6000;
    }

    public void setUpkeepAmount(int amt) {
        this.upkeepAmount = amt;
    }

    public void setBlockSide(int side) {
        this.blockSide = side;
    }

    public int getUpkeepBlockSide() {
        return blockSide;
    }

    public int getUpkeepDimension() {
        return upkeepDimension;
    }

    public BlockPosition getUpkeepPosition() {
        return upkeepPosition;
    }

    public Block getBlock() {
        return block;
    }

    public int getBlockMeta() {
        return blockMeta;
    }

    public final int getUpkeepAmount() {
        return upkeepAmount;
    }

    public boolean addUpkeepPosition(World world, BlockPosition pos) {
        upkeepPosition = pos;
        upkeepDimension = world.provider.dimensionId;
        blockSide = 0;
        block = world.getBlock(pos.x, pos.y, pos.z);
        blockMeta = world.getBlockMetadata(pos.x, pos.y, pos.z);
        upkeepAmount = 6000;
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if (tag.hasKey("upkeepPosition")) {
            upkeepPosition = new BlockPosition(tag.getCompoundTag("upkeepPosition"));
            upkeepDimension = tag.getInteger("dim");
            blockSide = tag.getInteger("side");
            block = Block.getBlockFromName(tag.getString("block"));
            blockMeta = tag.getInteger("blockMeta");
            upkeepAmount = tag.getInteger("upkeepAmount");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        if (upkeepPosition != null) {
            tag.setTag("upkeepPosition", upkeepPosition.writeToNBT(new NBTTagCompound()));
            tag.setInteger("dim", upkeepDimension);
            tag.setInteger("side", blockSide);
            tag.setString("block", Block.blockRegistry.getNameForObject(block));
            tag.setInteger("blockMeta", blockMeta);
            tag.setInteger("upkeepAmount", upkeepAmount);
        }
        return tag;
    }

    @Override
    public String toString() {
        return "Upkeep Orders[" + upkeepPosition + "]";
    }

    public static UpkeepOrder getUpkeepOrder(ItemStack stack) {
        if (stack != null && stack.getItem() == AWNpcItemLoader.upkeepOrder) {
            UpkeepOrder order = new UpkeepOrder();
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("orders")) {
                order.readFromNBT(stack.getTagCompound().getCompoundTag("orders"));
            }
            return order;
        }
        return null;
    }

    public static void writeUpkeepOrder(ItemStack stack, UpkeepOrder order) {
        if (stack != null && stack.getItem() == AWNpcItemLoader.upkeepOrder) {
            stack.setTagInfo("orders", order.writeToNBT(new NBTTagCompound()));
        }
    }

}
