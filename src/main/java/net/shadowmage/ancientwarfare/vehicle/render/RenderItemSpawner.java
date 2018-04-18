package net.shadowmage.ancientwarfare.vehicle.render;

import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.model.IModelState;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.types.VehicleType;

public class RenderItemSpawner implements IItemRenderer {
	@Override
	public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
		int level = 1;
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("spawnData")) {
			NBTTagCompound tag = stack.getTagCompound().getCompoundTag("spawnData");
			level = tag.getInteger("level");
		}

		VehicleBase vehicle = VehicleType.getVehicleForType(Minecraft.getMinecraft().world, stack.getItemDamage(), level);

		if (vehicle == null) {
			return;
		}

		GlStateManager.pushMatrix();

		float baseScale = 0.6F;
		if (transformType == ItemCameraTransforms.TransformType.GUI) {
			baseScale = 0.6F;
		}

		float scale = baseScale / Math.max(vehicle.width, vehicle.height);
		GlStateManager.translate(0.5, 0.175, 0.5);
		GlStateManager.scale(scale, scale, scale);
		if (transformType == ItemCameraTransforms.TransformType.GUI) {
			GlStateManager.rotate(135, 0, 1, 0);
			GlStateManager.rotate(-20, 1, 0, 0);
			GlStateManager.rotate(20, 0, 0, 1);
		}

		RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
		rendermanager.renderEntity(vehicle, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);

		if (transformType != ItemCameraTransforms.TransformType.GROUND && transformType != ItemCameraTransforms.TransformType.FIXED) {
			GlStateManager.enableRescaleNormal();
			GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
			GlStateManager.disableTexture2D();
			GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
			GlStateManager.disableLighting();
		}

		//Some entities like the ender dragon modify the blend state which if not corrected like this breaks inventory rendering.
		GlStateManager.enableBlend();
		GlStateManager
				.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
						GlStateManager.DestFactor.ZERO);
		GlStateManager.popMatrix();
	}

	@Override
	public IModelState getTransforms() {
		return TransformUtils.DEFAULT_ITEM;
	}

	@Override
	public boolean isAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}
}
