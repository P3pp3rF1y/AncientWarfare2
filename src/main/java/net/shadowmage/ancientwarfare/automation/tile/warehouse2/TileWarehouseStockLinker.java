package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.shadowmage.ancientwarfare.automation.block.BlockWarehouseStockLinker;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseStockLinker;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.owner.IOwnable;
import net.shadowmage.ancientwarfare.core.owner.Owner;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.NBTHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TileWarehouseStockLinker extends TileControlled implements IOwnable, IInteractableTile {

	private static final String FILTER_LIST_TAG = "filterList";
	private final List<WarehouseStockFilter> filters = new ArrayList<>();
	private Owner owner = Owner.EMPTY;
	private BlockPos warehouseBlockPos;

	private final Set<ContainerWarehouseStockLinker> viewers = new HashSet<>();
	private int searchCooldown = 0;
	private int blockUpdateCooldown = 0;
	private boolean currentEquality;

	private void updateViewers() {
		for (ContainerWarehouseStockLinker viewer : viewers) {
			viewer.onFiltersChanged();
		}
	}

	public void addViewer(ContainerWarehouseStockLinker viewer) {
		viewers.add(viewer);
	}

	public void removeViewer(ContainerWarehouseStockLinker viewer) {
		viewers.remove(viewer);
	}

	public void setWarehousePos(BlockPos pos) {
		this.warehouseBlockPos = pos;
	}

	public List<WarehouseStockFilter> getFilters() {
		return filters;
	}

	public void setFilters(List<WarehouseStockFilter> filters) {
		this.filters.clear();
		this.filters.addAll(filters);
		BlockTools.notifyBlockUpdate(this);
		recountFilters();
		BlockTools.notifyNeighbors(this);
	}

	public boolean getEqualityHandle() {
		if (!filters.isEmpty()) {
			for (WarehouseStockFilter stockFilter : filters) {
				if (handleEqualitySign(stockFilter, stockFilter.getCompareValue(), stockFilter.getQuantity())) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * should be called whenever controller tile is set or warehouse inventory updated
	 */
	private void recountFilters() {
		Optional<TileWarehouseBase> controller = getController();
		if (!controller.isPresent()) {
			for (WarehouseStockFilter filter : this.filters) {
				filter.setQuantity(0);
				filter.setEqualitySignType((byte) 0);
				filter.setCompareValue(0);
			}
		} else {
			if (controller.get().isActive()) {
				for (WarehouseStockFilter filter : this.filters) {
					filter.setQuantity(filter.getFilterItem().isEmpty() ? 0 : controller.get().getCountOf(filter.getFilterItem()));
					filter.setEqualitySignType((byte) filter.getEqualitySignType().ordinal());
					filter.setCompareValue(filter.getCompareValue());
				}
			}
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void searchForController() {
		linkToWarehouse();
	}

	private void linkToWarehouse() {
		if (warehouseBlockPos != null && world.isBlockLoaded(warehouseBlockPos)) {
			TileEntity te = world.getTileEntity(warehouseBlockPos);
			if (te instanceof TileWarehouseBase && isValidController((IControllerTile) te)) {
				TileWarehouseBase warehouse = (TileWarehouseBase) te;
				warehouse.addControlledTile(this);
				setController(warehouse);
			}
		}
	}

	@Override
	public boolean isValidController(IControllerTile tile) {
		return (tile instanceof TileWarehouseBase);
	}

	@Override
	protected void onControllerChanged() {
		onWarehouseInventoryUpdated();
	}

	@Override
	public boolean isOwner(EntityPlayer player) {
		return owner.isOwnerOrSameTeamOrFriend(player);
	}

	@Override
	public void setOwner(EntityPlayer player) {
		owner = new Owner(player);
	}

	@Override
	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	@Override
	public Owner getOwner() {
		return owner;
	}

	@Override
	public boolean onBlockClicked(EntityPlayer player, @Nullable EnumHand hand) {
		if (!player.world.isRemote && isOwner(player)) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WAREHOUSE_STOCK_LINKER, pos);
		}
		return true;
	}

	@Override
	protected void updateTile() {
		if (searchCooldown > 0) {
			searchCooldown--;
		}
		if (!getController().isPresent() && canSearchForWarehouseAgain()) {
			linkToWarehouse();
			searchCooldown = 40;
		}
		if (blockUpdateCooldown > 0){
			blockUpdateCooldown--;
		}
		if (canDoBlockUpdateAgain()){
			BlockWarehouseStockLinker.setActiveState(getEqualityHandle(), world, pos);
			blockUpdateCooldown = 60;
			currentEquality = getEqualityHandle();
		}
	}

	private boolean canSearchForWarehouseAgain() {
		return searchCooldown <= 0;
	}

	private boolean canDoBlockUpdateAgain() {
		return blockUpdateCooldown <= 0 && currentEquality != getEqualityHandle();
	}

	/*
	 * should be called on SERVER whenever warehouse inventory changes
	 */
	void onWarehouseInventoryUpdated() {
		if (!world.isRemote) {
			recountFilters();
			BlockTools.notifyNeighbors(this);
			BlockTools.notifyBlockUpdate(this);
		}
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		NBTHelper.writeSerializablesTo(tag, FILTER_LIST_TAG, filters);
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		filters.clear();
		filters.addAll(NBTHelper.deserializeListFrom(tag, TileWarehouseStockLinker.FILTER_LIST_TAG, WarehouseStockFilter::new));
		BlockTools.notifyBlockUpdate(this);
		updateViewers();
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		filters.addAll(NBTHelper.deserializeListFrom(tag, TileWarehouseStockLinker.FILTER_LIST_TAG, WarehouseStockFilter::new));
		owner = Owner.deserializeFromNBT(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTHelper.writeSerializablesTo(tag, FILTER_LIST_TAG, filters);
		owner.serializeToNBT(tag);
		return tag;
	}

	public static class WarehouseStockFilter implements INBTSerializable<NBTTagCompound> {
		private static final String ITEM_TAG = "item";
		private static final String QUANTITY_TAG = "quantity";
		private static final String EQUALITY_TAG = "equality_sign";
		private static final String COMPARE_VALUE_TAG = "compare_value";
		private ItemStack item = ItemStack.EMPTY;
		public EqualitySignType equalitySignType = EqualitySignType.EQUAL_TO;
		public int compareValue;
		public int quantity;

		public WarehouseStockFilter() {}

		public WarehouseStockFilter(ItemStack item, int equalitySign, int qty, int compareValue) {
			setItem(item);
			setEqualitySignType((byte) equalitySign);
			setQuantity(qty);
			setCompareValue(compareValue);
		}

		public void setItem(ItemStack item) {
			this.item = item;
		}

		private void setEqualitySignType(byte equalitySignType) {
			this.equalitySignType = EqualitySignType.values()[equalitySignType];
		}

		private void setQuantity(int quantity) {
			this.quantity = quantity;
		}

		public void setCompareValue(int compareValue) {
			this.compareValue = compareValue;
		}

		public ItemStack getFilterItem() {
			return item;
		}

		private EqualitySignType getEqualitySignType() {
			return equalitySignType;
		}

		private int getCompareValue() {
			return compareValue;
		}

		public int getQuantity() {
			return quantity;
		}

		public void changeEqualitySign(boolean isRmb) {
			equalitySignType = isRmb ? equalitySignType.previous() : equalitySignType.next();
		}

		private boolean equalToTarget(int compareValue, int quantity) {
			if (compareValue != 0) {
				return quantity == compareValue;
			} else {
				return false;
			}
		}

		private boolean greaterThanTarget(int compareValue, int quantity) {
			if (compareValue != 0) {
				return quantity > compareValue;
			} else {
				return false;
			}
		}

		private boolean lessThanTarget(int compareValue, int quantity) {
			if (compareValue != 0) {
				return quantity < compareValue;
			} else {
				return false;
			}
		}

		private boolean greaterThanOrEqualToTarget(int compareValue, int quantity) {
			if (compareValue != 0) {
				return quantity >= compareValue;
			} else {
				return false;
			}
		}

		private boolean lessThanOrEqualToTarget(int compareValue, int quantity) {
			if (compareValue != 0) {
				return quantity <= compareValue;
			} else {
				return false;
			}
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound tag = new NBTTagCompound();
			if (!item.isEmpty()) {
				tag.setTag(ITEM_TAG, item.writeToNBT(new NBTTagCompound()));
			}
			tag.setByte(EQUALITY_TAG, (byte) equalitySignType.ordinal());
			tag.setInteger(QUANTITY_TAG, quantity);
			tag.setInteger(COMPARE_VALUE_TAG, compareValue);
			return tag;
		}

		@Override
		public void deserializeNBT(NBTTagCompound tag) {
			setItem(tag.hasKey(ITEM_TAG) ? new ItemStack(tag.getCompoundTag(ITEM_TAG)) : ItemStack.EMPTY);
			setEqualitySignType(tag.getByte(EQUALITY_TAG));
			setQuantity(tag.getInteger(QUANTITY_TAG));
			setCompareValue(tag.getInteger(COMPARE_VALUE_TAG));
		}
	}

	public enum EqualitySignType {
		EQUAL_TO("equality.equal"),

		GREATER_THAN("equality.greaterthan"),

		LESS_THAN("equality.lessthan"),

		GREATER_THAN_OR_EQUAL_TO("equality.greaterthanorequalto"),

		LESS_THAN_OR_EQUAL_TO("equality.lessthanorequalto");

		final String key;

		EqualitySignType(String key) {
			this.key = key;
		}

		public String getTranslationKey() {
			return key;
		}

		public EqualitySignType next() {
			int ordinal = ordinal() + 1;
			if (ordinal >= EqualitySignType.values().length) {
				ordinal = 0;
			}
			return EqualitySignType.values()[ordinal];
		}

		public EqualitySignType previous() {
			int ordinal = ordinal() - 1;
			if (ordinal < 0) {
				ordinal = EqualitySignType.values().length - 1;
			}
			return EqualitySignType.values()[ordinal];
		}
	}

	private boolean handleEqualitySign(WarehouseStockFilter w, int compareValue, int quantity) {
		switch (w.equalitySignType) {
			case EQUAL_TO:
				return w.equalToTarget(compareValue, quantity);

			case GREATER_THAN:
				return w.greaterThanTarget(compareValue, quantity);

			case LESS_THAN:
				return w.lessThanTarget(compareValue, quantity);

			case GREATER_THAN_OR_EQUAL_TO:
				return w.greaterThanOrEqualToTarget(compareValue, quantity);

			case LESS_THAN_OR_EQUAL_TO:
				return w.lessThanOrEqualToTarget(compareValue, quantity);

			default:
				return false;
		}
	}
}
