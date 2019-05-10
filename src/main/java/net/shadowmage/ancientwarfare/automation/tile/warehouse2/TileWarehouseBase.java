package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseControl;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseCraftingStation;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseInterface.InterfaceEmptyRequest;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseInterface.InterfaceFillRequest;
import net.shadowmage.ancientwarfare.automation.tile.worksite.IWorksiteAction;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileWorksiteBounded;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class TileWarehouseBase extends TileWorksiteBounded implements IControllerTile {

	private boolean init;
	private boolean shouldRecount;

	private final Set<TileWarehouseStockViewer> stockViewers = new HashSet<>();
	private final Set<TileWarehouseInterface> interfaceTiles = new HashSet<>();
	private final Set<IWarehouseStorageTile> storageTiles = new HashSet<>();

	/*
	 * Interfaces that need filling, AND there are items available to fill.
	 * Anytime storage block inventories are updated, this list needs to be rechecked
	 * to make sure items are still available
	 */
	private final Set<TileWarehouseInterface> interfacesToFill = new HashSet<>();

	/*
	 * Interfaces that have an excess of items or non-matching items will be in this set
	 */
	private final Set<TileWarehouseInterface> interfacesToEmpty = new HashSet<>();

	protected WarehouseStorageMap storageMap = new WarehouseStorageMap();
	protected ItemQuantityMap cachedItemMap = new ItemQuantityMap();

	private final Set<ContainerWarehouseControl> viewers = new HashSet<>();
	private final Set<ContainerWarehouseCraftingStation> craftingViewers = new HashSet<>();

	public TileWarehouseBase() {

	}

	/*
	 * SERVER ONLY
	 *
	 * @return max allowed storage by item quantity
	 */
	public int getMaxStorage() {
		int max = 0;
		for (IWarehouseStorageTile t : storageTiles) {
			max += t.getStorageAdditionSize();
		}
		return max;
	}

	@Override
	public void onBoundsAdjusted() {
		BlockPos max = getWorkBoundsMax();
		setWorkBoundsMax(max.up(getWorkBoundsMin().getY() + getBoundsMaxHeight() - max.getY()));
		this.interfacesToEmpty.clear();
		this.interfacesToFill.clear();
		for (TileWarehouseInterface i : interfaceTiles) {
			i.setController(null);
		}
		this.interfaceTiles.clear();
		for (TileWarehouseStockViewer i : stockViewers) {
			i.setController(null);
		}
		this.stockViewers.clear();
		for (IWarehouseStorageTile i : storageTiles) {
			if (i instanceof IControlledTile)
				((IControlledTile) i).setController(null);
		}
		this.storageTiles.clear();

		storageMap = new WarehouseStorageMap();
		cachedItemMap.clear();

		scanForInitialTiles();

		WarehouseDebugger.reinitializeItemCounts(world, pos);
	}

	@Override
	public boolean userAdjustableBlocks() {
		return false;
	}

	@Override
	public int getBoundsMaxHeight() {
		return 3;
	}

	public abstract void handleSlotClick(EntityPlayer player, ItemStack filter, boolean shiftClick, boolean rightClick);

	public void changeCachedQuantity(ItemStack filter, int change) {
		if (change > 0) {
			cachedItemMap.addCount(filter, change);
		} else {
			cachedItemMap.decreaseCount(filter, -change);
		}
		updateViewers();

		WarehouseDebugger.changeItemQuantity(world, pos, filter, change);
	}

	private boolean tryEmptyInterfaces() {
		List<TileWarehouseInterface> toEmpty = new ArrayList<>(interfacesToEmpty);
		for (TileWarehouseInterface tile : toEmpty) {
			if (tryEmptyTile(tile)) {
				tile.recalcRequests();
				return true;
			}
		}
		return false;
	}

	private boolean tryEmptyTile(TileWarehouseInterface tile) {
		List<InterfaceEmptyRequest> reqs = tile.getEmptyRequests();
		for (InterfaceEmptyRequest req : reqs) {
			if (tryRemoveFromRequest(tile, req)) {
				return true;
			}
		}
		return false;
	}

	private boolean tryRemoveFromRequest(TileWarehouseInterface tile, InterfaceEmptyRequest request) {
		IItemHandler inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		ItemStack stack = inventory.getStackInSlot(request.slotNum);
		if (stack.isEmpty()) {
			return false;
		}
		int moved;
		int toMove = request.count;
		int stackMove;
		List<IWarehouseStorageTile> potentialStorage = storageMap.getDestinations(stack);
		for (IWarehouseStorageTile dest : potentialStorage) {
			stackMove = toMove > stack.getCount() ? stack.getCount() : toMove;
			moved = dest.insertItem(stack, stackMove);
			if (moved > 0) {
				ItemStack filter = stack.copy();
				filter.setCount(1);
				changeCachedQuantity(filter, moved);
			}

			toMove -= moved;
			if (!inventory.extractItem(request.slotNum, moved, false).isEmpty()) {
				return true;
			}
			if (toMove <= 0) {
				break;
			}
		}
		return false;
	}

	private boolean tryFillInterfaces() {
		List<TileWarehouseInterface> toFill = new ArrayList<>(interfacesToFill);
		for (TileWarehouseInterface tile : toFill) {
			if (tryFillTile(tile)) {
				tile.recalcRequests();
				return true;
			}
		}
		return false;
	}

	private boolean tryFillTile(TileWarehouseInterface tile) {
		List<InterfaceFillRequest> reqs = tile.getFillRequests();
		for (InterfaceFillRequest req : reqs) {
			if (tryFillFromRequest(tile, req)) {
				return true;
			}
		}
		return false;
	}

	private boolean tryFillFromRequest(TileWarehouseInterface tile, InterfaceFillRequest request) {
		List<IWarehouseStorageTile> potentialStorage = storageMap.getDestinations();
		IItemHandler inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		int remainingToTake = request.requestAmount;
		for (IWarehouseStorageTile source : potentialStorage) {
			int found = source.getQuantityStored(request.requestedItem);
			if (found > 0) {
				ItemStack stack = request.requestedItem.copy();
				int countToTake = Math.min(Math.min(stack.getMaxStackSize(), found), remainingToTake);

				stack.setCount(countToTake);
				stack = InventoryTools.mergeItemStack(inventory, stack);
				if (stack.isEmpty() || stack.getCount() != countToTake) {
					int moved = stack.isEmpty() ? countToTake : countToTake - stack.getCount();
					source.extractItem(request.requestedItem, moved);
					cachedItemMap.decreaseCount(request.requestedItem, moved);
					updateViewers();
					remainingToTake -= moved;
					if (remainingToTake <= 0) {
						return true;
					}
				}
			}
		}
		return remainingToTake != request.requestAmount;
	}

	public final void getItems(ItemQuantityMap map) {
		map.addAll(cachedItemMap);
	}

	public final void clearItemCache() {
		cachedItemMap.clear();
	}

	public final void addItemsToCache(ItemQuantityMap map) {
		cachedItemMap.addAll(map);
	}

	private static final IWorksiteAction EMPTY_ACTION = e -> WorksiteImplementation.getEnergyPerActivation(e) / 4D;
	private static final IWorksiteAction FILL_ACTION = e -> WorksiteImplementation.getEnergyPerActivation(e) / 4D;

	@Override
	protected Optional<IWorksiteAction> getNextAction() {
		if (!interfacesToEmpty.isEmpty()) {
			return Optional.of(EMPTY_ACTION);
		} else if (!interfacesToFill.isEmpty()) {
			return Optional.of(FILL_ACTION);
		}
		return Optional.empty();
	}

	@Override
	protected boolean processAction(IWorksiteAction action) {
		if (action == EMPTY_ACTION) {
			return tryEmptyInterfaces();
		} else if (action == FILL_ACTION) {
			return tryFillInterfaces();
		}
		return false;
	}

	@Override
	protected final void updateWorksite() {
		if (!init) {
			scanForInitialTiles();
		}
		if (shouldRecount) {
			shouldRecount = false;
			recountInventory();
		}
	}

	private void scanForInitialTiles() {
		BlockPos max = getWorkBoundsMax();
		BlockPos min = getWorkBoundsMin();
		List<TileEntity> tiles = WorldTools.getTileEntitiesInArea(world, min.getX() - 1, min.getY(), min.getZ() - 1, max.getX() + 1, max.getY(), max.getZ() + 1);
		for (TileEntity te : tiles) {
			if (te instanceof IControlledTile && !((IControlledTile) te).getController().isPresent() && ((IControlledTile) te).isValidController(this)) {
				addControlledTile((IControlledTile) te);
			}
		}
		init = true;
	}

	private void recountInventory() {
		cachedItemMap.clear();
		for (IWarehouseStorageTile tile : storageTiles) {
			tile.addItems(cachedItemMap);
		}
	}

	public final void addViewer(ContainerWarehouseControl viewer) {
		if (!hasWorld() || world.isRemote) {
			return;
		}
		viewers.add(viewer);
	}

	public final void addCraftingViewer(ContainerWarehouseCraftingStation viewer) {
		if (!hasWorld() || world.isRemote) {
			return;
		}
		craftingViewers.add(viewer);
	}

	public final void removeViewer(ContainerWarehouseControl viewer) {
		viewers.remove(viewer);
	}

	public final void removeCraftingViewer(ContainerWarehouseCraftingStation viewer) {
		craftingViewers.remove(viewer);
	}

	public final void updateViewers() {
		for (ContainerWarehouseControl viewer : viewers) {
			viewer.onWarehouseInventoryUpdated();
		}
		for (TileWarehouseStockViewer viewer : stockViewers) {
			viewer.onWarehouseInventoryUpdated();
		}
		for (ContainerWarehouseCraftingStation viewer : craftingViewers) {
			viewer.onWarehouseInventoryUpdated();
		}
	}

	private void addStorageTile(IWarehouseStorageTile tile) {
		if (world.isRemote) {
			return;
		}
		if (!storageTiles.contains(tile)) {
			if (tile instanceof IControlledTile) {
				((IControlledTile) tile).setController(this);
			}
			storageTiles.add(tile);
			storageMap.addStorageTile(tile);
			tile.addItems(cachedItemMap);
		}
	}

	private void removeStorageTile(IWarehouseStorageTile tile) {
		ItemQuantityMap iqm = new ItemQuantityMap();
		tile.addItems(iqm);
		this.cachedItemMap.removeAll(iqm);
		storageTiles.remove(tile);
		storageMap.removeStorageTile(tile);
		updateViewers();
	}

	private void addInterfaceTile(TileWarehouseInterface tile) {
		if (world.isRemote) {
			return;
		}
		if (!interfaceTiles.contains(tile)) {
			interfaceTiles.add(tile);
			tile.setController(this);
			if (!tile.getEmptyRequests().isEmpty()) {
				interfacesToEmpty.add(tile);
			}
			if (!tile.getFillRequests().isEmpty()) {
				interfacesToFill.add(tile);
			}
		}
	}

	private void removeInterfaceTile(TileWarehouseInterface tile) {
		interfaceTiles.remove(tile);
		interfacesToFill.remove(tile);
		interfacesToEmpty.remove(tile);
	}

	public final void onIterfaceInventoryChanged(TileWarehouseInterface tile) {
		if (world.isRemote) {
			return;
		}
		interfacesToFill.remove(tile);
		interfacesToEmpty.remove(tile);
		if (!tile.getEmptyRequests().isEmpty()) {
			interfacesToEmpty.add(tile);
		}
		if (!tile.getFillRequests().isEmpty()) {
			interfacesToFill.add(tile);
		}
	}

	public final void onStorageFilterChanged(IWarehouseStorageTile tile, List<WarehouseStorageFilter> oldFilters, List<WarehouseStorageFilter> newFilters) {
		if (world.isRemote) {
			return;
		}
		storageMap.updateTileFilters(tile, oldFilters, newFilters);
	}

	private void addStockViewer(TileWarehouseStockViewer viewer) {
		if (world.isRemote) {
			return;
		}
		stockViewers.add(viewer);
		viewer.setController(this);
		viewer.onWarehouseInventoryUpdated();
	}

	private void removeStockViewer(TileWarehouseStockViewer tile) {
		stockViewers.remove(tile);
	}

	@Override
	public final void addControlledTile(IControlledTile tile) {
		if (tile instanceof IWarehouseStorageTile) {
			addStorageTile((IWarehouseStorageTile) tile);
		} else if (tile instanceof TileWarehouseInterface) {
			addInterfaceTile((TileWarehouseInterface) tile);
		} else if (tile instanceof TileWarehouseStockViewer) {
			addStockViewer((TileWarehouseStockViewer) tile);
		}
	}

	@Override
	public final void removeControlledTile(IControlledTile tile) {
		if (tile instanceof IWarehouseStorageTile) {
			removeStorageTile((IWarehouseStorageTile) tile);
		} else if (tile instanceof TileWarehouseInterface) {
			removeInterfaceTile((TileWarehouseInterface) tile);
		} else if (tile instanceof TileWarehouseStockViewer) {
			removeStockViewer((TileWarehouseStockViewer) tile);
		}
	}

	@Override
	public BlockPos getPosisition() {
		return getPos();
	}

	@Override
	public final WorkType getWorkType() {
		return WorkType.CRAFTING;
	}

	@Override
	public final boolean onBlockClicked(EntityPlayer player, @Nullable EnumHand hand) {
		if (!player.world.isRemote) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WAREHOUSE_CONTROL, pos);
		}
		return true;
	}

	@Override
	public final boolean shouldRenderInPass(int pass) {
		return pass == 1;//it is for the TESR (for bounds rendering), block renders as per normal
	}

	public int getCountOf(ItemStack layoutStack) {
		return cachedItemMap.getCount(layoutStack);
	}

	public void decreaseCountOf(ItemStack layoutStack, int i) {
		if (world.isRemote) {
			cachedItemMap.decreaseCount(layoutStack, i);
			return;
		}
		List<IWarehouseStorageTile> dest = storageMap.getDestinations();
		int found = 0;
		for (IWarehouseStorageTile tile : dest) {
			found = tile.getQuantityStored(layoutStack);
			if (found > 0) {
				if (found > i) {
					found = i;
				}
				i -= found;
				tile.extractItem(layoutStack, found);
				cachedItemMap.decreaseCount(layoutStack, found);
				if (i <= 0) {
					break;
				}
			}
		}
		updateViewers();
	}

	public ItemStack tryAdd(ItemStack stack) {
		List<IWarehouseStorageTile> destinations = storageMap.getDestinations(stack);
		for (IWarehouseStorageTile tile : destinations) {
			int moved = tile.insertItem(stack, stack.getCount());
			ItemStack filter = stack.copy();
			filter.setCount(1);
			changeCachedQuantity(filter, moved);
			stack.shrink(moved);
			if (stack.getCount() <= 0) {
				break;
			}
		}
		if (stack.getCount() <= 0) {
			return ItemStack.EMPTY;
		}
		return stack;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("min")) {
			setWorkBoundsMin(BlockPos.fromLong(tag.getLong("min")));
		}
		if (tag.hasKey("max")) {
			setWorkBoundsMax(BlockPos.fromLong(tag.getLong("max")));
		}
	}

	/*
	 * Used by user-set-blocks tile to set all default harvest-checks to true when bounds are FIRST set
	 */
	@Override
	protected void onBoundsSet() {
		setWorkBoundsMax(getWorkBoundsMax().up(getWorkBoundsMin().getY() + getBoundsMaxHeight() - getWorkBoundsMax().getY()));
		BlockTools.notifyBlockUpdate(this);
	}
}
