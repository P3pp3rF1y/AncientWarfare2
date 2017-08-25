package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.item.ItemResearchBook;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nonnull;

public class TileWarehouseCraftingStation extends TileEntity implements IInteractableTile, IInventoryChangedListener {

    public InventoryCrafting layoutMatrix;
    public InventoryCraftResult result;
    public InventoryBasic bookInventory;

    ItemStack[] matrixShadow;

    public TileWarehouseCraftingStation() {
        Container c = new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer var1) {
                return true;
            }

            @Override
            public void onCraftMatrixChanged(IInventory par1iInventory) {
                onInventoryChanged(null);
            }
        };

        layoutMatrix = new InventoryCrafting(c, 3, 3);
        matrixShadow = new ItemStack[layoutMatrix.getSizeInventory()];
        result = new InventoryCraftResult();
        bookInventory = new InventoryBasic(1, this);
    }

    /**
     * called to shadow a copy of the input matrix, to know what to refill
     */
    public void preItemCrafted() {
        @Nonnull ItemStack stack;
        for (int i = 0; i < layoutMatrix.getSizeInventory(); i++) {
            stack = layoutMatrix.getStackInSlot(i);
            matrixShadow[i] = stack.isEmpty() ? null : stack.copy();
        }
    }

    public void onItemCrafted() {
        TileWarehouseBase warehouse = getWarehouse();
        if (warehouse == null) {
            return;
        }
        AWLog.logDebug("crafting item...");
        int q;
        @Nonnull ItemStack layoutStack;
        for (int i = 0; i < layoutMatrix.getSizeInventory(); i++) {
            layoutStack = matrixShadow[i];
            if (layoutStack == null) {
                continue;
            }
            if (!layoutMatrix.getStackInSlot(i).isEmpty()) {
                continue;
            }
            q = warehouse.getCountOf(layoutStack);
            AWLog.logDebug("warehouse count of: " + layoutStack + " :: " + q);
            if (q > 0) {
                warehouse.decreaseCountOf(layoutStack, 1);
                layoutStack = layoutStack.copy();
                layoutStack.setCount(1);
                layoutMatrix.setInventorySlotContents(i, layoutStack);
            }
        }
        if (!world.isRemote) {
            warehouse.updateViewers();
        }
    }

    public final TileWarehouseBase getWarehouse() {
        if (pos.getY() <= 1)//could not possibly be a warehouse below...
        {
            return null;
        }
        TileEntity te = world.getTileEntity(pos.down());
        if (te instanceof TileWarehouseBase) {
            return (TileWarehouseBase) te;
        }
        return null;
    }

    private void onLayoutMatrixChanged() {
        this.result.setInventorySlotContents(0, AWCraftingManager.INSTANCE.findMatchingRecipe(layoutMatrix, world, getCrafterName()));
    }

    public String getCrafterName() {
        return ItemResearchBook.getResearcherName(bookInventory.getStackInSlot(0));
    }

    @Override
    public void setWorld(World world){
        super.setWorld(world);
        onLayoutMatrixChanged();
    }

    @Override
    public void onInventoryChanged(IInventory internal){
        onLayoutMatrixChanged();
        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        InventoryTools.readInventoryFromNBT(bookInventory, tag.getCompoundTag("bookInventory"));
        InventoryTools.readInventoryFromNBT(result, tag.getCompoundTag("resultInventory"));
        InventoryTools.readInventoryFromNBT(layoutMatrix, tag.getCompoundTag("layoutMatrix"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        NBTTagCompound inventoryTag = InventoryTools.writeInventoryToNBT(bookInventory, new NBTTagCompound());
        tag.setTag("bookInventory", inventoryTag);

        inventoryTag = InventoryTools.writeInventoryToNBT(result, new NBTTagCompound());
        tag.setTag("resultInventory", inventoryTag);

        inventoryTag = InventoryTools.writeInventoryToNBT(layoutMatrix, new NBTTagCompound());
        tag.setTag("layoutMatrix", inventoryTag);

        return tag;
    }

    @Override
    public boolean onBlockClicked(EntityPlayer player, EnumHand hand) {
        if (!player.world.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WAREHOUSE_CRAFTING, pos);
        }
        return true;
    }

}
