package net.shadowmage.ancientwarfare.core.gui.manual.elements;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.gui.manual.GuiManual;
import net.shadowmage.ancientwarfare.core.gui.manual.IElementWrapperCreator;
import net.shadowmage.ancientwarfare.core.manual.ItemElement;

import java.util.List;

public class ItemElementWrapper extends BaseElementWrapper<ItemElement> {
	private static final int HEIGHT = 18;
	private static final int WIDTH = 18;
	private static final int PADDING_BETWEEN = 2;
	private static final int BOTTOM_PADDING = 2;

	public ItemElementWrapper(GuiManual gui, int topLeftY, int width, int height, ItemElement element) {
		super(gui, 0, topLeftY, width, height, element);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		super.render(mouseX, mouseY, partialTick);

		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.enableDepth();
		ItemStack[] stacks = getElement().getItemStacks();
		for (int i = 0; i < Math.min(stacks.length, 9); i++) {
			Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(stacks[i], renderX + i * (WIDTH + PADDING_BETWEEN), renderY);
		}
		GlStateManager.disableDepth();
		RenderHelper.disableStandardItemLighting();
	}

	public static class Creator implements IElementWrapperCreator<ItemElement> {
		@Override
		public List<BaseElementWrapper<ItemElement>> construct(GuiManual gui, int topLeftY, int width, int remainingPageHeight, int emptyPageHeight, ItemElement element) {
			return ImmutableList.of(new ItemElementWrapper(gui, remainingPageHeight < HEIGHT ? 0 : topLeftY, width, HEIGHT + BOTTOM_PADDING, element));
		}
	}
}
