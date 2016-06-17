package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseControl;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.elements.*;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap.ItemHashEntry;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools.ComparatorItemStack;
import net.shadowmage.ancientwarfare.core.util.InventoryTools.ComparatorItemStack.SortOrder;
import net.shadowmage.ancientwarfare.core.util.InventoryTools.ComparatorItemStack.SortType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class GuiWarehouseControl extends GuiContainerBase<ContainerWarehouseControl> {

    private CompositeScrolled area;
    private Text input;
    private SortType sortType = SortType.NAME;
    private SortOrder sortOrder = SortOrder.DESCENDING;
    private final ComparatorItemStack sorter;
    private Label storedLabel;

    public GuiWarehouseControl(ContainerBase par1Container) {
        super(par1Container, 178, 240);
        sorter = new ComparatorItemStack(sortType, sortOrder);
    }

    @Override
    public void initElements() {
        Button sortChange = new Button(8, 8, 110, 12, "guistrings.automation." + sortType.toString()) {
            @Override
            protected void onPressed() {
                sortType = sortType.next();
                setText("guistrings.automation." + sortType.toString());
                refreshGui();
            }
        };
        addGuiElement(sortChange);

        Checkbox sortOrderBox = new Checkbox(8 + 55 + 55 + 4, 6, 16, 16, "guistrings.automation.descending") {
            @Override
            public void onToggled() {
                super.onToggled();
                sortOrder = checked() ? SortOrder.ASCENDING : SortOrder.DESCENDING;
                String name = sortOrder.name().toLowerCase(Locale.ENGLISH);
                label = StatCollector.translateToLocal("guistrings.automation." + name);
                refreshGui();
            }
        };
        addGuiElement(sortOrderBox);

        input = new Text(8, 8 + 12 + 4, 178 - 16, "", this) {
            @Override
            public void onTextUpdated(String oldText, String newText) {
                if (!oldText.equals(newText)) {
                    refreshGui();
                }
            }
        };
        Listener l = new Listener(Listener.MOUSE_UP) {
            @Override
            public boolean onEvent(GuiElement widget, ActivationEvent evt) {
                if (evt.mButton == 1) {
                    ((Text) widget).setText("");
                    refreshGui();
                }
                return false;
            }
        };
        input.addNewListener(l);
        addGuiElement(input);

        area = new CompositeItemSlots(this, 0, 8 + 12 + 4 + 12 + 2, 178, 96, this);

        l = new Listener(Listener.MOUSE_DOWN) {
            @Override
            public boolean onEvent(GuiElement widget, ActivationEvent evt) {
                if (evt.mButton == 0 && widget.isMouseOverElement(evt.mx, evt.my) && !area.isMouseOverSubElement(evt.mx, evt.my)) {
                    getContainer().handleClientRequestSpecific(null, isShiftKeyDown(), false);
                }
                return true;
            }
        };
        area.addNewListener(l);
        addGuiElement(area);

        Button b = new Button(8, 240 - 8 - 12, 40, 12, "guistrings.automation.adjust_bounds") {
            @Override
            protected void onPressed() {
                NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_BOUNDS, getContainer().tileEntity.xCoord, getContainer().tileEntity.yCoord, getContainer().tileEntity.zCoord);
            }
        };
        addGuiElement(b);

        storedLabel = new Label(8 + 40 + 4, 240 - 8 - 11, StatCollector.translateToLocalFormatted("guistrings.warehouse.storage", getContainer().currentStored, getContainer().maxStorage));
        addGuiElement(storedLabel);
    }

    @Override
    public void setupElements() {
        area.clearElements();
        addInventoryViewElements();
        storedLabel.setText(StatCollector.translateToLocalFormatted("guistrings.warehouse.storage", getContainer().currentStored, getContainer().maxStorage));
    }

    private void addInventoryViewElements() {
        ItemStack stack;
        List<ItemStack> displayStacks = new ArrayList<ItemStack>();
        for (ItemHashEntry entry : getContainer().itemMap.keySet()) {
            stack = entry.getItemStack();
            stack.stackSize = getContainer().itemMap.getCount(entry);
            displayStacks.add(stack);
        }

        sortItems(displayStacks);

        int x = 0, y = 0;
        int totalSize = 8;
        ItemSlot slot;
        for (ItemStack displayStack : displayStacks) {
            slot = new ItemSlot(4 + x * 18, 3 + y * 18, displayStack, this) {
                @Override
                public void onSlotClicked(ItemStack stack, boolean rightClicked) {
                    ItemStack reqStack = getStack();
                    if (!(rightClicked && isShiftKeyDown()) && (reqStack != null) && (stack != null) && (reqStack.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(stack, reqStack)))
                        reqStack = null;
                    getContainer().handleClientRequestSpecific(reqStack, isShiftKeyDown(), rightClicked);
                }
            };
            area.addGuiElement(slot);
            x++;
            if (x >= 9) {
                x = 0;
                y++;
                totalSize += 18;
            }
        }
        area.setAreaSize(totalSize + 8);
    }

    private void sortItems(List<ItemStack> items) {
        sorter.setSortType(sortType);
        sorter.setSortOrder(sortOrder);
        sorter.setTextInput(input.getText());
        Collections.sort(items, sorter);
    }

}
