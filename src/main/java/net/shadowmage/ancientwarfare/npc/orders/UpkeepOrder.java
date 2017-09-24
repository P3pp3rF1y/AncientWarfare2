package net.shadowmage.ancientwarfare.npc.orders;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.shadowmage.ancientwarfare.npc.block.BlockTownHall;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.item.ItemUpkeepOrder;

public class UpkeepOrder implements INBTSerializable<NBTTagCompound> {

    private BlockPos upkeepPosition;
    private int upkeepDimension;
    private EnumFacing blockSide;
    private int upkeepAmount = 6000;

    public UpkeepOrder() {

    }

    public void changeBlockSide() {
        blockSide = EnumFacing.VALUES[(blockSide.ordinal() + 1) % EnumFacing.VALUES.length];
    }

    public void removeUpkeepPoint() {
        upkeepPosition = null;
        blockSide = EnumFacing.DOWN;
        upkeepDimension = 0;
        upkeepAmount = 6000;
    }

    public void setUpkeepAmount(int amt) {
        this.upkeepAmount = amt;
    }

    public void setBlockSide(EnumFacing side) {
        this.blockSide = side;
    }

    public EnumFacing getUpkeepBlockSide() {
        return blockSide;
    }

    public int getUpkeepDimension() {
        return upkeepDimension;
    }

    public BlockPos getUpkeepPosition() {
        return upkeepPosition;
    }

    public final int getUpkeepAmount() {
        return upkeepAmount;
    }

    public boolean addUpkeepPosition(World world, BlockPos pos) {
        if(pos != null && world.getTileEntity(pos) instanceof IInventory) {
            if (!AWNPCStatics.npcAllowUpkeepAnyInventory && (!(world.getBlockState(pos).getBlock() instanceof BlockTownHall)))
                return false;
            upkeepPosition = pos;
            upkeepDimension = world.provider.getDimension();
            blockSide = EnumFacing.DOWN;
            upkeepAmount = 6000;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Upkeep Orders[" + upkeepPosition + "]";
    }

    public static UpkeepOrder getUpkeepOrder(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof ItemUpkeepOrder) {
            UpkeepOrder order = new UpkeepOrder();
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("orders")) {
                order.deserializeNBT(stack.getTagCompound().getCompoundTag("orders"));
            }
            return order;
        }
        return null;
    }

    public void write(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof ItemUpkeepOrder) {
            stack.setTagInfo("orders", serializeNBT());
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        if (upkeepPosition != null) {
            tag.setLong("upkeepPosition", upkeepPosition.toLong());
            tag.setInteger("dim", upkeepDimension);
            tag.setByte("side", (byte) blockSide.ordinal());
            tag.setInteger("upkeepAmount", upkeepAmount);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        if (tag.hasKey("upkeepPosition")) {
            upkeepPosition = BlockPos.fromLong(tag.getLong("upkeepPosition"));
            upkeepDimension = tag.getInteger("dim");
            blockSide = EnumFacing.VALUES[tag.getByte("side")];
            upkeepAmount = tag.getInteger("upkeepAmount");
        }
    }
}
