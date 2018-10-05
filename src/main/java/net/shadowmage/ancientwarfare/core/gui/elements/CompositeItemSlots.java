package net.shadowmage.ancientwarfare.core.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.interfaces.ITooltipRenderer;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class CompositeItemSlots extends CompositeScrolled {
	private static RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
	private List<ItemSlot> itemSlots = new ArrayList<>();
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
			if (element.renderY > renderY + height || element.renderY + element.height < renderY
					|| element.renderX > renderX + width || element.renderX + element.width < renderX) {
				continue;
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
		GlStateManager.enableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		GlStateManager.color(1.f, 1.f, 1.f, 1.f);
		for (ItemSlot slot : itemSlots) {
			renderSlotBackground(slot);
		}

		//needs texture, lighting, color, alpha, and depth-test enabled
		GlStateManager.enableDepth();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		RenderHelper.enableGUIStandardItemLighting();
		itemRender.zLevel = 10.0F;
		for (ItemSlot slot : itemSlots) {
			renderItemStack(slot);
		}

		//needs texture(fonts), color, alpha enabled
		//needs lighting, depth-test disabled
		GlStateManager.disableDepth();
		GlStateManager.disableLighting();
		GlStateManager.enableTexture2D();
		for (ItemSlot slot : itemSlots) {
			renderOverlay(slot);
		}

		//needs texture disabled (draw white quad @ alpha for highlight)
		for (ItemSlot slot : itemSlots) {
			renderSlotHighlight(slot, mouseX, mouseY);
		}
		//reset renderabled and render-viewport
		itemSlots.clear();
		popViewport();

		//scroll-bar rendering, needs lighting/depth test disabled, texture enabled
		GlStateManager.enableTexture2D();
		scrollbar.render(mouseX, mouseY, partialTick);
	}

	private void renderSlotBackground(ItemSlot slot) {
		if (slot.visible && slot.renderSlotBackground) {
			RenderTools.renderQuarteredTexture(256, 256, 152, 120, 18, 18, slot.renderX, slot.renderY, slot.width, slot.height);
		}
	}

	private void renderSlotHighlight(ItemSlot slot, int mouseX, int mouseY) {
		@Nonnull ItemStack stack = slot.getStack();
		if (stack.isEmpty() || !slot.visible) {
			return;
		}
		if (slot.highlightOnMouseOver && slot.isMouseOverElement(mouseX, mouseY)) {
			GlStateManager.disableDepth();
			GlStateManager.disableLighting();
			GlStateManager.disableTexture2D();
			GlStateManager.color(1.f, 1.f, 1.f, 0.5f);

			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(slot.renderX, slot.renderY);
			GL11.glVertex2f(slot.renderX, (float) slot.renderY + slot.height);
			GL11.glVertex2f((float) slot.renderX + slot.width, (float) slot.renderY + slot.height);
			GL11.glVertex2d((float) slot.renderX + slot.width, (float) slot.renderY);
			GlStateManager.glEnd();
			GlStateManager.disableBlend();
			if (slot.renderTooltip && !slot.getStack().isEmpty() && render != null) {
				if (slot.tooltip != null) {
					this.render.handleElementTooltipRender(slot.tooltip, mouseX, mouseY);
				} else {
					this.render.handleItemStackTooltipRender(slot.getStack(), mouseX, mouseY);
				}
			}
			GlStateManager.color(1.f, 1.f, 1.f, 1.f);
			GlStateManager.enableDepth();
		}
	}

	private void renderOverlay(ItemSlot slot) {
		@Nonnull ItemStack stack = slot.getStack();
		if (stack.isEmpty() || !slot.visible) {
			return;
		}
		Minecraft mc = Minecraft.getMinecraft();
		FontRenderer font = stack.getItem().getFontRenderer(stack);
		if (font == null) {
			font = mc.fontRenderer;
		}
		itemRender.renderItemOverlayIntoGUI(font, stack, slot.renderX + 1, slot.renderY + 1, "");
		if (slot.renderItemQuantity && slot.getStack().getCount() > 1) {
			slot.renderStackSize(slot.renderX + 1, slot.renderY + 1, stack.getCount(), font);
		}
	}

	private void renderItemStack(ItemSlot slot) {
		@Nonnull ItemStack stack = slot.getStack();
		if (stack.isEmpty() || !slot.visible) {
			return;
		}
		itemRender.renderItemAndEffectIntoGUI(stack, slot.renderX + 1, slot.renderY + 1);
	}

}
