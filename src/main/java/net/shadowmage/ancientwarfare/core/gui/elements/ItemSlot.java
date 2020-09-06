package net.shadowmage.ancientwarfare.core.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.interfaces.ITooltipRenderer;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class ItemSlot extends GuiElement {
	private static final RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
	private ItemStack item = ItemStack.EMPTY;
	protected ITooltipRenderer render;
	protected boolean highlightOnMouseOver = true;
	protected boolean renderItemQuantity = true;
	protected boolean renderSlotBackground = true;
	protected boolean renderLabel = false;

	public ItemSlot(int topLeftX, int topLeftY, ItemStack item, ITooltipRenderer render) {
		super(topLeftX - 1, topLeftY - 1, 18, 18);
		this.item = item;
		this.render = render;

		Listener listener = new Listener(Listener.MOUSE_DOWN) {
			@Override
			public boolean onEvent(GuiElement widget, ActivationEvent evt) {
				if (widget.isMouseOverElement(evt.mx, evt.my)) {
					ItemStack stack = Minecraft.getMinecraft().player.inventory.getItemStack();
					onSlotClicked(stack, evt.mButton == 1);
				}
				return true;
			}
		};
		addNewListener(listener);
	}

	public void setRenderLabel(boolean val) {
		this.renderLabel = val;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	public ItemSlot setRenderTooltip(boolean val) {
		this.renderTooltip = val;
		return this;
	}

	public ItemSlot setRenderItemQuantity(boolean val) {
		this.renderItemQuantity = val;
		return this;
	}

	public ItemSlot setHighlightOnMouseOver(boolean val) {
		this.highlightOnMouseOver = val;
		return this;
	}

	public ItemSlot setRenderSlotBackground(boolean val) {
		this.renderSlotBackground = val;
		return this;
	}

	public ItemStack getStack() {
		return item;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		if (visible) {
			Minecraft mc = Minecraft.getMinecraft();
			if (renderSlotBackground) {
				mc.renderEngine.bindTexture(widgetTexture1);
				RenderTools.renderQuarteredTexture(256, 256, 152, 120, 18, 18, renderX, renderY, width, height);
			}

			if (!this.item.isEmpty()) {
				itemRender.zLevel = 10.0F;
				FontRenderer font = item.getItem().getFontRenderer(item);
				if (font == null) {
					font = mc.fontRenderer;
				}

				GlStateManager.enableRescaleNormal();
				RenderHelper.enableGUIStandardItemLighting();
				itemRender.renderItemAndEffectIntoGUI(item, renderX + 1, renderY + 1);
				if (renderItemQuantity && item.getCount() > 1) {
					itemRender.renderItemOverlayIntoGUI(font, item, renderX + 1, renderY + 1, "");
					renderStackSize(renderX + 1, renderY + 1, item.getCount(), font);
				}
				RenderHelper.disableStandardItemLighting();
				GlStateManager.disableRescaleNormal();
				if (renderLabel) {
					int x = renderX + 18;
					int y = renderY + 3;
					GlStateManager.color(1.f, 1.f, 1.f, 1.f);
					font.drawStringWithShadow(item.getDisplayName(), x, y, 0xffffffff);
				}
			}

			if (isMouseOverElement(mouseX, mouseY)) {
				if (highlightOnMouseOver) {
					/*
					 *  TODO -- find proper alpha for blend..it is close now, but probably not an exact match for vanilla
                     *  highlighting
                     */
					GlStateManager.color(1.f, 1.f, 1.f, 0.55f);

					GlStateManager.enableBlend();
					GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GlStateManager.disableLighting();
					GlStateManager.disableTexture2D();
					GlStateManager.pushMatrix();
					GlStateManager.translate(0, 0, 200);
					GlStateManager.glBegin(GL11.GL_QUADS);
					GL11.glVertex2f(renderX + 1, renderY + 1);
					GL11.glVertex2f(renderX + 1, renderY + 1 + (height - 2));
					GL11.glVertex2f(renderX + 1 + (width - 2), renderY + 1 + (height - 2));
					GL11.glVertex2d(renderX + 1 + (width - 2), renderY + 1);
					GlStateManager.glEnd();
					GlStateManager.popMatrix();
					GlStateManager.color(1.f, 1.f, 1.f, 1.f);
					GlStateManager.enableTexture2D();
					GlStateManager.disableBlend();
				}
				if (renderTooltip && this.render != null) {
					if (this.tooltip != null) {
						this.render.handleElementTooltipRender(tooltip, mouseX, mouseY);
					} else if (!this.item.isEmpty()) {
						this.render.handleItemStackTooltipRender(item, mouseX, mouseY);
					}
				}
			}
			GlStateManager.color(1, 1, 1, 1);
		}
	}

	public void renderStackSize(int renderX, int renderY, int stackSize, FontRenderer fr) {
		GlStateManager.pushMatrix();
		float ox = renderX + 16, oy = renderY + 8;
		GlStateManager.translate(ox + 0.5f, oy + 0.5f, 0);
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		GlStateManager.disableBlend();

		String s1 = String.valueOf(stackSize);

		float w = fr.getStringWidth(s1);
		float scale = stackSize > 99 ? 0.5f : 1.f;
		int oy1 = stackSize > 99 ? 6 : 0;

		GlStateManager.scale(scale, scale, scale);

		fr.drawStringWithShadow(s1, -(int) w, oy1, 16777215);

		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		GlStateManager.popMatrix();
	}

	public void onSlotClicked(ItemStack stack, boolean rightClicked) {

	}

}
