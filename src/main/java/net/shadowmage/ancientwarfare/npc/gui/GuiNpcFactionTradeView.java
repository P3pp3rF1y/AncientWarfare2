package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.*;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcFactionTradeView;
import net.shadowmage.ancientwarfare.npc.trade.FactionTrade;
import net.shadowmage.ancientwarfare.npc.trade.Trade;

import java.util.List;

public class GuiNpcFactionTradeView extends GuiContainerBase<ContainerNpcFactionTradeView> {

    Button inventoryButton;
    Button setupButton;
    CompositeScrolled area;

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

        List<Trade> trades = getContainer().tradeList.getTrades();

        int totalHeight = 8;
        if(trades.isEmpty()){
            area.addGuiElement(new Label(8, 8, "guistrings.trader.no_trade"));
        }else {
            for (int i = 0; i < trades.size(); i++) {
                totalHeight = addTrade((FactionTrade) trades.get(i), i, totalHeight);
            }
        }

        area.setAreaSize(totalHeight);
    }

    private int addTrade(final FactionTrade trade, final int tradeNum, int startHeight) {
        if (trade.getCurrentAvailable() <= 0) {
            return startHeight;
        }
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
        startWidth *= 2;
        startWidth += 9;
        Button tradeButton = new Button(startWidth, startHeight + 17, 70, 20, "guistrings.trade") {
            @Override
            protected void onPressed() {
                trade.performTrade(player, null);
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger("doTrade", tradeNum);
                sendDataToContainer(tag);
                refreshGui();
            }
        };
        area.addGuiElement(tradeButton);

        Label available = new Label(startWidth, startHeight, StatCollector.translateToLocal("guistrings.trades_available") + ": " + trade.getCurrentAvailable());
        area.addGuiElement(available);

        startHeight += 18 * gridY;//input/output grid size
        area.addGuiElement(new Line(0, startHeight + 1, xSize, startHeight + 1, 1, 0x000000ff));
        startHeight += 5;//separator line and padding
        return startHeight;
    }

    private void addTradeInputSlot(final FactionTrade trade, int x, int y, final int slotNum) {
        ItemStack stack = trade.getInputStack(slotNum);
        stack = stack == null ? null : stack.copy();
        final ItemSlot slot = new ItemSlot(x, y, stack, this);
        if (stack == null) {
            slot.addTooltip("guistrings.npc.trade_input_slot");
        }
        area.addGuiElement(slot);
    }

    private void addTradeOutputSlot(final FactionTrade trade, int x, int y, final int slotNum) {
        ItemStack stack = trade.getOutputStack(slotNum);
        stack = stack == null ? null : stack.copy();
        final ItemSlot slot = new ItemSlot(x, y, stack, this);
        if (stack == null) {
            slot.addTooltip("guistrings.npc.trade_output_slot");
        }
        area.addGuiElement(slot);
    }

}
