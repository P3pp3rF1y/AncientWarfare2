package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.util.ArrayList;
import java.util.List;

public class TileWarehouse extends TileWarehouseBase {

    public TileWarehouse() {

    }

    @Override
    public void handleSlotClick(EntityPlayer player, ItemStack filter, boolean shiftClick) {
        if (filter == null) {
            tryAddItem(player, player.inventory.getItemStack());
        }else if (player.inventory.getItemStack() == null) {
            tryGetItem(player, filter, shiftClick);
        }
    }

    private void tryAddItem(EntityPlayer player, ItemStack cursorStack) {
        if(cursorStack==null){
            return;
        }
        List<IWarehouseStorageTile> destinations = new ArrayList<IWarehouseStorageTile>();
        storageMap.getDestinations(cursorStack, destinations);
        int stackSize = cursorStack.stackSize;
        int moved;
        for (IWarehouseStorageTile tile : destinations) {
            moved = tile.insertItem(cursorStack, cursorStack.stackSize);
            cursorStack.stackSize -= moved;
            changeCachedQuantity(cursorStack, moved);
            updateViewers();
            if (cursorStack.stackSize <= 0) {
                break;
            }
        }
        if (cursorStack.stackSize <= 0) {
            player.inventory.setItemStack(null);
        }
        if (stackSize != cursorStack.stackSize) {
            ((EntityPlayerMP)player).updateHeldItem();
        }
    }

    private void tryGetItem(EntityPlayer player, ItemStack filter, boolean shiftClick) {
        List<IWarehouseStorageTile> destinations = new ArrayList<IWarehouseStorageTile>();
        ItemStack newCursorStack = filter.copy();
        newCursorStack.stackSize = 0;
        storageMap.getDestinations(filter, destinations);
        int count;
        int toMove;
        for (IWarehouseStorageTile tile : destinations) {
            count = tile.getQuantityStored(filter);
            toMove = newCursorStack.getMaxStackSize() - newCursorStack.stackSize;
            toMove = toMove > count ? count : toMove;
            if (toMove > 0) {
                newCursorStack.stackSize += toMove;
                tile.extractItem(filter, toMove);
                changeCachedQuantity(filter, -toMove);
                updateViewers();
            }
            if (newCursorStack.stackSize >= newCursorStack.getMaxStackSize()) {
                break;
            }
        }
        InventoryTools.updateCursorItem((EntityPlayerMP)player, newCursorStack, shiftClick);
    }

}
