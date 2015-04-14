package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.*;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcFactionTradeSetup;
import net.shadowmage.ancientwarfare.npc.trade.FactionTrade;
import net.shadowmage.ancientwarfare.npc.trade.FactionTradeList;

import java.util.ArrayList;
import java.util.List;

public class GuiNpcFactionTradeSetup extends GuiContainerBase<ContainerNpcFactionTradeSetup> {

    CompositeScrolled area;
    FactionTradeList tradeList;

    public GuiNpcFactionTradeSetup(ContainerBase container) {
        super(container, 320, 240);
    }

    @Override
    public void initElements() {
        area = new CompositeScrolled(this, 0, 0, xSize, ySize - 16 - 4 - 4 * 18);
        addGuiElement(area);
    }

    @Override
    public void setupElements() {
        area.clearElements();
        tradeList = getContainer().tradeList;
        if (tradeList == null) {
            return;
        }

        List<FactionTrade> trades = new ArrayList<FactionTrade>();
        tradeList.getTrades(trades);

        int totalHeight = 8;
        for (int i = 0; i < trades.size(); i++) {
            totalHeight = addTrade(trades.get(i), totalHeight, i);
        }

        Button newTradeButton = new Button(8, totalHeight, xSize - 8 - 16, 12, "guistrings.new_trade") {
            @Override
            protected void onPressed() {
                tradeList.addNewTrade();
                getContainer().tradesChanged = true;
                refreshGui();
            }
        };
        area.addGuiElement(newTradeButton);
        totalHeight += 12;

        area.setAreaSize(totalHeight);
    }

    private int addTrade(final FactionTrade trade, int startHeight, final int tradeNum) {
        int gridX, gridY, slotX, slotY;
        gridX = 0;
        gridY = 0;
        for (int i = 0; i < 9; i++) {
            slotX = gridX * 18 + 8;
            slotY = gridY * 18 + startHeight;
            addTradeInputSlot(trade, slotX, slotY, i);
            slotX += 3 * 18 + 9;
            addTradeOutputSlot(trade, slotX, slotY, i);
            gridX++;
            if (gridX >= 3) {
                gridX = 0;
                gridY++;
            }
            if (gridY >= 3) {
                break;
            }
        }

        addTradeControls(trade, startHeight, tradeNum);

        startHeight += 18 * 3;//input/output grid size
        area.addGuiElement(new Line(0, startHeight + 1, xSize, startHeight + 1, 1, 0x000000ff));
        startHeight += 5;//separator line and padding
        return startHeight;
    }

    private void addTradeControls(final FactionTrade trade, int startHeight, final int tradeNum) {
        startHeight -= 1;//offset by 1 to lineup better with the item slot boxes, as they align to the inner slot rather than border
        int infoX = 6 * 18 + 8 + 9 + 4;
        Button upButton = new Button(infoX, startHeight, 55, 12, "guistrings.up") {
            @Override
            protected void onPressed() {
                tradeList.decrementTradePosition(tradeNum);
                refreshGui();
                getContainer().tradesChanged = true;
            }
        };
        area.addGuiElement(upButton);

        Button downButton = new Button(infoX, startHeight + 3 * 18 - 12, 55, 12, "guistrings.down") {
            @Override
            protected void onPressed() {
                tradeList.incrementTradePosition(tradeNum);
                refreshGui();
                getContainer().tradesChanged = true;
            }
        };
        area.addGuiElement(downButton);

        Button delete = new Button(infoX, startHeight + 21, 55, 12, "guistrings.delete") {
            @Override
            protected void onPressed() {
                tradeList.deleteTrade(tradeNum);
                refreshGui();
                getContainer().tradesChanged = true;
            }
        };
        area.addGuiElement(delete);

        area.addGuiElement(new Label(8 + 3 * 18 + 1, startHeight + 20, "="));

        area.addGuiElement(new Label(infoX + 55 + 4, startHeight + 1, "guistrings.max_trades"));

        area.addGuiElement(new Label(infoX + 55 + 4, startHeight + 18 + 1, "guistrings.refill_frequency"));

        NumberInput tradeInput = new NumberInput(infoX + 55 + 4 + 60, startHeight, 40, trade.getMaxAvailable(), this) {
            @Override
            public void onValueUpdated(float value) {
                trade.setMaxAvailable((int) value);
                getContainer().tradesChanged = true;
            }
        };
        tradeInput.setIntegerValue();
        area.addGuiElement(tradeInput);

        NumberInput refillInput = new NumberInput(infoX + 55 + 4 + 60, startHeight + 18, 40, trade.getRefillFrequency(), this) {
            @Override
            public void onValueUpdated(float value) {
                trade.setRefillFrequency((int) value);
                getContainer().tradesChanged = true;
            }
        };
        refillInput.setIntegerValue();
        refillInput.setAllowNegative();
        area.addGuiElement(refillInput);
    }

    private void addTradeInputSlot(final FactionTrade trade, int x, int y, final int slotNum) {
        ItemStack stack = trade.getInput()[slotNum];
        stack = stack == null ? null : stack.copy();
        final ItemSlot slot = new ItemSlot(x, y, stack, this) {
            @Override
            public void onSlotClicked(ItemStack stack) {
                stack = stack == null ? stack : stack.copy();
                setItem(stack);
                trade.getInput()[slotNum] = stack;
                getContainer().tradesChanged = true;
            }
        };
        area.addGuiElement(slot);
    }

    private void addTradeOutputSlot(final FactionTrade trade, int x, int y, final int slotNum) {
        ItemStack stack = trade.getOutput()[slotNum];
        stack = stack == null ? null : stack.copy();
        final ItemSlot slot = new ItemSlot(x, y, stack, this) {
            @Override
            public void onSlotClicked(ItemStack stack) {
                stack = stack == null ? stack : stack.copy();
                setItem(stack);
                trade.getOutput()[slotNum] = stack;
                getContainer().tradesChanged = true;
            }
        };
        area.addGuiElement(slot);
    }

    @Override
    protected boolean onGuiCloseRequested() {
        getContainer().onGuiClosed();
        return super.onGuiCloseRequested();
    }

}
