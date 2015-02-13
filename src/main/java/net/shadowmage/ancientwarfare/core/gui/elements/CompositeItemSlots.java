package net.shadowmage.ancientwarfare.core.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.interfaces.ITooltipRenderer;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class CompositeItemSlots extends CompositeScrolled {
    protected static RenderItem itemRender = new RenderItem();
    private List<ItemSlot> itemSlots = new ArrayList<ItemSlot>();
    ITooltipRenderer render;

    public CompositeItemSlots(GuiContainerBase gui, int topLeftX, int topLeftY, int width, int height, ITooltipRenderer render) {
        super(gui, topLeftX, topLeftY, width, height);
        this.render = render;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        if (!isMouseOverElement(mouseX, mouseY)) {
            mouseX = Integer.MIN_VALUE;
            mouseY = Integer.MIN_VALUE;
        }
        Minecraft.getMinecraft().renderEngine.bindTexture(backgroundTextureLocation);
        //render background before setting viewport so that it is not cropped
        RenderTools.renderQuarteredTexture(256, 256, 0, 0, 256, 240, renderX, renderY, width, height);
        setViewport();
        for (GuiElement element : this.elements) {
            if (element.renderY > renderY + height || element.renderY + element.height < renderY) {
                continue;//manual frustrum culling of elements, on Y axis
            }
            if (element.renderX > renderX + width || element.renderX + element.width < renderX) {
                continue;//manual frustrum culling of elements, on X axis
            }
            if (element instanceof ItemSlot) {
                itemSlots.add((ItemSlot) element);
            } else {
                element.render(mouseX, mouseY, partialTick);
            }
        }

        Minecraft mc = Minecraft.getMinecraft();
        mc.renderEngine.bindTexture(widgetTexture1);

        //needs texture enabled
        //lighting, full color and alpha, depth-test disabled
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
        for (ItemSlot slot : itemSlots) {
            renderSlotBackground(slot);
        }

        //needs texture, lighting, color, alpha, and depth-test enabled
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        RenderHelper.enableGUIStandardItemLighting();
        itemRender.zLevel = 10.0F;
        for (ItemSlot slot : itemSlots) {
            renderItemStack(slot, mouseX, mouseY);
        }

        //needs texture(fonts), color, alpha enabled
        //needs lighting, depth-test disabled
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        for (ItemSlot slot : itemSlots) {
            renderOverlay(slot, mouseX, mouseY);
        }

        //needs texture disabled (draw white quad @ alpha for highlight)
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.f, 1.f, 1.f, 0.55f);
        for (ItemSlot slot : itemSlots) {
            renderSlotHighlight(slot, mouseX, mouseY);
        }
        GL11.glColor4f(1.f, 1.f, 1.f, 1.f);

        //reset renderabled and render-viewport
        itemSlots.clear();
        popViewport();

        //scroll-bar rendering, needs lighting/depth test disabled, texture enabled
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        scrollbar.render(mouseX, mouseY, partialTick);
    }

    private void renderSlotBackground(ItemSlot slot) {
        if (slot.visible && slot.renderSlotBackground) {
            RenderTools.renderQuarteredTexture(256, 256, 152, 120, 18, 18, slot.renderX, slot.renderY, slot.width, slot.height);
        }
    }

    private void renderSlotHighlight(ItemSlot slot, int mouseX, int mouseY) {
        ItemStack stack = slot.getStack();
        if (stack == null || stack.getItem() == null || !slot.visible) {
            return;
        }
        if (slot.highlightOnMouseOver && slot.isMouseOverElement(mouseX, mouseY)) {
            /**
             *  TODO -- find proper alpha for blend..it is close now, but probably not an exact match for vanilla
             *  highlighting
             */
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(slot.renderX, slot.renderY);
            GL11.glVertex2f(slot.renderX, slot.renderY + slot.height);
            GL11.glVertex2f(slot.renderX + slot.width, slot.renderY + slot.height);
            GL11.glVertex2d(slot.renderX + slot.width, slot.renderY);
            GL11.glEnd();
            GL11.glDisable(GL11.GL_BLEND);
            if (slot.renderTooltip && slot.getStack() != null && render != null) {
                if (slot.tooltip != null) {
                    this.render.handleElementTooltipRender(slot.tooltip);
                } else {
                    this.render.handleItemStackTooltipRender(slot.getStack());
                }
            }
        }
    }

    private void renderOverlay(ItemSlot slot, int mouseX, int mouseY) {
        ItemStack stack = slot.getStack();
        if (stack == null || stack.getItem() == null || !slot.visible) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer font = stack.getItem().getFontRenderer(stack);
        if (font == null) {
            font = mc.fontRenderer;
        }
        if (slot.renderItemQuantity && slot.getStack().stackSize > 1) {
            itemRender.renderItemOverlayIntoGUI(font, mc.getTextureManager(), stack, slot.renderX + 1, slot.renderY + 1, "");
            slot.renderStackSize(slot.renderX + 1, slot.renderY + 1, stack.stackSize, font);
        }
    }

    private void renderItemStack(ItemSlot slot, int mouseX, int mouseY) {
        ItemStack stack = slot.getStack();
        if (stack == null || stack.getItem() == null || !slot.visible) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer font = stack.getItem().getFontRenderer(stack);
        if (font == null) {
            font = mc.fontRenderer;
        }
        itemRender.renderItemAndEffectIntoGUI(font, mc.getTextureManager(), stack, slot.renderX + 1, slot.renderY + 1);
    }

}
