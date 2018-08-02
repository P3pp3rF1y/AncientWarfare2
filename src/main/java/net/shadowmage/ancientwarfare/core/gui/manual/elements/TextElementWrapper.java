package net.shadowmage.ancientwarfare.core.gui.manual.elements;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.gui.manual.GuiManual;
import net.shadowmage.ancientwarfare.core.gui.manual.IElementWrapperCreator;
import net.shadowmage.ancientwarfare.core.manual.TextElement;
import net.shadowmage.ancientwarfare.core.proxy.ClientProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class TextElementWrapper extends BaseElementWrapper<TextElement> {
	private static final int SPACING = 9;

	public TextElementWrapper(GuiManual gui, int topLeftY, int width, int height, TextElement element) {
		super(gui, 0, topLeftY, width, height, element);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		super.render(mouseX, mouseY, partialTick);
		ClientProxy.getUnicodeFontRenderer().drawSplitString(getElement().getText(), renderX, renderY, width, 0x000000);
	}

	@Override
	public int getHeight() {
		return super.getHeight() + SPACING;
	}

	public static class Creator implements IElementWrapperCreator<TextElement> {
		@Override
		public List<BaseElementWrapper<TextElement>> construct(GuiManual gui, int topLeftY, int width, int remainingPageHeight, int emptyPageHeight, TextElement element) {
			FontRenderer fontRenderer = ClientProxy.getUnicodeFontRenderer();
			List<String> textLines = fontRenderer.listFormattedStringToWidth(element.getText(), width);

			int fontHeight = fontRenderer.FONT_HEIGHT;

			int textHeight = fontHeight * textLines.size();
			if (textHeight <= remainingPageHeight) {
				return ImmutableList.of(new TextElementWrapper(gui, topLeftY, width, textHeight, element));
			} else {
				ImmutableList.Builder<BaseElementWrapper<TextElement>> listBuilder = new ImmutableList.Builder<>();
				int linesCurrent = Math.max(remainingPageHeight / fontHeight, 0);
				if (remainingPageHeight >= 2 * fontHeight) {
					TextElement firstElement = new TextElement(textLines.stream().limit(linesCurrent).collect(Collectors.joining("\n")));
					listBuilder.add(new TextElementWrapper(gui, topLeftY, width, linesCurrent * fontHeight, firstElement));
				}
				listBuilder.addAll(getNextElements(gui, width, emptyPageHeight, textLines, fontHeight, linesCurrent));

				return listBuilder.build();
			}
		}

		private List<BaseElementWrapper<TextElement>> getNextElements(GuiManual gui, int width, int emptyPageHeight, List<String> textLines, int fontHeight, int linesCurrent) {
			int skipLines = linesCurrent;
			List<BaseElementWrapper<TextElement>> nextElements = new ArrayList<>();
			int maxLinesOnPage = emptyPageHeight / fontHeight;
			while (textLines.size() - skipLines > 0) {
				int linesToAdd = Math.min(textLines.size() - skipLines, maxLinesOnPage);

				TextElement textElement = new TextElement(textLines.stream().skip(skipLines).limit(linesToAdd).collect(Collectors.joining("\n")));
				nextElements.add(new TextElementWrapper(gui, 0, width, linesToAdd * fontHeight, textElement));
				skipLines += linesToAdd;
			}
			return nextElements;
		}
	}
}
