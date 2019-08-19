package net.shadowmage.ancientwarfare.structure.render.statue;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.shadowmage.ancientwarfare.core.util.MathUtils;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.tile.EntityStatueInfo;
import net.shadowmage.ancientwarfare.structure.tile.TileStatue;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class StatueRenderer extends TileEntitySpecialRenderer<TileStatue> {
	@Override
	public void render(TileStatue te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		EntityStatueInfo statueInfo = te.getEntityStatueInfo();

		if (statueInfo.getRenderType() == EntityStatueInfo.RenderType.MODEL) {
			StatueEntityRegistry.StatueEntity statueEntity = StatueEntityRegistry.getStatueEntity(statueInfo.getStatueEntityName());
			EntityLivingBase entity = statueEntity.instantiateEntity(Minecraft.getMinecraft().world);
			RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();

			Render<EntityLivingBase> render = rendermanager.getEntityRenderObject(entity);
			RenderLivingBase livingRender = (RenderLivingBase) render; //TODO see if this is really safe to assume - EntityLivingBase's children always rendered using RenderLivingBase's children

			IStatueModel statueModel = statueEntity.getStatueModel();
			applyPartTransforms(statueModel, statueInfo.getPartTransforms());

			doRender(livingRender, statueModel, statueInfo.getOverallTransform(), entity, (float) x + 0.5f, (float) y, (float) z + 0.5f, getEntityTexture(livingRender, entity), te.getPrimaryFacing());
		}
	}

	private void applyPartTransforms(IStatueModel statueModel, Map<String, EntityStatueInfo.Transform> partTransforms) {
		for(String partName : statueModel.getModelPartNames()) {
			ModelRenderer part = statueModel.getModelPart(partName);
			EntityStatueInfo.Transform transform = partTransforms.getOrDefault(partName, new EntityStatueInfo.Transform());
			EntityStatueInfo.Transform baseTransform = statueModel.getBaseTransforms().getOrDefault(partName, new EntityStatueInfo.Transform());
			part.offsetX = baseTransform.getOffsetX() + transform.getOffsetX();
			part.offsetY = baseTransform.getOffsetY() + transform.getOffsetY();
			part.offsetZ = baseTransform.getOffsetZ() + transform.getOffsetZ();

			part.rotateAngleX = baseTransform.getRotationX() + transform.getRotationX();
			part.rotateAngleY = baseTransform.getRotationY() + transform.getRotationY();
			part.rotateAngleZ = baseTransform.getRotationZ() + transform.getRotationZ();
		}
	}

	private void doRender(RenderLivingBase<EntityLivingBase> renderer, IStatueModel statueModel, EntityStatueInfo.Transform overallTransform, EntityLivingBase entity, float x, float y, float z, ResourceLocation entityTexture, EnumFacing primaryFacing) {
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();

		try {
			float f = entity.renderYawOffset + overallTransform.getRotationY() + primaryFacing.getHorizontalAngle();

			GlStateManager.translate(x + overallTransform.getOffsetX(), y + overallTransform.getOffsetY(), z + overallTransform.getOffsetZ());
			GlStateManager.rotate(180.0F - f, 0.0F, 1.0F, 0.0F);
			if (!MathUtils.epsilonEquals(overallTransform.getRotationX(), 0)) {
				GlStateManager.rotate(overallTransform.getRotationX(), 1, 0, 0);
			}
			if (!MathUtils.epsilonEquals(overallTransform.getRotationZ(), 0)) {
				GlStateManager.rotate(overallTransform.getRotationZ(), 0, 0, 1);
			}
			float scale = overallTransform.getScale();
			if (!MathUtils.epsilonEquals(scale, 1)) {
				GlStateManager.scale(scale, scale, scale);
			}
			float f4 = renderer.prepareScale(entity, 0);
			float limbSwingAmount = 0.0F;
			float limbSwing = 0.0F;

			if (!entity.isRiding()) {
				limbSwingAmount = entity.prevLimbSwingAmount;
				limbSwing = entity.limbSwing - entity.limbSwingAmount;

				if (entity.isChild()) {
					limbSwing *= 3.0F;
				}

				if (limbSwingAmount > 1.0F) {
					limbSwingAmount = 1.0F;
				}
			}

			GlStateManager.enableAlpha();
			statueModel.getModel().setLivingAnimations(entity, limbSwing, limbSwingAmount, 0);

			renderModel(statueModel, entityTexture, f4);
			GlStateManager.depthMask(true);
			GlStateManager.disableRescaleNormal();
		}
		catch (Exception exception) {
			AncientWarfareStructure.LOG.error("Couldn't render entity", (Throwable) exception);
		}

		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.enableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GlStateManager.enableCull();
		GlStateManager.popMatrix();
	}

	private static final Method GET_ENTITY_TEXTURE = ObfuscationReflectionHelper.findMethod(Render.class, "func_110775_a", ResourceLocation.class, Entity.class);

	private ResourceLocation getEntityTexture(Render render, Entity entity) {
		try {
			return (ResourceLocation) GET_ENTITY_TEXTURE.invoke(render, entity);
		}
		catch (IllegalAccessException | InvocationTargetException e) {
			AncientWarfareStructure.LOG.error("Error getting entity texture: ", e);
			return new ResourceLocation("");
		}
	}

	private void renderModel(IStatueModel model, ResourceLocation entityTexture, float scale) {
		if (!bindEntityTexture(entityTexture)) {
			return;
		}
		GlStateManager.pushMatrix();
		model.render(scale);
		GlStateManager.popMatrix();
	}

	private boolean bindEntityTexture(@Nullable ResourceLocation resourcelocation) {
		if (resourcelocation == null) {
			return false;
		} else {
			bindTexture(resourcelocation);
			return true;
		}
	}

	public void bindTexture(ResourceLocation location) {
		Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(location);
	}
}
