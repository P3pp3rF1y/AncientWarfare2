package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.*;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcPlayerOwnedTrade;
import net.shadowmage.ancientwarfare.npc.trade.POTrade;
import net.shadowmage.ancientwarfare.npc.trade.Trade;

import java.util.List;

public class GuiNpcPlayerOwnedTrade extends GuiContainerBase<ContainerNpcPlayerOwnedTrade> {

    CompositeScrolled area;
    private boolean owner;

    public GuiNpcPlayerOwnedTrade(ContainerBase container) {
        super(container);
    }

    @Override
    public void initElements() {
        int areaSize = ySize - 8 - 4 - 8 - 4 * 18;
        int areaY = 0;
        if (player.getCommandSenderName().equals(getContainer().entity.getOwnerName())) {
            areaSize -= 12 + 8 + 4;
            areaY = 12 + 8 + 4;
            owner = true;
        }
        area = new CompositeScrolled(this, 0, areaY, xSize, areaSize);
    }

    @Override
    public void setupElements() {
        area.clearElements();
        clearElements();
        if (owner) {
            Button inventory = new Button(8, 8, 240, 12, "guistrings.inventory") {
                @Override
                protected void onPressed() {
                    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_INVENTORY, getContainer().entity.getEntityId(), 0, 0);
                }
            };
            addGuiElement(inventory);
        }
        addGuiElement(area);
        if (getContainer().storage != null) {
            addTrades();
        } else {
            addSingleMessage("guistrings.trader.missing_backpack");
        }
    }

    private void addSingleMessage(String text){
        area.addGuiElement(new Label(8, 8, text));
    }

    private void addTrades() {
        List<Trade> trades = getContainer().tradeList.getTrades();
        int totalHeight = 8;
        if(trades.isEmpty()){
            addSingleMessage("guistrings.trader.no_trade");
        }else {
            POTrade trade;
            for (int i = 0; i < trades.size(); i++) {
                trade = (POTrade) trades.get(i);
                if (trade.isAvailable(getContainer().storage)) {
                    totalHeight = addTrade(trade, i, totalHeight);
                }
            }
            if(totalHeight == 8){
                addSingleMessage("guistrings.trader.cant_trade");
            }
        }
        area.setAreaSize(totalHeight);
    }

    private int addTrade(final Trade trade, final int tradeIndex, int startHeight) {
        int gridX = 0, gridY = 0, slotX, slotY;
        for (int i = 0; i < trade.size(); i++) {
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
        }
        int startWidth = 8 + 3 * 18;
        if (trade.size() < 3) {
            startWidth += (trade.size() - 3) * 18;
        }

        area.addGuiElement(new Label(startWidth + 1, startHeight + (gridY + 1) * 5, ">"));

        Button tradeButton = new Button(2 * startWidth + 9, startHeight + 17, 70, 20, "guistrings.trade") {
            @Override
            protected void onPressed() {
                trade.performTrade(player, getContainer().storage);
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger("doTrade", tradeIndex);
                sendDataToContainer(tag);
                refreshGui();
            }
        };
        area.addGuiElement(tradeButton);

        startHeight += 18 * gridY;
        area.addGuiElement(new Line(0, startHeight + 1, xSize, startHeight + 1, 1, 0x000000ff));
        startHeight += 5;
        return startHeight;
    }

    private void addTradeInputSlot(final Trade trade, int x, int y, final int slotNum) {
        ItemStack stack = trade.getInputStack(slotNum);
        stack = stack == null ? null : stack.copy();
        final ItemSlot slot = new ItemSlot(x, y, stack, this);
        if (stack == null) {
            slot.addTooltip("guistrings.npc.trade_input_slot");
        }
        area.addGuiElement(slot);
    }

    private void addTradeOutputSlot(final Trade trade, int x, int y, final int slotNum) {
        ItemStack stack = trade.getOutputStack(slotNum);
        stack = stack == null ? null : stack.copy();
        final ItemSlot slot = new ItemSlot(x, y, stack, this);
        if (stack == null) {
            slot.addTooltip("guistrings.npc.trade_output_slot");
        }
        area.addGuiElement(slot);
    }


}
