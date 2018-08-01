package net.shadowmage.ancientwarfare.core.gui.manual;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.core.manual.HeadingElement;

import java.util.List;

public class HeadingElementWrapper extends BaseElementWrapper<HeadingElement> {
	private static final int TOP_PADDING = 3;
	private static final int BOTTOM_PADDING = 3;

	public HeadingElementWrapper(int topLeftY, int width, int height, HeadingElement element) {
		super(0, topLeftY, width, height, element);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		super.render(mouseX, mouseY, partialTick);
		String prefix = getElement().getLevel() == 1 ? "§l" : "§n";
		Minecraft.getMinecraft().fontRenderer.drawSplitString(prefix + getElement().getText() + "§r", renderX, renderY + TOP_PADDING, width, 0x000000);
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
		public List<BaseElementWrapper<HeadingElement>> construct(int topLeftY, int width, int remainingPageHeight, int emptyPageHeight, HeadingElement element) {
			int textHeight = Minecraft.getMinecraft().fontRenderer.getWordWrappedHeight(element.getText(), width);
			return ImmutableList.of(new HeadingElementWrapper(topLeftY, width, textHeight + TOP_PADDING + BOTTOM_PADDING, element));
		}
	}
}
