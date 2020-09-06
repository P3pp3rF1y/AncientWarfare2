package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseStockLinker;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStockLinker.WarehouseStockFilter;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.core.interfaces.ITooltipRenderer;

import javax.annotation.Nonnull;

public class GuiWarehouseStockLinker extends GuiContainerBase<ContainerWarehouseStockLinker> {

	private CompositeScrolled area;

	public GuiWarehouseStockLinker(ContainerBase par1Container) {
		super(par1Container, 250, 172);
	}

	@Override
	public void initElements() {
		area = new CompositeScrolled(this, 0, 0, 250, 80);//240-8-8-4-4*18
		addGuiElement(area);
	}

	@Override
	public void setupElements() {
		area.clearElements();
		int totalHeight = 8;

		ItemSlot slot;
		Button button;

		Label label;
		String text;

		for (WarehouseStockFilter filter : getContainer().filters) {
			//Item Slot
			slot = new FilterItemSlot(8, totalHeight, filter, this);
			area.addGuiElement(slot);

			//Item Name Label
			text = filter.getFilterItem().isEmpty() ? "Empty Filter" : filter.getFilterItem().getDisplayName();
			label = new Label(8 + 20, totalHeight + 4, text);
			area.addGuiElement(label);

			//Filter Remove Button
			button = new FilterRemoveButton(xSize - 20, totalHeight + 4, filter);
			area.addGuiElement(button);

			//Quantity Number Label
			text = String.valueOf(filter.getQuantity());
			label = new Label(xSize - 120 - fontRenderer.getStringWidth(text), totalHeight + 4, String.valueOf(filter.getQuantity()));
			area.addGuiElement(label);

			//Equality Sign Button
			Button equalitySign = new Button(xSize - 80, totalHeight + 4, 10, 12, filter.equalitySignType.getTranslationKey()) {
				@Override
				protected void onPressed(int mButton) {
					filter.changeEqualitySign(mButton == 1);
					getContainer().sendFiltersToServer();
					refreshGui();
				}
			};
			area.addGuiElement(equalitySign);

			//Compare Value Number Input
			NumberInput compareValue = new NumberInput(xSize - 70, totalHeight + 4, 50, filter.compareValue, this) {
				@Override
				public void onValueUpdated(float value) {
					filter.setCompareValue((int) value);
					getContainer().sendFiltersToServer();
					refreshGui();
				}
			};
			compareValue.setIntegerValue();
			area.addGuiElement(compareValue);
			compareValue.setValue(filter.compareValue);
			totalHeight += 16;
		}

		if (getContainer().filters.size() < 4) {
			button = new Button(8, totalHeight + 4, 178 - 16 - 8, 12, "guistrings.automation.new_filter") {
				@Override
				protected void onPressed() {
					getContainer().filters.add(new WarehouseStockFilter(ItemStack.EMPTY, 0,0,0));
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

		private FilterRemoveButton(int topLeftX, int topLeftY, WarehouseStockFilter filter) {
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

		private FilterItemSlot(int topLeftX, int topLeftY, WarehouseStockFilter filter, ITooltipRenderer render) {
			super(topLeftX, topLeftY, filter.getFilterItem(), render);
			this.filter = filter;
			this.renderItemQuantity = false;
		}

		@Override
		public void onSlotClicked(ItemStack stack, boolean rightClicked) {
			ItemStack in = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
			this.setItem(in);
			if (!in.isEmpty()) {
				in.setCount(1);
			}
			filter.setItem(in.isEmpty() ? ItemStack.EMPTY : in.copy());
			getContainer().sendFiltersToServer();
		}
	}
	@Override
	protected boolean onGuiCloseRequested() {
		return super.onGuiCloseRequested();
	}
}
