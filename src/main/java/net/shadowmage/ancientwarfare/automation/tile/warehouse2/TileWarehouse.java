package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TileWarehouse extends TileWarehouseBase {

    public TileWarehouse() {

    }

    @Override
    public void handleSlotClick(EntityPlayer player, ItemStack filter, boolean shiftClick) {
        if (filter.isEmpty()) {
            tryAddItem(player, player.inventory.getItemStack());
        }else if (player.inventory.getItemStack().isEmpty()) {
            tryGetItem(player, filter, shiftClick);
        }
    }

    private void tryAddItem(EntityPlayer player, ItemStack cursorStack) {
        if(cursorStack.isEmpty()){
            return;
        }
        List<IWarehouseStorageTile> destinations = new ArrayList<>();
        storageMap.getDestinations(cursorStack, destinations);
        int stackSize = cursorStack.getCount();
        int moved;
        for (IWarehouseStorageTile tile : destinations) {
            moved = tile.insertItem(cursorStack, cursorStack.getCount());
            ItemStack filter = cursorStack.copy();
            filter.setCount(1);
            changeCachedQuantity(filter, moved);
            updateViewers();
            cursorStack.shrink(moved);
            if (cursorStack.getCount() <= 0) {
                break;
            }
        }
        if (cursorStack.getCount() <= 0) {
            player.inventory.setItemStack(ItemStack.EMPTY);
        }
        if (stackSize != cursorStack.getCount()) {
            ((EntityPlayerMP)player).updateHeldItem();
        }
    }

    private void tryGetItem(EntityPlayer player, ItemStack filter, boolean shiftClick) {
        List<IWarehouseStorageTile> destinations = new ArrayList<>();
        @Nonnull ItemStack newCursorStack = filter.copy();
        newCursorStack.setCount(0);
        storageMap.getDestinations(filter, destinations);
        int count;
        int toMove;
        for (IWarehouseStorageTile tile : destinations) {
            count = tile.getQuantityStored(filter);
            toMove = newCursorStack.getMaxStackSize() - newCursorStack.getCount();
            toMove = toMove > count ? count : toMove;
            if (toMove > 0) {
                newCursorStack.grow(toMove);
                tile.extractItem(filter, toMove);
                changeCachedQuantity(filter, -toMove);
                updateViewers();
            }
            if (newCursorStack.getCount() >= newCursorStack.getMaxStackSize()) {
                break;
            }
        }
        InventoryTools.updateCursorItem((EntityPlayerMP)player, newCursorStack, shiftClick);
    }

}
