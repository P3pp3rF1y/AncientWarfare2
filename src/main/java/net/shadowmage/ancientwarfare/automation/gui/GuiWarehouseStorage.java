package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseStorage;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.WarehouseStorageFilter;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeItemSlots;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.interfaces.ITooltipRenderer;
import net.shadowmage.ancientwarfare.core.inventory.ItemHashEntry;

import javax.annotation.Nonnull;
import java.util.List;

public class GuiWarehouseStorage extends GuiContainerBase<ContainerWarehouseStorage> {

	private CompositeScrolled area;
	private CompositeItemSlots area2;

	public GuiWarehouseStorage(ContainerBase par1Container) {
		super(par1Container, 178, 240);
	}

	@Override
	public void initElements() {
		area = new CompositeScrolled(this, 0, 0, xSize, 74);
		addGuiElement(area);

		Listener l = new Listener(Listener.MOUSE_DOWN) {
			@Override
			public boolean onEvent(GuiElement widget, ActivationEvent evt) {
				if (evt.mButton == 0 && widget.isMouseOverElement(evt.mx, evt.my) && !area2.isMouseOverSubElement(evt.mx, evt.my)) {
					getContainer().handleClientRequestSpecific(ItemStack.EMPTY, isShiftKeyDown(), false);
				}
				return true;
			}
		};
		area2 = new CompositeItemSlots(this, 0, 74, xSize, 74, this);
		area2.addNewListener(l);
		addGuiElement(area2);
	}

	@Override
	public void setupElements() {
		area.clearElements();
		area2.clearElements();
		addInventoryViewElements();
		List<WarehouseStorageFilter> filters = getContainer().filters;

		int totalHeight = 8;

		ItemSlot slot;
		Label label;
		Button button;
		String name;

		for (WarehouseStorageFilter filter : filters) {
			slot = new FilterItemSlot(8, totalHeight, filter, this);
			area.addGuiElement(slot);

			name = filter.getFilterItem().isEmpty() ? "" : filter.getFilterItem().getDisplayName();

			label = new Label(20 + 8, totalHeight + 4, name);
			area.addGuiElement(label);

			button = new FilterRemoveButton(xSize - 16 - 12, totalHeight + 3, filter);
			area.addGuiElement(button);

			totalHeight += 18;
		}

		if (filters.size() < 10) {
			button = new Button(8, totalHeight, 95, 12, "guistrings.automation.new_filter") {
				@Override
				protected void onPressed() {
					WarehouseStorageFilter filter = new WarehouseStorageFilter(ItemStack.EMPTY);
					getContainer().filters.add(filter);
					getContainer().sendFiltersToServer();
					refreshGui();
				}
			};
			area.addGuiElement(button);
			totalHeight += 12;
		}
		area.setAreaSize(totalHeight);
	}

	private void addInventoryViewElements() {
		ItemSlot slot;
		int qty;
		@Nonnull ItemStack stack;
		int x = 0;
		int y = 0;
		NonNullList<ItemStack> displayStacks = NonNullList.create();
		for (ItemHashEntry entry : getContainer().itemMap.keySet()) {
			qty = getContainer().itemMap.getCount(entry);
			stack = entry.getItemStack();
			stack.setCount(qty);
			displayStacks.add(stack);
		}

		int totalSize = 22;
		for (ItemStack displayStack : displayStacks) {
			if (x >= 9) {
				x = 0;
				y++;
				totalSize += 18;
			}
			slot = new ItemSlot(4 + x * 18, 4 + y * 18, displayStack, this) {
				@Override
				public void onSlotClicked(ItemStack stack, boolean rightClicked) {
					@Nonnull ItemStack reqStack = getStack();
					if (!(rightClicked && isShiftKeyDown()) && !reqStack.isEmpty() && (reqStack.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(stack, reqStack)))
						reqStack = ItemStack.EMPTY;
					getContainer().handleClientRequestSpecific(reqStack, isShiftKeyDown(), rightClicked);
				}
			};
			area2.addGuiElement(slot);
			x++;
		}
		area2.setAreaSize(totalSize);
	}

	private class FilterRemoveButton extends Button {
		private final WarehouseStorageFilter filter;

		private FilterRemoveButton(int topLeftX, int topLeftY, WarehouseStorageFilter filter) {
			super(topLeftX, topLeftY, 12, 12, "-");
			this.filter = filter;
		}

		@Override
		protected void onPressed() {
			getContainer().filters.remove(filter);
			getContainer().sendFiltersToServer();
			refreshGui();
		}
	}

	private class FilterItemSlot extends ItemSlot {
		private final WarehouseStorageFilter filter;

		private FilterItemSlot(int topLeftX, int topLeftY, WarehouseStorageFilter filter, ITooltipRenderer render) {
			super(topLeftX, topLeftY, filter.getFilterItem(), render);
			this.filter = filter;
		}

		@Override
		public void onSlotClicked(ItemStack stack, boolean rightClicked) {
			@Nonnull ItemStack in = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
			this.setItem(in);
			if (!in.isEmpty()) {
				in.setCount(1);
			}
			filter.setFilterItem(in.isEmpty() ? ItemStack.EMPTY : in.copy());
			getContainer().sendFiltersToServer();
		}
	}

}
