package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

/*
 * Created by Olivier on 23/03/2015.
 */
public abstract class POTradeTransferEntry {
    private TransferType type = getDefaultType();
    @Nonnull
    private ItemStack filter = ItemStack.EMPTY;

    protected abstract TransferType getDefaultType();

    public abstract void toggleType();

    protected abstract TransferType getTypeFrom(int type);

    public final ItemStack getFilter() {
        return filter;
    }

    public final void setFilter(ItemStack stack) {
        filter = stack;
    }

    public final void setType(TransferType type) {
        this.type = type == null ? getDefaultType() : type;
    }

    public final TransferType getType() {
        return type;
    }

    public final void process(IInventory storage, IInventory move, EnumFacing side) {
        if (!filter.isEmpty())
            type.doTransfer(storage, move, side, filter);
    }

    public final void readFromNBT(NBTTagCompound tag) {
        if (tag.hasKey("item")) {
            filter = new ItemStack(tag.getCompoundTag("item"));
        }
        type = getTypeFrom(tag.getInteger("type"));
    }


    public final NBTTagCompound writeToNBT(NBTTagCompound tag) {
        if (!filter.isEmpty()) {
            tag.setTag("item", filter.writeToNBT(new NBTTagCompound()));
        }
        tag.setInteger("type", type.ordinal());
        return tag;
    }

    public interface TransferType {

        void doTransfer(IInventory storage, IInventory move, EnumFacing side, ItemStack filter);

        int ordinal();
    }
}
