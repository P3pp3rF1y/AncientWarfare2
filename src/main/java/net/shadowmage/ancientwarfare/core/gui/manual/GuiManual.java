package net.shadowmage.ancientwarfare.core.gui.manual;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.container.ContainerManual;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.TextureData;
import net.shadowmage.ancientwarfare.core.gui.elements.ImageButton;
import net.shadowmage.ancientwarfare.core.gui.manual.elements.BaseElementWrapper;
import net.shadowmage.ancientwarfare.core.manual.IContentElement;
import net.shadowmage.ancientwarfare.core.manual.ManualContentRegistry;
import net.shadowmage.ancientwarfare.core.util.RenderTools;

import java.awt.*;
import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiManual extends GuiContainerBase<ContainerManual> {

	private static final int BOOK_WIDTH = 412;
	private static final int PAGE_WIDTH = BOOK_WIDTH / 2;
	private static final int BOOK_HEIGHT = 254;
	public static final int FOOTER_HEIGHT = 14;
	private static final ResourceLocation MANUAL_TEXTURE = new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/gui/manual.png");
	private static final int HORIZONTAL_PAGE_PADDING = 8;
	private static final int VERTICAL_PAGE_PADDING = 7;
	private static final String INITIAL_CATEGORY = "table_of_contents";
	private List<List<BaseElementWrapper>> pageElements;
	private int currentPageIndex = 0;
	private Page leftPage;
	private Page rightPage;
	private ImageButton backButton;
	private String currentCategory = INITIAL_CATEGORY;
	private String previousCategory = "";

	public GuiManual(ContainerBase container) {
		super(container, BOOK_WIDTH, BOOK_HEIGHT);

		loadCategory(currentCategory);
	}

	private void loadCategory(String category) {
		List<IContentElement> elements = ManualContentRegistry.getCategoryContent(category);

		int innerPageWidth = BOOK_WIDTH / 2 - HORIZONTAL_PAGE_PADDING - 2 * Page.getPadding();
		pageElements = ElementWrapperFactory.getPagedWrappedContent(this, elements, innerPageWidth, getInnerPageHeight());
	}

	private int getInnerPageHeight() {
		return BOOK_HEIGHT - 2 * VERTICAL_PAGE_PADDING - 2 * Page.getPadding() - FOOTER_HEIGHT;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		Minecraft.getMinecraft().renderEngine.bindTexture(MANUAL_TEXTURE);
		RenderTools.renderQuarteredTexture(512, 512, 0, 0, BOOK_WIDTH, BOOK_HEIGHT, width / 2 - xSize / 2, (height / 2) - (ySize / 2), xSize, ySize);
	}

	@Override
	public void initElements() {
		int pageWidth = PAGE_WIDTH - HORIZONTAL_PAGE_PADDING;
		int pageHeight = BOOK_HEIGHT - 2 * VERTICAL_PAGE_PADDING;
		leftPage = new Page(this, HORIZONTAL_PAGE_PADDING, VERTICAL_PAGE_PADDING, pageWidth, pageHeight,
				new TextureData(MANUAL_TEXTURE, 512, 512, HORIZONTAL_PAGE_PADDING, BOOK_HEIGHT + VERTICAL_PAGE_PADDING, pageWidth, pageHeight), true);
		addGuiElement(leftPage);

		rightPage = new Page(this, PAGE_WIDTH, VERTICAL_PAGE_PADDING, PAGE_WIDTH - HORIZONTAL_PAGE_PADDING, BOOK_HEIGHT - 2 * VERTICAL_PAGE_PADDING,
				new TextureData(MANUAL_TEXTURE, 512, 512, PAGE_WIDTH, BOOK_HEIGHT + VERTICAL_PAGE_PADDING, pageWidth, pageHeight), false);
		addGuiElement(rightPage);
		refreshGui();

		backButton = new ImageButton(-8, 0, 14, 16,
				new TextureData(MANUAL_TEXTURE, 512, 512, 412, 41, 14, 16), Color.GRAY, Color.CYAN) {
			@Override
			protected void onPressed(int mButton) {
				setCurrentCategory(previousCategory);
			}
		};
		addGuiElement(backButton);
	}

	private List<BaseElementWrapper> getPageElements(int pageNumber) {
		return pageElements.size() > pageNumber ? pageElements.get(pageNumber) : Collections.emptyList();
	}

	@Override
	public void setupElements() {
		leftPage.updateContentElements(getPageElements(getCurrentPageIndex()));
		rightPage.updateContentElements(getPageElements(getCurrentPageIndex() + 1));
		backButton.setVisible(!currentCategory.equals(INITIAL_CATEGORY));
	}

	public int getCurrentPageIndex() {
		return currentPageIndex;
	}

	public void setCurrentPageIndex(int currentPageIndex) {
		this.currentPageIndex = currentPageIndex;
		refreshGui();
	}

	public int getPageCount() {
		return pageElements.size();
	}

	public void setCurrentCategory(String category) {
		currentPageIndex = 0;
		previousCategory = currentCategory;
		currentCategory = category;
		loadCategory(category);
		refreshGui();
	}
}
