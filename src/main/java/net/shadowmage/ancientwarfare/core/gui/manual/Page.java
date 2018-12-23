package net.shadowmage.ancientwarfare.core.gui.manual;

import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.TextureData;
import net.shadowmage.ancientwarfare.core.gui.elements.Composite;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.manual.elements.BaseElementWrapper;

import java.util.List;

import static net.shadowmage.ancientwarfare.core.gui.manual.GuiManual.FOOTER_HEIGHT;

public class Page extends Composite {
	private static final int PADDING = 7;
	private final boolean leftPage;
	private final TextureData textureData;

	public Page(GuiContainerBase gui, int topLeftX, int topLeftY, int width, int height, TextureData textureData, boolean leftPage) {
		super(gui, topLeftX, topLeftY, width, height);
		this.textureData = textureData;
		this.leftPage = leftPage;
	}

	public static int getPadding() {
		return PADDING;
	}

	public void updateContentElements(List<BaseElementWrapper> contentElements) {
		clearElements();
		contentElements.forEach(this::addGuiElement);
		addPaging();
	}

	@Override
	public GuiManual getGui() {
		return (GuiManual) super.getGui();
	}

	@Override
	public void updateGuiPosition(int guiLeft, int guiTop) {
		super.updateGuiPosition(guiLeft + PADDING, guiTop + PADDING);
	}

	private void addPaging() {
		addGuiElement(new Paging(getGui(), 0, height - 2 * PADDING - FOOTER_HEIGHT, width - 2 * PADDING, FOOTER_HEIGHT,
				textureData.getTexture(), leftPage));
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		for (GuiElement element : this.elements) {
			element.render(mouseX, mouseY, partialTick);
		}
	}
}
