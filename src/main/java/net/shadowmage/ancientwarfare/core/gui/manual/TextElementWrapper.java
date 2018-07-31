package net.shadowmage.ancientwarfare.core.gui.manual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.manual.TextElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class TextElementWrapper extends BaseElementWrapper<TextElement> {
	private static final int SPACING = 9;

	public TextElementWrapper(int topLeftY, int width, int height, TextElement element) {
		super(0, topLeftY, width, height, element);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		super.render(mouseX, mouseY, partialTick);
		Minecraft.getMinecraft().fontRenderer.drawSplitString(getElement().getText(), renderX, renderY, width, 0x000000);
	}

	@Override
	public int getHeight() {
		return super.getHeight() + SPACING;
	}

	public static class Creator implements IElementWrapperCreator<TextElement> {
		@Override
		public PageElements<TextElement> construct(int topLeftY, int width, int remainingPageHeight, int emptyPageHeight, TextElement element) {
			FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
			List<String> textLines = fontRenderer.listFormattedStringToWidth(element.getText(), width);

			int fontHeight = fontRenderer.FONT_HEIGHT;

			int textHeight = fontHeight * textLines.size();
			if (textHeight <= remainingPageHeight) {
				return new PageElements<>(new TextElementWrapper(topLeftY, width, textHeight, element), null);
			} else if (remainingPageHeight >= 2 * fontHeight) {
				int linesCurrent = remainingPageHeight / fontHeight;
				TextElement currentElement = new TextElement(textLines.stream().limit(linesCurrent).collect(Collectors.joining("\n")));
				List<BaseElementWrapper<TextElement>> nextElements = getNextElements(width, emptyPageHeight, textLines, fontHeight, linesCurrent);

				return new PageElements<>(new TextElementWrapper(topLeftY, width, linesCurrent * fontHeight, currentElement), nextElements);
			} else {
				return new PageElements<>(null, getNextElements(width, emptyPageHeight, textLines, fontHeight, 0));
			}
		}

		private List<BaseElementWrapper<TextElement>> getNextElements(int width, int emptyPageHeight, List<String> textLines, int fontHeight, int linesCurrent) {
			int skipLines = linesCurrent;
			List<BaseElementWrapper<TextElement>> nextElements = new ArrayList<>();
			int maxLinesOnPage = emptyPageHeight / fontHeight;
			while (textLines.size() - skipLines > 0) {
				int linesToAdd = Math.min(textLines.size() - skipLines, maxLinesOnPage);

				TextElement textElement = new TextElement(textLines.stream().skip(skipLines).limit(linesToAdd).collect(Collectors.joining("\n")));
				nextElements.add(new TextElementWrapper(0, width, linesToAdd * fontHeight, textElement));
				skipLines += linesToAdd;
			}
			return nextElements;
		}
	}
}
