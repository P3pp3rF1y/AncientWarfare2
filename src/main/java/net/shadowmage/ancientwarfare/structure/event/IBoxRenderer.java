package net.shadowmage.ancientwarfare.structure.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.util.RenderTools;

import java.awt.*;

/*
 * Created by Olivier on 05/02/2015.
 */
public interface IBoxRenderer {
	void renderBox(EntityPlayer player, EnumHand hand, ItemStack itemStack, float partialTick);

	final class Util {
		private Util() {
		}

		@SideOnly(Side.CLIENT)
		public static void renderBoundingBoxTopSide(EntityPlayer player, BlockPos min, BlockPos max, float delta, Color color) {
			AxisAlignedBB bb = new AxisAlignedBB(min.getX(), min.getY(), min.getZ(), max.getX() + 1, max.getY() + 1, max.getZ() + 1);
			bb = RenderTools.adjustBBForPlayerPos(bb, player, delta);
			RenderTools.drawTopSideOverlay(bb, color);
		}

		@SideOnly(Side.CLIENT)
		public static void renderBoundingBox(EntityPlayer player, BlockPos min, BlockPos max, float delta, Color color) {
			AxisAlignedBB bb = new AxisAlignedBB(min.getX(), min.getY(), min.getZ(), max.getX() + 1, max.getY() + 1, max.getZ() + 1);
			bb = RenderTools.adjustBBForPlayerPos(bb, player, delta);
			RenderTools.drawOutlinedBoundingBox(bb, color);
		}

		@SideOnly(Side.CLIENT)
		public static void renderBoundingBox(EntityPlayer player, BlockPos min, BlockPos max, float delta) {
			AxisAlignedBB bb = new AxisAlignedBB(min.getX(), min.getY(), min.getZ(), max.getX() + 1, max.getY() + 1, max.getZ() + 1);
			bb = RenderTools.adjustBBForPlayerPos(bb, player, delta);
			RenderTools.drawOutlinedBoundingBox(bb, 1.f, 1.f, 1.f);
		}

		@SideOnly(Side.CLIENT)
		public static void renderBoundingBox(EntityPlayer player, BlockPos min, BlockPos max, float delta, float r, float g, float b) {
			AxisAlignedBB bb = new AxisAlignedBB(min.getX(), min.getY(), min.getZ(), max.getX() + 1, max.getY() + 1, max.getZ() + 1);
			bb = RenderTools.adjustBBForPlayerPos(bb, player, delta);
			RenderTools.drawOutlinedBoundingBox(bb, r, g, b);
		}
	}
}
