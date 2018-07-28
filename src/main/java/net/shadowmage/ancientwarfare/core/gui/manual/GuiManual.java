package net.shadowmage.ancientwarfare.core.gui.manual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.container.ContainerManual;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.util.RenderTools;

public class GuiManual extends GuiContainerBase<ContainerManual> {

	private static final int BOOK_WIDTH = 411;
	private static final int BOOK_HEIGHT = 199;
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/gui/manual.png");

	public GuiManual(ContainerBase container) {
		super(container, BOOK_WIDTH, BOOK_HEIGHT);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(BACKGROUND_TEXTURE);
		RenderTools.renderQuarteredTexture(512, 512, 0, 0, BOOK_WIDTH, BOOK_HEIGHT, width / 2 - xSize / 2, (height / 2) - (ySize / 2), xSize, ySize);
	}

	@Override
	public void initElements() {

	}

	@Override
	public void setupElements() {

	}
}
