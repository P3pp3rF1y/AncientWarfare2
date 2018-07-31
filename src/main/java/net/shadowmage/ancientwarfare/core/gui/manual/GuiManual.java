package net.shadowmage.ancientwarfare.core.gui.manual;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.container.ContainerManual;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.manual.IContentElement;
import net.shadowmage.ancientwarfare.core.manual.TextElement;
import net.shadowmage.ancientwarfare.core.util.RenderTools;

import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiManual extends GuiContainerBase<ContainerManual> {

	private static final int BOOK_WIDTH = 412;
	private static final int PAGE_WIDTH = BOOK_WIDTH / 2;
	private static final int BOOK_HEIGHT = 200;
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/gui/manual.png");
	private static final int HORIZONTAL_PAGE_PADDING = 8;
	private static final int VERTICAL_PAGE_PADDING = 7;
	List<List<BaseElementWrapper>> pageElements;

	public GuiManual(ContainerBase container) {
		super(container, BOOK_WIDTH, BOOK_HEIGHT);

		TextElement paragraph1 = new TextElement("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate.");
		TextElement paragraph2 = new TextElement("Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores.");
		TextElement paragraph3 = new TextElement("Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae.");
		TextElement paragraph4 = new TextElement("Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae.");
		TextElement paragraph5 = new TextElement("Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa.");

		List<IContentElement> elements = ImmutableList.of(paragraph1, paragraph2, paragraph3, paragraph4, paragraph5);

		int innerPageWidth = BOOK_WIDTH / 2 - HORIZONTAL_PAGE_PADDING - 2 * Page.getPadding();
		int innerPageHeight = BOOK_HEIGHT - 2 * VERTICAL_PAGE_PADDING - 2 * Page.getPadding();
		pageElements = ElementWrapperFactory.getPagedWrappedContent(elements, innerPageWidth, innerPageHeight);

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		Minecraft.getMinecraft().renderEngine.bindTexture(BACKGROUND_TEXTURE);
		RenderTools.renderQuarteredTexture(512, 512, 0, 0, BOOK_WIDTH, BOOK_HEIGHT, width / 2 - xSize / 2, (height / 2) - (ySize / 2), xSize, ySize);
	}

	@Override
	public void initElements() {
		Page leftPage = new Page(HORIZONTAL_PAGE_PADDING, VERTICAL_PAGE_PADDING, PAGE_WIDTH - HORIZONTAL_PAGE_PADDING, BOOK_HEIGHT - 2 * VERTICAL_PAGE_PADDING, getPageElements(0));
		leftPage.setBackground(BACKGROUND_TEXTURE, HORIZONTAL_PAGE_PADDING, BOOK_HEIGHT + VERTICAL_PAGE_PADDING);
		addGuiElement(leftPage);

		Page rightPage = new Page(PAGE_WIDTH, VERTICAL_PAGE_PADDING, PAGE_WIDTH - HORIZONTAL_PAGE_PADDING, BOOK_HEIGHT - 2 * VERTICAL_PAGE_PADDING, getPageElements(1));
		rightPage.setBackground(BACKGROUND_TEXTURE, PAGE_WIDTH, BOOK_HEIGHT + VERTICAL_PAGE_PADDING);
		addGuiElement(rightPage);
	}

	private List<BaseElementWrapper> getPageElements(int pageNumber) {
		return pageElements.size() > pageNumber ? pageElements.get(pageNumber) : Collections.emptyList();
	}

	@Override
	public void setupElements() {

	}
}
