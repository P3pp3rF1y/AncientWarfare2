package net.shadowmage.ancientwarfare.structure.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.shadowmage.ancientwarfare.structure.tile.TileStake;

import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.FACING;

public class StakeRenderer extends TileEntitySpecialRenderer<TileStake> {
	private static Entity entity = null;

	@Override
	public void render(TileStake te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		IBlockState state = te.getWorld().getBlockState(te.getPos());

		EnumFacing facing = state.getValue(FACING);

		RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
		te.getRenderEntity().ifPresent(e -> {
			if (te.isEntityOnFire()) {
				e.setFire(1);
			} else {
				e.extinguish();
			}
			e.setRenderYawOffset(facing.getHorizontalAngle());
			e.setRotationYawHead(facing.getHorizontalAngle());
			rendermanager.renderEntity(e, x + 0.5 + facing.getFrontOffsetX() * 0.3, y + 0.6, z + 0.5 + facing.getFrontOffsetZ() * 0.3, facing.getHorizontalAngle(), 1.0F, false);
		});
	}
}