package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.shadowmage.ancientwarfare.core.block.Direction;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.*;
import net.shadowmage.ancientwarfare.core.interfaces.ITooltipRenderer;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.container.ContainerRoutingOrder;
import net.shadowmage.ancientwarfare.npc.orders.RoutingOrder.RoutePoint;
import org.lwjgl.input.Mouse;

import java.util.List;

public class GuiRoutingOrder extends GuiContainerBase<ContainerRoutingOrder> {

    private boolean hasChanged = false;
    private CompositeScrolled area;

    public GuiRoutingOrder(ContainerBase container) {
        super(container);
    }

    @Override
    public void initElements() {
        area = new CompositeScrolled(this, 0, 0, xSize, ySize - 4 * 18 - 8 - 4 - 8);
        addGuiElement(area);
    }

    @Override
    public void setupElements() {
        area.clearElements();
        List<RoutePoint> entries = getContainer().routingOrder.getEntries();
        ItemSlot slot;
        Label label;
        Button button;
        int totalHeight = 12;
        int index = 0;

        Block block;
        BlockPos pos;

        String labelString;
        for (RoutePoint point : entries) {
            pos = point.getTarget();
            block = player.world.getBlock(pos.x, pos.y, pos.z);
            label = new Label(8, totalHeight, block == null ? "" : block.getLocalizedName());
            area.addGuiElement(label);

            label = new Label(120, totalHeight, pos.toString());
            area.addGuiElement(label);

            labelString = Direction.getDirectionFor(point.getBlockSide()).getTranslationKey();
            button = new IndexedButton(8, totalHeight + 10, 55, 12, labelString, index) {
                @Override
                protected void onPressed() {
                    getContainer().routingOrder.changeBlockSide(index);
                    refreshGui();
                    hasChanged = true;
                }
            };
            area.addGuiElement(button);

            labelString = point.getRouteType().getTranslationKey();
            button = new IndexedButton(8 + 55 + 2, totalHeight + 10, 79, 12, labelString, index) {
                @Override
                protected void onPressed(int mButton) {
                    getContainer().routingOrder.changeRouteType(index, mButton == 1);
                    refreshGui();
                    hasChanged = true;
                }
            };
            area.addGuiElement(button);
            
            labelString = point.getIgnoreDamage() ? EnumChatFormatting.RED.toString() + EnumChatFormatting.STRIKETHROUGH.toString() : "";
            labelString += I18n.format("guistrings.dmg");
            button = new IndexedButton(8 + 55 + 2 + 79 + 2, totalHeight + 10, 38, 12, labelString, index) {
                @Override
                protected void onPressed() {
                    getContainer().routingOrder.toggleIgnoreDamage(index);
                    hasChanged = true;
                    refreshGui();
                }
            };
            button.addTooltip(I18n.format("guistrings.dmg.tooltipprefix." + (point.getIgnoreDamage() ? "disabled" : "enabled")) + I18n.format("guistrings.dmg.tooltip"), 28);
            area.addGuiElement(button);

            labelString = point.getIgnoreTag() ? EnumChatFormatting.RED.toString() + EnumChatFormatting.STRIKETHROUGH.toString() : "";
            labelString += I18n.format("guistrings.tag");
            button = new IndexedButton(8 + 55 + 2 + 79 + 2 + 38 + 2, totalHeight + 10, 38, 12, labelString, index) {
                @Override
                protected void onPressed() {
                    getContainer().routingOrder.toggleIgnoreTag(index);
                    hasChanged = true;
                    refreshGui();
                }
            };
            button.addTooltip(I18n.format("guistrings.tag.tooltipprefix." + (point.getIgnoreTag() ? "disabled" : "enabled")) + I18n.format("guistrings.tag.tooltip"), 28);
            area.addGuiElement(button);
            
            for (int i = 0; i < point.getFilterSize(); i++) {
                slot = new IndexedRoutePointItemSlot(9 + i * 18, totalHeight + 10 + 12 + 2, point.getFilterInSlot(i), this, point, i) {
                    @Override
                    public void onSlotClicked(ItemStack stack) {
                        onFilterSlotClicked(this, point, index, stack);
                    }
                };
                area.addGuiElement(slot);
            }
            
            button = new IndexedButton(8 + 55 + 2 + 80 + 2 + 40 + 2 + 40, totalHeight, 12, 9, "guistrings.npc.remove_point", index) {
                @Override
                protected void onPressed() {
                    getContainer().routingOrder.remove(index);
                    refreshGui();
                    hasChanged = true;
                }
            };
            area.addGuiElement(button);

            button = new IndexedButton(8 + 55 + 2 + 80 + 2 + 40 + 2 + 40, totalHeight + 15, 12, 12, "guistrings.moveup", index) {
                @Override
                protected void onPressed() {
                    getContainer().routingOrder.increment(index);
                    refreshGui();
                    hasChanged = true;
                }
            };
            area.addGuiElement(button);

            button = new IndexedButton(8 + 55 + 2 + 80 + 2 + 40 + 2 + 40, totalHeight + 29, 12, 12, "guistrings.movedown", index) {
                @Override
                protected void onPressed() {
                    getContainer().routingOrder.decrement(index);
                    refreshGui();
                    hasChanged = true;
                }
            };
            area.addGuiElement(button);

            totalHeight += 18 + 10 + 12 + 8;

            area.addGuiElement(new Line(0, totalHeight - 1, xSize - 13, totalHeight - 1, 1, 0x000000ff));

            totalHeight += 6;
            
            index++;
        }
        area.setAreaSize(totalHeight);
    }

    private void onFilterSlotClicked(ItemSlot slot, RoutePoint point, int index, ItemStack stack) {
        //TODO move this functionality in as default for item-slots, or toggleable to enable?
        if (slot.getStack() != null && isShiftKeyDown()) {
            if (Mouse.getEventButton() == 0)//left
            {
                slot.getStack().grow(32);
                point.setFilter(index, slot.getStack());
            } else if (Mouse.getEventButton() == 1)//right
            {
                slot.getStack().shrink(32);
                if (slot.getStack().stackSize < 1) {
                    slot.getStack().setCount(1);
                }
                point.setFilter(index, slot.getStack());
            }
        } else if (slot.getStack() != null && isCtrlKeyDown()) {
            if (Mouse.getEventButton() == 0)//left
            {
                slot.getStack().grow(1);
                point.setFilter(index, slot.getStack());
            } else if (Mouse.getEventButton() == 1)//right
            {
                slot.getStack().shrink(1);
                if (slot.getStack().stackSize < 1) {
                    slot.getStack().setCount(1);
                }
                point.setFilter(index, slot.getStack());
            }
        } else {
            if (stack == null) {
                point.setFilter(index, null);
                slot.setItem(null);
            } else {
                if (InventoryTools.doItemStacksMatch(stack, slot.getStack())) {
                    if (Mouse.getEventButton() == 0)//left
                    {
                        slot.getStack().grow(stack.getCount());
                        point.setFilter(index, slot.getStack());
                    } else if (Mouse.getEventButton() == 1)//right
                    {
                        slot.getStack().shrink(stack.getCount());
                        if (slot.getStack().stackSize < 1) {
                            slot.getStack().setCount(1);
                        }
                        point.setFilter(index, slot.getStack());
                    }
                } else {
                    stack = stack.copy();
                    point.setFilter(index, stack);
                    slot.setItem(stack);
                }
            }
        }
        hasChanged = true;
    }

    @Override
    protected boolean onGuiCloseRequested() {
        if (hasChanged) {
            getContainer().onClose();
        }
        return super.onGuiCloseRequested();
    }

    private class IndexedRoutePointItemSlot extends ItemSlot {
        final RoutePoint point;
        final int index;

        public IndexedRoutePointItemSlot(int topLeftX, int topLeftY, ItemStack item,
                                         ITooltipRenderer render, RoutePoint point, int index) {
            super(topLeftX, topLeftY, item, render);
            this.point = point;
            this.index = index;
        }
    }

    private class IndexedButton extends Button {
        final int index;

        public IndexedButton(int topLeftX, int topLeftY, int width, int height, String text, int index) {
            super(topLeftX, topLeftY, width, height, text);
            this.index = index;
        }
    }

}
