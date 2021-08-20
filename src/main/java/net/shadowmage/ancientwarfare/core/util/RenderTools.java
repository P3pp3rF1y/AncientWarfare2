package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;

@SideOnly(Side.CLIENT)
public class RenderTools {
	private RenderTools() {}

	public static void setFullColorLightmap() {
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0.f, 240.f);
	}

	/*
	 * @param textureWidth  texture width
	 * @param textureHeight texture height
	 * @param texStartX     pixel start U
	 * @param texStartY     pixel start V
	 * @param texUsedWidth  pixel U width (width of used tex in pixels)
	 * @param texUsedHeight pixel V height (height of used tex in pixels)
	 * @param renderStartX  render position x
	 * @param renderStartY  render position y
	 * @param renderHeight  render height
	 * @param renderWidth   render width
	 */
	public static void renderQuarteredTexture(int textureWidth, int textureHeight, int texStartX, int texStartY, int texUsedWidth, int texUsedHeight, int renderStartX, int renderStartY, int renderWidth, int renderHeight) {
		//perspective percent x, y
		float perX = 1.f / ((float) textureWidth);
		float perY = 1.f / ((float) textureHeight);
		float texMinX = ((float) texStartX) * perX;
		float texMinY = ((float) texStartY) * perY;
		float texMaxX = (float) (texStartX + texUsedWidth) * perX;
		float texMaxY = (float) (texStartY + texUsedHeight) * perY;
		float halfWidth = (((float) renderWidth) / 2.f) * perX;
		float halfHeight = (((float) renderHeight) / 2.f) * perY;
		float halfRenderWidth = ((float) renderWidth) * 0.5f;
		float halfRenderHeight = ((float) renderHeight) * 0.5f;

		//draw top-left quadrant
		renderTexturedQuad(renderStartX, renderStartY, renderStartX + halfRenderWidth, renderStartY + halfRenderHeight, texMinX, texMinY, texMinX + halfWidth, texMinY + halfHeight);

		//draw top-right quadrant
		renderTexturedQuad(renderStartX + halfRenderWidth, renderStartY, renderStartX + halfRenderWidth * 2, renderStartY + halfRenderHeight, texMaxX - halfWidth, texMinY, texMaxX, texMinY + halfHeight);

		//draw bottom-left quadrant
		renderTexturedQuad(renderStartX, renderStartY + halfRenderHeight, renderStartX + halfRenderWidth, renderStartY + halfRenderHeight * 2, texMinX, texMaxY - halfHeight, texMinX + halfWidth, texMaxY);

		//draw bottom-right quadrant
		renderTexturedQuad(renderStartX + halfRenderWidth, renderStartY + halfRenderHeight, renderStartX + halfRenderWidth * 2, renderStartY + halfRenderHeight * 2, texMaxX - halfWidth, texMaxY - halfHeight, texMaxX, texMaxY);
	}

	public static void renderTexturedQuad(float x1, float y1, float x2, float y2, float u1, float v1, float u2, float v2) {
		GlStateManager.glBegin(GL11.GL_QUADS);
		GlStateManager.glTexCoord2f(u1, v1);
		GL11.glVertex2f(x1, y1);
		GlStateManager.glTexCoord2f(u1, v2);
		GL11.glVertex2f(x1, y2);
		GlStateManager.glTexCoord2f(u2, v2);
		GL11.glVertex2f(x2, y2);
		GlStateManager.glTexCoord2f(u2, v1);
		GL11.glVertex2f(x2, y1);
		GlStateManager.glEnd();
	}

	public static void renderColoredQuad(int renderStartX, int renderStartY, int renderWidth, int renderHeight, float colorRed, float colorGreen, float colorBlue) {
		GlStateManager.disableTexture2D();
		GlStateManager.color(colorRed, colorGreen, colorBlue);
		GlStateManager.glBegin(GL11.GL_QUADS);
		GL11.glVertex2f(renderStartX, renderStartY);
		GL11.glVertex2f(renderStartX, (float) renderStartY + renderHeight);
		GL11.glVertex2f((float) renderStartX + renderWidth, (float) renderStartY + renderHeight);
		GL11.glVertex2f((float) renderStartX + renderWidth, renderStartY);
		GlStateManager.color(1, 1, 1);
		GlStateManager.glEnd();
		GlStateManager.enableTexture2D();
	}

	/*
	 * render a BB as a set of enlarged cuboids.
	 */
	public static void drawOutlinedBoundingBox2(AxisAlignedBB bb, float r, float g, float b, float width) {
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(r, g, b, 0.4F);
		GlStateManager.bindTexture(0);
		float hw = width / 2;
		drawCuboid((float) bb.minX, (float) bb.minY - hw, (float) bb.minZ - hw, (float) bb.maxX, (float) bb.minY + hw, (float) bb.minZ + hw);
		drawCuboid((float) bb.minX, (float) bb.maxY - hw, (float) bb.minZ - hw, (float) bb.maxX, (float) bb.maxY + hw, (float) bb.minZ + hw);
		drawCuboid((float) bb.minX, (float) bb.minY - hw, (float) bb.maxZ - hw, (float) bb.maxX, (float) bb.minY + hw, (float) bb.maxZ + hw);
		drawCuboid((float) bb.minX, (float) bb.maxY - hw, (float) bb.maxZ - hw, (float) bb.maxX, (float) bb.maxY + hw, (float) bb.maxZ + hw);

		drawCuboid((float) bb.minX - hw, (float) bb.minY, (float) bb.minZ - hw, (float) bb.minX + hw, (float) bb.maxY, (float) bb.minZ + hw);
		drawCuboid((float) bb.maxX - hw, (float) bb.minY, (float) bb.minZ - hw, (float) bb.maxX + hw, (float) bb.maxY, (float) bb.minZ + hw);
		drawCuboid((float) bb.minX - hw, (float) bb.minY, (float) bb.maxZ - hw, (float) bb.minX + hw, (float) bb.maxY, (float) bb.maxZ + hw);
		drawCuboid((float) bb.maxX - hw, (float) bb.minY, (float) bb.maxZ - hw, (float) bb.maxX + hw, (float) bb.maxY, (float) bb.maxZ + hw);

		drawCuboid((float) bb.minX - hw, (float) bb.minY - hw, (float) bb.minZ, (float) bb.minX + hw, (float) bb.minY + hw, (float) bb.maxZ);
		drawCuboid((float) bb.minX - hw, (float) bb.maxY - hw, (float) bb.minZ, (float) bb.minX + hw, (float) bb.maxY + hw, (float) bb.maxZ);
		drawCuboid((float) bb.maxX - hw, (float) bb.minY - hw, (float) bb.minZ, (float) bb.maxX + hw, (float) bb.minY + hw, (float) bb.maxZ);
		drawCuboid((float) bb.maxX - hw, (float) bb.maxY - hw, (float) bb.minZ, (float) bb.maxX + hw, (float) bb.maxY + hw, (float) bb.maxZ);
		GlStateManager.disableBlend();
	}

	private static void drawCuboid(float x, float y, float z, float mx, float my, float mz) {
		GlStateManager.glBegin(GL11.GL_QUADS);
		//z+ side
		GlStateManager.glNormal3f(0, 0, 1);
		GlStateManager.glVertex3f(x, my, mz);
		GlStateManager.glVertex3f(x, y, mz);
		GlStateManager.glVertex3f(mx, y, mz);
		GlStateManager.glVertex3f(mx, my, mz);

		//x+ side
		GlStateManager.glNormal3f(1, 0, 0);
		GlStateManager.glVertex3f(mx, my, mz);
		GlStateManager.glVertex3f(mx, y, mz);
		GlStateManager.glVertex3f(mx, y, z);
		GlStateManager.glVertex3f(mx, my, z);

		//y+ side
		GlStateManager.glNormal3f(0, 1, 0);
		GlStateManager.glVertex3f(x, my, z);
		GlStateManager.glVertex3f(x, my, mz);
		GlStateManager.glVertex3f(mx, my, mz);
		GlStateManager.glVertex3f(mx, my, z);

		//z- side
		GlStateManager.glNormal3f(0, 0, -1);
		GlStateManager.glVertex3f(x, my, z);
		GlStateManager.glVertex3f(mx, my, z);
		GlStateManager.glVertex3f(mx, y, z);
		GlStateManager.glVertex3f(x, y, z);

		//x-side
		GlStateManager.glNormal3f(-1, 0, 0);
		GlStateManager.glVertex3f(x, y, mz);
		GlStateManager.glVertex3f(x, my, mz);
		GlStateManager.glVertex3f(x, my, z);
		GlStateManager.glVertex3f(x, y, z);

		//y- side
		GlStateManager.glNormal3f(0, -1, 0);
		GlStateManager.glVertex3f(x, y, z);
		GlStateManager.glVertex3f(mx, y, z);
		GlStateManager.glVertex3f(mx, y, mz);
		GlStateManager.glVertex3f(x, y, mz);

		GlStateManager.glEnd();
	}

	/*
	 * Renders a white point in center, and RGB lines/points for X,Y,Z axis'
	 */
	public static void renderOrientationPoints(float colorMult) {
		GlStateManager.pushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GL11.glPointSize(3.f);

		GlStateManager.color(colorMult, colorMult, colorMult, 1.f);

		//draw origin point
		GlStateManager.glBegin(GL11.GL_POINTS);
		GlStateManager.glVertex3f(0, 0, 0);
		GlStateManager.glEnd();

		GlStateManager.color(colorMult, 0, 0, 1.f);//red for x axis
		GlStateManager.glBegin(GL11.GL_LINES);
		GlStateManager.glVertex3f(0, 0, 0);
		GlStateManager.glVertex3f(1, 0, 0);
		GlStateManager.glEnd();

		GlStateManager.glBegin(GL11.GL_POINTS);
		GlStateManager.glVertex3f(1, 0, 0);
		GlStateManager.glEnd();

		GlStateManager.color(0, colorMult, 0, 1.f);//green for y axis
		GlStateManager.glBegin(GL11.GL_LINES);
		GlStateManager.glVertex3f(0, 0, 0);
		GlStateManager.glVertex3f(0, 1, 0);
		GlStateManager.glEnd();

		GlStateManager.glBegin(GL11.GL_POINTS);
		GlStateManager.glVertex3f(0, 1, 0);
		GlStateManager.glEnd();

		GlStateManager.color(0, 0, colorMult, 1.f);//blue for z axis
		GlStateManager.glBegin(GL11.GL_LINES);
		GlStateManager.glVertex3f(0, 0, 0);
		GlStateManager.glVertex3f(0, 0, 1);
		GlStateManager.glEnd();

		GlStateManager.glBegin(GL11.GL_POINTS);
		GlStateManager.glVertex3f(0, 0, 1);
		GlStateManager.glEnd();

		GlStateManager.color(1.f, 1.f, 1.f, 1.f);
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}

	/*
	 * draw a player-position-normalized bounding box (can only be called during worldRender)
	 */
	public static void drawTopSideOverlay(AxisAlignedBB bb, Color color) {
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GlStateManager.pushMatrix();
		GlStateManager.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() / 255f);
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tess.getBuffer();
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		float yOffset = 0.01f; //offset to prevent z fighting
		bufferBuilder.pos(bb.minX, bb.maxY + yOffset, bb.maxZ).tex(0, 0).endVertex();
		bufferBuilder.pos(bb.maxX, bb.maxY + yOffset, bb.maxZ).tex(0, 1).endVertex();
		bufferBuilder.pos(bb.maxX, bb.maxY + yOffset, bb.minZ).tex(1, 1).endVertex();
		bufferBuilder.pos(bb.minX, bb.maxY + yOffset, bb.minZ).tex(1, 0).endVertex();
		tess.draw();
		GlStateManager.popMatrix();

		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public static void drawOutlinedBoundingBox(AxisAlignedBB bb, Color color) {
		drawOutlinedBoundingBox(bb, color, false);
	}

	public static void drawOutlinedBoundingBox(AxisAlignedBB bb, Color color, boolean disableDepth) {
		drawOutlinedBoundingBox(bb, color.getRed(), color.getGreen(), color.getBlue(), disableDepth);
	}

	public static void drawOutlinedBoundingBox(AxisAlignedBB bb, float r, float g, float b) {
		drawOutlinedBoundingBox(bb, r, g, b, false);
	}

	private static void drawOutlinedBoundingBox(AxisAlignedBB bb, float r, float g, float b, boolean disabledDepth) {

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(r, g, b, 0.4F);
		GlStateManager.glLineWidth(8.0F);
		GlStateManager.disableTexture2D();
		if (disabledDepth) {
			GlStateManager.disableDepth();
		}
		GlStateManager.depthFunc(519);
		GlStateManager.depthMask(false);

		Tessellator tess = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tess.getBuffer();
		drawBottomOutline(bb, bufferBuilder);
		tess.draw();
		drawTopOutline(bb, bufferBuilder);
		tess.draw();
		bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).endVertex();
		bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
		bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
		bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
		bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
		bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
		bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
		bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
		tess.draw();

		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	private static void drawTopOutline(AxisAlignedBB bb, BufferBuilder bufferBuilder) {
		bufferBuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
		bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
		bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
		bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
		bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
		bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
	}

	private static void drawBottomOutline(AxisAlignedBB bb, BufferBuilder bufferBuilder) {
		bufferBuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
		bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).endVertex();
		bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
		bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
		bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
		bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).endVertex();
	}

	/*
	 * @param bb
	 * @param player
	 * @param partialTick
	 * @return
	 */
	public static AxisAlignedBB adjustBBForPlayerPos(AxisAlignedBB bb, EntityPlayer player, float partialTick) {
		double x = getRenderOffsetX(player, partialTick);
		double y = getRenderOffsetY(player, partialTick);
		double z = getRenderOffsetZ(player, partialTick);
		return bb.offset(-x, -y, -z);
	}

	public static double getRenderOffsetX(EntityPlayer player, float partialTick) {
		return player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTick;
	}

	public static double getRenderOffsetY(EntityPlayer player, float partialTick) {
		return player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTick;
	}

	public static double getRenderOffsetZ(EntityPlayer player, float partialTick) {
		return player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTick;
	}

	@SuppressWarnings("squid:S2259")
	@SideOnly(Side.CLIENT)
	public static void renderTESR(@Nullable TileEntity te, BlockPos pos) {
		TileEntitySpecialRenderer<TileEntity> renderer = TileEntityRendererDispatcher.instance.getRenderer(te);
		if (renderer != null) {
			World dispatcherWorld = TileEntityRendererDispatcher.instance.world;
			//noinspection ConstantConditions
			TileEntityRendererDispatcher.instance.setWorld(te.getWorld());
			renderer.render(te, pos.getX(), pos.getY(), pos.getZ(), 0, -1, 1);
			TileEntityRendererDispatcher.instance.setWorld(dispatcherWorld);
		}
	}
}
