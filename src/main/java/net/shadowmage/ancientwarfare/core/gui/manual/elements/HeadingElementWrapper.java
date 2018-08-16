package net.shadowmage.ancientwarfare.core.gui.manual.elements;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.text.TextFormatting;
import net.shadowmage.ancientwarfare.core.gui.manual.GuiManual;
import net.shadowmage.ancientwarfare.core.gui.manual.IElementWrapperCreator;
import net.shadowmage.ancientwarfare.core.manual.HeadingElement;
import net.shadowmage.ancientwarfare.core.proxy.ClientProxy;

import java.util.List;

public class HeadingElementWrapper extends BaseElementWrapper<HeadingElement> {
	private static final int TOP_PADDING = 3;
	private static final int BOTTOM_PADDING = 3;

	public HeadingElementWrapper(GuiManual gui, int topLeftY, int width, int height, HeadingElement element) {
		super(gui, 0, topLeftY, width, height, element);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		super.render(mouseX, mouseY, partialTick);
		TextFormatting prefix = getElement().getLevel() == 1 ? TextFormatting.BOLD : TextFormatting.UNDERLINE;
		ClientProxy.getUnicodeFontRenderer().drawSplitString(prefix + getElement().getText() + TextFormatting.RESET, renderX, renderY + TOP_PADDING, width, 0x000000);
	}

	@Override
	public int getHeight() {
		return super.getHeight() + TOP_PADDING + BOTTOM_PADDING;
	}

	@Override
	public boolean shouldKeepWithNext() {
		return true;
	}

	public static class Creator implements IElementWrapperCreator<HeadingElement> {
		@Override
		public List<BaseElementWrapper<HeadingElement>> construct(GuiManual gui, int topLeftY, int width, int remainingPageHeight, int emptyPageHeight, HeadingElement element) {
			int textHeight = ClientProxy.getUnicodeFontRenderer().getWordWrappedHeight(element.getText(), width);
			return ImmutableList.of(new HeadingElementWrapper(gui, topLeftY, width, textHeight + TOP_PADDING + BOTTOM_PADDING, element));
		}
	}
}
