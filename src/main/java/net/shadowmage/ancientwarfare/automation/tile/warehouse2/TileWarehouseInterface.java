package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseInterface;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.tile.IBlockBreakHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.NBTSerializableUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TileWarehouseInterface extends TileControlled implements IInteractableTile, IBlockBreakHandler {
	private final ItemStackHandler inventory = new ItemStackHandler(27) {
		@Override
		protected void onContentsChanged(int slot) {
			markDirty();
		}
	};

	private boolean init = false;
	private final List<InterfaceFillRequest> fillRequests = new ArrayList<>();
	private final List<InterfaceEmptyRequest> emptyRequests = new ArrayList<>();
	List<WarehouseInterfaceFilter> filters = new ArrayList<>();
	List<ContainerWarehouseInterface> viewers = new ArrayList<>();

	public void addViewer(ContainerWarehouseInterface viewer) {
		if (!hasWorld() || world.isRemote) {
			return;
		}
		viewers.add(viewer);
	}

	public void removeViewer(ContainerWarehouseInterface viewer) {
		viewers.remove(viewer);
	}

	public void updateViewers() {
		for (ContainerWarehouseInterface v : viewers) {
			v.onInterfaceFiltersChanged();
		}
	}

	public List<WarehouseInterfaceFilter> getFilters() {
		return filters;
	}

	public void setFilters(List<WarehouseInterfaceFilter> filters) {
		this.filters.clear();
		this.filters.addAll(filters);
		recalcRequests();
		updateViewers();
		markDirty();
	}

	@Override
	protected void updateTile() {
		if (world.isRemote) {
			return;
		}
		if (!init) {
			init = true;
			recalcRequests();
		}
	}

	@Override
	public boolean onBlockClicked(EntityPlayer player, @Nullable EnumHand hand) {
		if (!player.world.isRemote) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WAREHOUSE_OUTPUT, pos);
		}
		return true;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.deserializeNBT(tag.getCompoundTag("inventory"));
		filters = NBTSerializableUtils.read(tag, "filterList", WarehouseInterfaceFilter.class);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("inventory", inventory.serializeNBT());
		NBTSerializableUtils.write(tag, "filterList", getFilters());
		return tag;
	}

	public void recalcRequests() {
		if (world.isRemote) {
			return;
		}
		fillRequests.clear();
		emptyRequests.clear();
		@Nonnull ItemStack stack;
		for (int i = 0; i < inventory.getSlots(); i++) {
			stack = inventory.getStackInSlot(i);
			if (stack.isEmpty()) {
				continue;
			}
			if (!matchesFilter(stack)) {
				emptyRequests.add(new InterfaceEmptyRequest(i, stack.getCount()));
			} else//matches, remove extras
			{
				int count = InventoryTools.getCountOf(inventory, stack);
				int max = getFilterQuantity(stack);
				if (count > max) {
					emptyRequests.add(new InterfaceEmptyRequest(i, count - max));
				}
			}
		}

		int count;
		for (WarehouseInterfaceFilter filter : filters) {
			if (filter.getFilterItem().isEmpty()) {
				continue;
			}
			count = InventoryTools.getCountOf(inventory, filter.getFilterItem());
			if (count < filter.getFilterQuantity()) {
				fillRequests.add(new InterfaceFillRequest(filter.getFilterItem().copy(), filter.getFilterQuantity() - count));
			}
		}
		TileWarehouseBase twb = (TileWarehouseBase) getController();
		if (twb != null) {
			twb.onIterfaceInventoryChanged(this);
		}
	}

	protected boolean matchesFilter(ItemStack stack) {
		if (filters.isEmpty()) {
			return false;
		}
		for (WarehouseInterfaceFilter filter : filters) {
			if (filter.apply(stack)) {
				return true;
			}
		}
		return false;
	}

	protected int getFilterQuantity(ItemStack stack) {
		int qty = 0;
		for (WarehouseInterfaceFilter filter : filters) {
			if (filter.apply(stack)) {
				qty += filter.getFilterQuantity();
			}
		}
		return qty;
	}

	public List<InterfaceFillRequest> getFillRequests() {
		return fillRequests;
	}

	public List<InterfaceEmptyRequest> getEmptyRequests() {
		return emptyRequests;
	}

	@Override
	public void onBlockBroken() {
		InventoryTools.dropItemsInWorld(world, inventory, pos);
	}

	public static class InterfaceFillRequest {
		final ItemStack requestedItem;
		final int requestAmount;

		public InterfaceFillRequest(ItemStack item, int amount) {
			requestedItem = item;
			requestAmount = amount;
		}
	}

	public static class InterfaceEmptyRequest {
		final int slotNum;
		final int count;

		public InterfaceEmptyRequest(int slot, int count) {
			slotNum = slot;
			this.count = count;
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) inventory;
		}
		return super.getCapability(capability, facing);
	}
}
