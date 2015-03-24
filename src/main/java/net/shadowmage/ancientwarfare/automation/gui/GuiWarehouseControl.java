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
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiWarehouseControl extends GuiContainerBase<ContainerWarehouseControl> {

    CompositeScrolled area;
    Button sortChange;
    Text input;
    Checkbox sortOrderBox;
    SortType sortType = SortType.NAME;
    SortOrder sortOrder = SortOrder.DESCENDING;
    ComparatorItemStack sorter;
    private Label storedLabel;

    public GuiWarehouseControl(ContainerBase par1Container) {
        super(par1Container, 178, 240);
        sorter = new ComparatorItemStack(sortType, sortOrder);
    }

    @Override
    public void initElements() {
        sortChange = new Button(8, 8, 110, 12, StatCollector.translateToLocal("guistrings.automation.sort_type") + ":" + StatCollector.translateToLocal(sortType.toString())) {
            @Override
            protected void onPressed() {
                sortType = sortType.next();
                setText(StatCollector.translateToLocal("guistrings.automation.sort_type") + ": " + StatCollector.translateToLocal(sortType.toString()));
                refreshGui();
            }
        };
        addGuiElement(sortChange);

        sortOrderBox = new Checkbox(8 + 55 + 55 + 4, 6, 16, 16, "guistrings.automation.descending") {
            @Override
            public void onToggled() {
                super.onToggled();
                sortOrder = checked() ? SortOrder.ASCENDING : SortOrder.DESCENDING;
                String name = sortOrder == SortOrder.ASCENDING ? "ascending" : "descending";
                label = StatCollector.translateToLocal("guistrings.automation." + name);
                refreshGui();
            }
        };
        addGuiElement(sortOrderBox);

        input = new Text(8, 8 + 12 + 4, 178 - 16, "", this) {
            @Override
            public void onTextUpdated(String oldText, String newText) {
                if (sortType == SortType.NAME_INPUT) {
                    refreshGui();
                }
            }
        };
        addGuiElement(input);

        area = new CompositeItemSlots(this, 0, 8 + 12 + 4 + 12 + 2, 178, 96, this);

        Listener l = new Listener(Listener.MOUSE_DOWN) {
            @Override
            public boolean onEvent(GuiElement widget, ActivationEvent evt) {
                if (evt.mButton == 0 && widget.isMouseOverElement(evt.mx, evt.my) && !area.isMouseOverSubElement(evt.mx, evt.my)) {
                    getContainer().handleClientRequestSpecific(null, Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
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

        storedLabel = new Label(8 + 40 + 4, 240 - 8 - 11, StatCollector.translateToLocal("guistrings.warehouse.storage") + ": " + getContainer().currentStored + "/" + getContainer().maxStorage);
        addGuiElement(storedLabel);
    }

    @Override
    public void setupElements() {
        area.clearElements();
        addInventoryViewElements();
        storedLabel.setText(StatCollector.translateToLocal("guistrings.warehouse.storage") + ": " + getContainer().currentStored + "/" + getContainer().maxStorage);
    }

    private void addInventoryViewElements() {
        ItemSlot slot;
        int qty;
        ItemStack stack;
        int x = 0, y = 0;
        int totalSize = 8;
        List<ItemStack> displayStacks = new ArrayList<ItemStack>();
        for (ItemHashEntry entry : getContainer().itemMap.keySet()) {
            qty = getContainer().itemMap.getCount(entry);
            stack = entry.getItemStack();
            stack.stackSize = qty;
            displayStacks.add(stack);
        }

        sortItems(displayStacks);

        for (ItemStack displayStack : displayStacks) {
            slot = new ItemSlot(4 + x * 18, 3 + y * 18, displayStack, this) {
                @Override
                public void onSlotClicked(ItemStack stack) {
                    getContainer().handleClientRequestSpecific(getStack(), Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
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
