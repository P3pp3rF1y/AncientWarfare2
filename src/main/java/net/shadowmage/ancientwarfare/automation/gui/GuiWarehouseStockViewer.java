package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseStockViewer;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStockViewer.WarehouseStockFilter;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.interfaces.ITooltipRenderer;

import javax.annotation.Nonnull;

public class GuiWarehouseStockViewer extends GuiContainerBase<ContainerWarehouseStockViewer> {

	private CompositeScrolled area;

	public GuiWarehouseStockViewer(ContainerBase par1Container) {
		super(par1Container, 178, 172);
	}

	@Override
	public void initElements() {
		area = new CompositeScrolled(this, 0, 0, 178, 80);//240-8-8-4-4*18
		addGuiElement(area);
	}

	@Override
	public void setupElements() {
		area.clearElements();
		getContainer().filters.clear();
		getContainer().filters.addAll(getContainer().tileEntity.getFilters());
		int totalHeight = 8;

		ItemSlot slot;
		Button button;

		Label label;
		String text;

		for (WarehouseStockFilter filter : getContainer().filters) {
			slot = new FilterItemSlot(8, totalHeight, filter, this);
			area.addGuiElement(slot);

			button = new FilterRemoveButton(178 - 16 - 12, totalHeight + 3, filter);
			area.addGuiElement(button);

			text = String.valueOf(filter.getQuantity());
			label = new Label(178 - 16 - 12 - 2 - fontRenderer.getStringWidth(text), totalHeight + 4, String.valueOf(filter.getQuantity()));
			area.addGuiElement(label);

			text = filter.getFilterItem().isEmpty() ? "Empty Filter" : filter.getFilterItem().getDisplayName();
			label = new Label(8 + 20, totalHeight + 4, text);
			area.addGuiElement(label);

			totalHeight += 16;
		}
		if (getContainer().filters.size() < 4) {
			button = new Button(8, totalHeight + 4, 178 - 16 - 8, 12, "guistrings.automation.new_filter") {
				@Override
				protected void onPressed() {
					getContainer().filters.add(new WarehouseStockFilter(ItemStack.EMPTY, 0));
					getContainer().sendFiltersToServer();
					refreshGui();
				}
			};

			area.addGuiElement(button);
			totalHeight += 16;
		}
		area.setAreaSize(totalHeight);
	}

	private class FilterRemoveButton extends Button {
		WarehouseStockFilter filter;

		public FilterRemoveButton(int topLeftX, int topLeftY, WarehouseStockFilter filter) {
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
		WarehouseStockFilter filter;

		public FilterItemSlot(int topLeftX, int topLeftY, WarehouseStockFilter filter, ITooltipRenderer render) {
			super(topLeftX, topLeftY, filter.getFilterItem(), render);
			this.filter = filter;
			this.renderItemQuantity = false;
		}

		@Override
		public void onSlotClicked(ItemStack stack, boolean rightClicked) {
			@Nonnull ItemStack in = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
			this.setItem(in);
			if (!in.isEmpty()) {
				in.setCount(1);
			}
			filter.setItem(in.isEmpty() ? ItemStack.EMPTY : in.copy());
			getContainer().sendFiltersToServer();
		}
	}
}
