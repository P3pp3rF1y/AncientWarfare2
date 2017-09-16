package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.shadowmage.ancientwarfare.automation.gamedata.MailboxData;
import net.shadowmage.ancientwarfare.automation.gamedata.MailboxData.DeliverableItem;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.InventorySided;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.render.BlockRenderProperties;
import net.shadowmage.ancientwarfare.core.tile.TileOwned;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TileMailbox extends TileOwned implements ISidedInventory, IRotatableTile, ITickable {

    private boolean autoExport;//TODO : should automatically try and export from output side
    private boolean privateBox;

    public InventorySided inventory;

    private String mailboxName;
    private String destinationName;

    public TileMailbox() {
        inventory = new InventorySided(this, RotationType.FOUR_WAY, 36);
        InventoryTools.IndexHelper helper = new InventoryTools.IndexHelper();
        int[] topIndices = helper.getIndiceArrayForSpread(inventory.getSizeInventory()/2);
        int[] bottomIndices = helper.getIndiceArrayForSpread(inventory.getSizeInventory()/2);
        inventory.setAccessibleSideDefault(RelativeSide.TOP, RelativeSide.TOP, topIndices);
        inventory.setAccessibleSideDefault(RelativeSide.BOTTOM, RelativeSide.BOTTOM, bottomIndices);
    }

    @Override
    public void update() {
        if (!hasWorld() || world.isRemote) {
            return;
        }
        if (mailboxName != null)//try to receive mail
        {
            MailboxData data = AWGameData.INSTANCE.getData(world, MailboxData.class);

            List<DeliverableItem> items = new ArrayList<>();
            data.getDeliverableItems(privateBox ? getOwnerName() : null, mailboxName, items, world, pos.getX(), pos.getY(), pos.getZ());
            data.addMailboxReceiver(privateBox ? getOwnerName() : null, mailboxName, this);

            if (destinationName != null)//try to send mail
            {
                trySendItems(data);
            }
        }
    }

    private void trySendItems(MailboxData data) {
        @Nonnull ItemStack item;
        String owner = privateBox ? getOwnerName() : null;
        int dim = world.provider.getDimension();
        for (int k = inventory.getSizeInventory()/2; k < inventory.getSizeInventory(); k++) {
            item = inventory.getStackInSlot(k);
            if (!item.isEmpty()) {
                data.addDeliverableItem(owner, destinationName, item, dim, pos);
                inventory.setInventorySlotContents(k, ItemStack.EMPTY);
                break;
            }
        }
    }

    public String getMailboxName() {
        return mailboxName;
    }

    public String getTargetName() {
        return destinationName;
    }

    public void setMailboxName(String name) {
        if (world.isRemote) {
            return;
        }
        mailboxName = name;
        markDirty();
    }

    public void setTargetName(String name) {
        if (world.isRemote) {
            return;
        }
        destinationName = name;
        markDirty();
    }

    public boolean isAutoExport() {
        return autoExport;
    }

    public boolean isPrivateBox() {
        return privateBox;
    }

    public void setAutoExport(boolean val) {
        autoExport = val;
    }

    public void setPrivateBox(boolean val) {
        if (world.isRemote) {
            return;
        }
        if (val != privateBox) {
            mailboxName = null;
            destinationName = null;
            markDirty();
        }
        privateBox = val;
    }
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("targetName")) {
            destinationName = tag.getString("targetName");
        }
        if (tag.hasKey("mailboxName")) {
            mailboxName = tag.getString("mailboxName");
        }
        if (tag.hasKey("inventory")) {
            inventory.deserializeNBT(tag.getCompoundTag("inventory"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (destinationName != null) {
            tag.setString("targetName", destinationName);
        }
        if (mailboxName != null) {
            tag.setString("mailboxName", mailboxName);
        }
        tag.setTag("inventory", inventory.serializeNBT());
        return tag;
    }

    @Override
    public int getSizeInventory() {
        return inventory.getSizeInventory();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amt) {
        return inventory.decrStackSize(slot, amt);
    }

    @Override
    public ItemStack removeStackFromSlot(int slot) {
        return inventory.removeStackFromSlot(slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        inventory.setInventorySlotContents(slot, stack);
    }

    @Override
    public String getName() {
        return inventory.getName();
    }

    @Override
    public boolean hasCustomName() {
        return inventory.hasCustomName();
    }

    @Override
    public int getInventoryStackLimit() {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer p_70300_1_) {
        return inventory.isUsableByPlayer(p_70300_1_);
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
        return inventory.isItemValidForSlot(p_94041_1_, p_94041_2_);
    }

    @Override
    public int getField(int id) {
        return inventory.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        inventory.setField(id, value);
    }

    @Override
    public int getFieldCount() {
        return inventory.getFieldCount();
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    @Override
    public EnumFacing getPrimaryFacing() {
        return EnumFacing.VALUES[getBlockMetadata()];
    }

    @Override
    public void setPrimaryFacing(EnumFacing face) {
        world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockRenderProperties.FACING, face), 0);
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return inventory.getSlotsForFace(side);
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return inventory.canInsertItem(index, itemStackIn, direction);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return inventory.canExtractItem(index, stack, direction);
    }
}
