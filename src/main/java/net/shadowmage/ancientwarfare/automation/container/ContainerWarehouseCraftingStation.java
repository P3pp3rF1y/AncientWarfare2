package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseBase;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseCraftingStation;
import net.shadowmage.ancientwarfare.core.container.ContainerCraftingRecipeMemory;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;
import net.shadowmage.ancientwarfare.core.container.ICraftingContainer;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.crafting.ICraftingRecipe;
import net.shadowmage.ancientwarfare.core.inventory.ItemHashEntry;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nonnull;
import java.util.List;

import static net.minecraft.util.EnumActionResult.PASS;
import static net.minecraft.util.EnumActionResult.SUCCESS;

public class ContainerWarehouseCraftingStation extends ContainerTileBase<TileWarehouseCraftingStation> implements ICraftingContainer {
	private static final int BOOK_SLOT = 1;
	private static final String CHANGE_LIST_TAG = "changeList";
	private static final int CRAFTING_SLOT = 0;
	public ContainerCraftingRecipeMemory containerCrafting;

	private ItemQuantityMap itemMap = new ItemQuantityMap();
	private final ItemQuantityMap cache = new ItemQuantityMap();
	private boolean shouldUpdate = true;
	private int currentCraftTotalSize = 0;
	private IItemHandlerModifiable warehouseItemHandler = null;

	public ContainerWarehouseCraftingStation(final EntityPlayer player, int x, int y, int z) {
		super(player, x, y, z);

		containerCrafting = new ContainerCraftingRecipeMemory(tileEntity.craftingRecipeMemory, player) {
			@Override
			protected OnTakeResult handleOnTake(EntityPlayer player, ItemStack stack) {
				if (warehouseItemHandler == null) {
					return new OnTakeResult(PASS, stack);
				}
				ICraftingRecipe recipe = tileEntity.craftingRecipeMemory.getRecipe();
				NonNullList<ItemStack> reusableStacks = AWCraftingManager.getReusableStacks(recipe, tileEntity.craftingRecipeMemory.craftMatrix);
				CombinedInvWrapper combinedHandler = new CombinedInvWrapper(new ItemStackHandler(reusableStacks), warehouseItemHandler);
				NonNullList<ItemStack> resources = AWCraftingManager.getRecipeInventoryMatch(recipe,
						containerCrafting.getCraftingStacks(), s -> tileEntity.getWarehouse().getCountOf(s) >= s.getCount(), combinedHandler);
				if (!resources.isEmpty()) {
					resources = InventoryTools.removeItems(resources, reusableStacks);
					InventoryTools.removeItems(warehouseItemHandler, resources);

					NonNullList<ItemStack> remainingItems = InventoryTools.removeItems(tileEntity.craftingRecipeMemory.getRemainingItems(
							AWCraftingManager.fillCraftingMatrixFromInventory(resources)), reusableStacks);
					InventoryTools.insertOrDropItems(warehouseItemHandler, remainingItems, tileEntity.getWorld(), tileEntity.getPos());

					return new OnTakeResult(SUCCESS, stack);
				}
				return new OnTakeResult(PASS, stack);
			}

			@Override
			protected boolean canTakeStackFromOutput(EntityPlayer player) {
				return true;
			}
		};
		for (Slot slot : containerCrafting.getSlots()) {
			addSlotToContainer(slot);
		}

		int y1 = 8 + 3 * 18 + 8;
		addPlayerSlots(y1);
		TileWarehouseBase warehouse = tileEntity.getWarehouse();
		if (warehouse != null) {
			warehouse.addCraftingViewer(this);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setAll(List<ItemStack> stacks) {
		containerCrafting.setOpening(true);
		super.setAll(stacks);
		containerCrafting.setOpening(false);
	}

	@Override
	public void putStackInSlot(int slotID, ItemStack stack) {
		containerCrafting.setOpening(true);
		super.putStackInSlot(slotID, stack);
		containerCrafting.setOpening(false);
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		if (slotId == CRAFTING_SLOT) {
			warehouseItemHandler = tileEntity.getWarehouse() != null ? tileEntity.getWarehouse().getItemHandler() : null;
		}
		ItemStack result = super.slotClick(slotId, dragType, clickTypeIn, player);
		currentCraftTotalSize = 0;
		return result;
	}

	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer) {
		TileWarehouseBase warehouse = tileEntity.getWarehouse();
		if (warehouse != null) {
			warehouse.removeCraftingViewer(this);
		}
		super.onContainerClosed(par1EntityPlayer);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex) {
		if (slotClickedIndex == 0 && !updateAndCheckCraftStackOrLessInTotal()) {
			return ItemStack.EMPTY;
		}

		@Nonnull ItemStack slotStackCopy = ItemStack.EMPTY;
		Slot theSlot = this.getSlot(slotClickedIndex);
		if (theSlot.getHasStack()) {
			@Nonnull ItemStack slotStack = theSlot.getStack();
			slotStackCopy = slotStack.copy();
			int playerSlotStart = 2 + tileEntity.craftingRecipeMemory.craftMatrix.getSizeInventory();
			if (slotClickedIndex < playerSlotStart)//result slot, book slot
			{
				if (!this.mergeItemStack(slotStack, playerSlotStart, playerSlotStart + playerSlots, false))//merge into player inventory
				{
					return ItemStack.EMPTY;
				}
			} else if (slotClickedIndex < playerSlotStart + playerSlots && !mergeItemStack(slotStack, BOOK_SLOT, BOOK_SLOT + 1, false)) {
				return ItemStack.EMPTY;
			}
			if (slotStack.getCount() == 0) {
				theSlot.putStack(ItemStack.EMPTY);
			} else {
				theSlot.onSlotChanged();
			}
			if (slotStack.getCount() == slotStackCopy.getCount()) {
				return ItemStack.EMPTY;
			}
			theSlot.onTake(par1EntityPlayer, slotStack);
		}
		return slotStackCopy;
	}

	private boolean updateAndCheckCraftStackOrLessInTotal() {
		ItemStack craftedStack = getSlot(CRAFTING_SLOT).getStack();
		currentCraftTotalSize += craftedStack.getCount();
		return currentCraftTotalSize <= craftedStack.getMaxStackSize();
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey(CHANGE_LIST_TAG)) {
			handleChangeList(tag.getTagList(CHANGE_LIST_TAG, Constants.NBT.TAG_COMPOUND));
		} else if (tag.hasKey("recipe")) {
			containerCrafting.handleRecipeUpdate(tag);
		}
		refreshGui();
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (shouldUpdate) {
			synchItemMaps();
			shouldUpdate = false;
		}
	}

	private void handleChangeList(NBTTagList changeList) {
		NBTTagCompound tag;
		for (int i = 0; i < changeList.tagCount(); i++) {
			tag = changeList.getCompoundTagAt(i);
			itemMap.putEntryFromNBT(tag);
		}
		TileWarehouseBase warehouse = tileEntity.getWarehouse();
		if (warehouse != null) {
			warehouse.clearItemCache();
			warehouse.addItemsToCache(itemMap);
		}
	}

	private void synchItemMaps() {
		/*
		 * need to loop through this.itemMap and compare quantities to warehouse.itemMap
         *    add any changes to change-list
         * need to loop through warehouse.itemMap and find new entries
         *    add any new entries to change-list
         */

		cache.clear();
		TileWarehouseBase warehouse = tileEntity.getWarehouse();
		if (warehouse != null) {
			warehouse.getItems(cache);
		}
		ItemQuantityMap warehouseItemMap = cache;
		int qty;
		NBTTagList changeList = new NBTTagList();
		NBTTagCompound tag;
		for (ItemHashEntry wrap : this.itemMap.keySet()) {
			qty = this.itemMap.getCount(wrap);
			if (qty != warehouseItemMap.getCount(wrap)) {
				qty = warehouseItemMap.getCount(wrap);
				changeList.appendTag(warehouseItemMap.writeEntryToNBT(wrap));
				this.itemMap.put(wrap, qty);
			}
		}
		for (ItemHashEntry entry : warehouseItemMap.keySet()) {
			if (!itemMap.contains(entry)) {
				qty = warehouseItemMap.getCount(entry);
				changeList.appendTag(warehouseItemMap.writeEntryToNBT(entry));
				this.itemMap.put(entry, qty);
			}
		}
		if (changeList.tagCount() > 0) {
			tag = new NBTTagCompound();
			tag.setTag(CHANGE_LIST_TAG, changeList);
			sendDataToClient(tag);
		}
	}

	public void onWarehouseInventoryUpdated() {
		shouldUpdate = true;
	}

	@Override
	public ContainerCraftingRecipeMemory getCraftingMemoryContainer() {
		return containerCrafting;
	}

	@Override
	public IItemHandlerModifiable[] getInventories() {
		if (tileEntity.getWarehouse() == null) {
			return new IItemHandlerModifiable[] {new PlayerInvWrapper(player.inventory)};
		}

		return new IItemHandlerModifiable[] {tileEntity.getWarehouse().getItemHandler(), new PlayerInvWrapper(player.inventory)};
	}

	@Override
	public boolean pushCraftingMatrixToInventories() {
		IItemHandler craftMatrixWrapper = new InvWrapper(tileEntity.craftingRecipeMemory.craftMatrix);
		NonNullList<ItemStack> craftingItems = InventoryTools.getItems(craftMatrixWrapper);

		IItemHandler inventories;
		if (tileEntity.getWarehouse() == null) {
			inventories = new PlayerInvWrapper(player.inventory);
		} else {
			inventories = new CombinedInvWrapper(tileEntity.getWarehouse().getItemHandler(), new PlayerInvWrapper(player.inventory));
		}

		if (InventoryTools.insertItems(inventories, craftingItems, true).isEmpty()) {
			InventoryTools.insertItems(inventories, craftingItems, false);
			InventoryTools.removeItems(craftMatrixWrapper, craftingItems);
			return true;
		}

		return false;
	}
}
