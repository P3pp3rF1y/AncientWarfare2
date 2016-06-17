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
    public void handleSlotClick(EntityPlayer player, ItemStack filter, boolean shiftClick, boolean rightClick) {
        if (filter == null) {
            tryAddItem(player, player.inventory.getItemStack());
        } else {
            tryGetItem(player, filter, shiftClick, rightClick);
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

    private void tryGetItem(EntityPlayer player, ItemStack filter, boolean shiftClick, boolean rightClick) {
        List<IWarehouseStorageTile> destinations = new ArrayList<IWarehouseStorageTile>();
        ItemStack newCursorStack; 
        
        if (player.inventory.getItemStack() != null) {
            newCursorStack = player.inventory.getItemStack().copy();
            if (!(newCursorStack.isItemEqual(filter) && ItemStack.areItemStackTagsEqual(newCursorStack, filter)))
                return;
        } else {
            newCursorStack = filter.copy();
            newCursorStack.stackSize = 0;
        }
        
        storageMap.getDestinations(filter, destinations);
        int count;
        int toMove;
        int toMoveMax = newCursorStack.getMaxStackSize();
        if (rightClick && (toMoveMax > 1)) {
            if (shiftClick) {
                toMoveMax = Math.min(newCursorStack.stackSize + 1, toMoveMax);
            } else {
                int available = 0;
                for (IWarehouseStorageTile tile : destinations) {
                    available += tile.getQuantityStored(filter);
                }
                if (toMoveMax > available) {
                    toMoveMax = available;
                }
                toMoveMax = (int) Math.ceil(toMoveMax / 2.0);
            }
        }
        for (IWarehouseStorageTile tile : destinations) {
            count = tile.getQuantityStored(filter);
            toMove = toMoveMax - newCursorStack.stackSize;
            toMove = toMove > count ? count : toMove;
            if (toMove > 0) {
                newCursorStack.stackSize += toMove;
                tile.extractItem(filter, toMove);
                changeCachedQuantity(filter, -toMove);
                updateViewers();
            }
            if (newCursorStack.stackSize >= toMoveMax) {
                break;
            }
        }
        InventoryTools.updateCursorItem((EntityPlayerMP)player, newCursorStack, !rightClick && shiftClick);
    }

}
