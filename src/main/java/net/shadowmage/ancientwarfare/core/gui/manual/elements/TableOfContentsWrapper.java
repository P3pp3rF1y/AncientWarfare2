package net.shadowmage.ancientwarfare.core.gui.manual.elements;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.TextButton;
import net.shadowmage.ancientwarfare.core.gui.manual.GuiManual;
import net.shadowmage.ancientwarfare.core.gui.manual.IElementWrapperCreator;
import net.shadowmage.ancientwarfare.core.manual.TableOfContentsElement;
import net.shadowmage.ancientwarfare.core.proxy.ClientProxy;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class TableOfContentsWrapper extends BaseElementWrapper<TableOfContentsElement> {
	private static final int SPACING = 5;
	private final List<GuiElement> guiElements = new ArrayList<>();

	public TableOfContentsWrapper(GuiManual gui, int topLeftY, int width, int height, TableOfContentsElement element) {
		super(gui, 0, topLeftY, width, height, element);

		int currentY = topLeftY;
		FontRenderer fontRenderer = ClientProxy.getUnicodeFontRenderer();
		int fontHeight = fontRenderer.FONT_HEIGHT;
		int itemHeight = fontHeight + SPACING;
		for (TableOfContentsElement.TableOfContentsItem item : element.getItems()) {
			addGuiElement(new TextButton(0, currentY, fontRenderer.getStringWidth(item.getText()), fontHeight, item.getText(), Color.BLACK, Color.CYAN) {
				@Override
				protected void onPressed(int mButton) {
					getGui().setCurrentCategory(item.getCategory());
				}
			});
			currentY += itemHeight;
		}

		this.addNewListener(new Listener(Listener.MOUSE_TYPES) {
			@Override
			public boolean onEvent(GuiElement widget, GuiContainerBase.ActivationEvent evt) {
				if ((evt.type & Listener.MOUSE_TYPES) != 0) {
					if (isMouseOverElement(evt.mx, evt.my)) {
						/*
						 * adjust mouse event position for relative to composite
                         */
						int x = evt.mx;
						int y = evt.my;
						for (GuiElement element : guiElements) {
							element.handleMouseInput(evt);
						}
						evt.mx = x;
						evt.my = y;
					} else if (evt.type == Listener.MOUSE_UP) {
						for (GuiElement element : guiElements) {
							element.setSelected(false);
						}
					}
				}
				return true;
			}
		});
	}

	private void addGuiElement(GuiElement guiElement) {
		guiElements.add(guiElement);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		for (GuiElement element : guiElements) {
			element.render(mouseX, mouseY, partialTick);
		}
	}

	@Override
	public void updateGuiPosition(int guiLeft, int guiTop) {
		super.updateGuiPosition(guiLeft, guiTop);
		guiElements.forEach(e -> e.updateGuiPosition(guiLeft, guiTop));
	}

	public static class Creator implements IElementWrapperCreator<TableOfContentsElement> {
		@Override
		public List<BaseElementWrapper<TableOfContentsElement>> construct(GuiManual gui, int topLeftY, int width, int remainingPageHeight, int emptyPageHeight, TableOfContentsElement element) {
			FontRenderer fontRenderer = ClientProxy.getUnicodeFontRenderer();
			int remainingCount = element.getItems().size();
			int remainingHeight = remainingCount * (fontRenderer.FONT_HEIGHT + SPACING);

			if (remainingHeight <= remainingPageHeight) {
				return ImmutableList.of(new TableOfContentsWrapper(gui, topLeftY, width, remainingHeight, element));
			}

			int pageHeight = remainingPageHeight;
			List<BaseElementWrapper<TableOfContentsElement>> ret = new ArrayList<>();
			int yCoord = topLeftY;
			while (remainingCount > 0) {
				int renderCount = Math.min(remainingCount, pageHeight / (fontRenderer.FONT_HEIGHT + SPACING));

				TableOfContentsElement toc = new TableOfContentsElement(element.getItems().stream().skip(element.getItems().size() - remainingCount).limit(renderCount).collect(Collectors.toList()));
				ret.add(new TableOfContentsWrapper(gui, yCoord, width, renderCount * (fontRenderer.FONT_HEIGHT + SPACING), toc));

				remainingCount -= renderCount;
				pageHeight = emptyPageHeight;
				yCoord = 0;
			}

			return ret;
		}
	}
}
