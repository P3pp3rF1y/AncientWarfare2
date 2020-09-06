package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Line;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcFactionTradeView;
import net.shadowmage.ancientwarfare.npc.trade.FactionTrade;

import javax.annotation.Nonnull;

public class GuiNpcFactionTradeView extends GuiContainerBase<ContainerNpcFactionTradeView> {

	private Button inventoryButton;
	private Button setupButton;
	private CompositeScrolled area;

	public GuiNpcFactionTradeView(ContainerBase container) {
		super(container);
	}

	@Override
	public void initElements() {
		int areaYSize = player.capabilities.isCreativeMode ? ySize - 24 - 16 - 4 - 4 * 18 : ySize - 16 - 4 - 4 * 18;
		area = new CompositeScrolled(this, 0, player.capabilities.isCreativeMode ? 24 : 0, xSize, areaYSize);
		inventoryButton = new Button(8, 8, (256 - 16) / 2, 12, "guistrings.inventory") {
			@Override
			protected void onPressed() {
				NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_INVENTORY, getContainer().entity.getEntityId(), 0, 0);
			}
		};
		setupButton = new Button(8 + ((256 - 16) / 2), 8, (256 - 16) / 2, 12, "guistrings.trade_setup") {
			@Override
			protected void onPressed() {
				NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_FACTION_TRADE_SETUP, getContainer().entity.getEntityId(), 0, 0);
			}
		};
	}

	@Override
	public void setupElements() {
		clearElements();
		addGuiElement(area);
		if (player.capabilities.isCreativeMode) {
			addGuiElement(inventoryButton);
			addGuiElement(setupButton);
		}
		addTrades();
	}

	private void addTrades() {
		area.clearElements();

		int totalHeight = 8;
		if (getContainer().tradeList.isEmpty()) {
			area.addGuiElement(new Label(8, 8, "guistrings.trader.no_trade"));
		} else {
			for (int i = 0; i < getContainer().tradeList.size(); i++) {
				totalHeight = addTrade(getContainer().tradeList.get(i), i, totalHeight);
			}
		}

		area.setAreaSize(totalHeight);
	}

	private int addTrade(final FactionTrade trade, final int tradeNum, int startHeight) {
		int gridX = 0;
		int gridY = 0;
		for (int i = 0; i < trade.size(); i++) {
			int slotX = gridX * 18 + 8;
			int slotY = gridY * 18 + startHeight;
			addTradeInputSlot(trade, slotX, slotY, i);
			slotX += 3 * 18 + 9;
			addTradeOutputSlot(trade, slotX, slotY, i);
			gridX++;
			if (gridX >= 3) {
				gridX = 0;
				gridY++;
			}
		}
		int startWidth = 8 + 3 * 18;
		if (trade.size() < 3) {
			startWidth += (trade.size() - 3) * 18;
		}
		area.addGuiElement(new Label(startWidth + 1, startHeight + (gridY + 1) * 5, ">"));
		startWidth *= 2;
		startWidth += 9;

		if (trade.getCurrentAvailable() <= 0) {
			Label unavailable = new Label(startWidth, startHeight, I18n.format("guistrings.trade_unavailable"));
			area.addGuiElement(unavailable);
			long refillsIn = trade.getRefillTime() - getContainer().getWorld().getTotalWorldTime();
			if (refillsIn < 0) {
				refillsIn = 0;
			}
			long seconds = (refillsIn / 20) % 60;
			long minutes = (refillsIn / (60 * 20)) % 60;
			long hours = (refillsIn / (60 * 60 * 20)) % 24;
			long days = (refillsIn / (24 * 60 * 60 * 20));

			Label refillLabel = new Label(startWidth, startHeight + 12, I18n.format("guistrings.trade_refills_in"));
			area.addGuiElement(refillLabel);
			Label refillTime = new Label(startWidth, startHeight + 24, I18n.format("guistrings.trade_refill_time", days, hours, minutes, seconds));
			area.addGuiElement(refillTime);
		} else {
			Button tradeButton = new Button(startWidth, startHeight + 17, 70, 20, "guistrings.trade") {
				@Override
				protected void onPressed() {
					trade.performTrade(player, null);
					getContainer().doTrade(tradeNum);
					refreshGui();
				}
			};
			area.addGuiElement(tradeButton);

			Label available = new Label(startWidth, startHeight, I18n.format("guistrings.trades_available", trade.getCurrentAvailable()));
			area.addGuiElement(available);
		}

		startHeight += 18 * gridY;//input/output grid size
		area.addGuiElement(new Line(0, startHeight + 1, xSize, startHeight + 1, 1, 0x000000ff));
		startHeight += 5;//separator line and padding
		return startHeight;
	}

	private void addTradeInputSlot(final FactionTrade trade, int x, int y, final int slotNum) {
		ItemStack stack = trade.getInputStack(slotNum);
		stack = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
		final ItemSlot slot = new ItemSlot(x, y, stack, this);
		if (stack.isEmpty()) {
			slot.addTooltip("guistrings.npc.trade_input_slot");
		}
		area.addGuiElement(slot);
	}

	private void addTradeOutputSlot(final FactionTrade trade, int x, int y, final int slotNum) {
		ItemStack stack = trade.getOutputStack(slotNum);
		stack = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
		final ItemSlot slot = new ItemSlot(x, y, stack, this);
		if (stack.isEmpty()) {
			slot.addTooltip("guistrings.npc.trade_output_slot");
		}
		area.addGuiElement(slot);
	}

}
