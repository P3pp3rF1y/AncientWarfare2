package net.shadowmage.ancientwarfare.structure.render;

import codechicken.lib.render.CCModelState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import net.shadowmage.ancientwarfare.structure.block.BlockMulti;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockCoffin;
import net.shadowmage.ancientwarfare.structure.model.ModelCoffin;
import net.shadowmage.ancientwarfare.structure.tile.TileCoffin;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class CoffinRenderer extends TileEntitySpecialRenderer<TileCoffin> implements IItemRenderer {
	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.MOD_ID + ":structure/coffin", "normal");
	private static final ModelCoffin COFFIN_MODEL = new ModelCoffin();

	private static final Map<Integer, ResourceLocation> TEXTURES = new HashMap<>();

	static {
		for (int id = 1; id <= 6; id++) {
			TEXTURES.put(id, new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/structure/coffin_" + id + ".png"));
		}
	}

	private static final IModelState TRANSFORMS;

	static {
		Map<ItemCameraTransforms.TransformType, TRSRTransformation> map;
		TRSRTransformation thirdPerson;

		map = new EnumMap<>(ItemCameraTransforms.TransformType.class);
		thirdPerson = TransformUtils.create(0F, 3F, 5F, 75F, 180F, 180F, 0.015F);
		map.put(ItemCameraTransforms.TransformType.GUI, TransformUtils.create(6F, 5F, 0F, 60F, 225F, 200F, 0.035F));
		map.put(ItemCameraTransforms.TransformType.GROUND, TransformUtils.create(0F, 8F, 0F, 0F, 0F, 180F, 0.025F));
		map.put(ItemCameraTransforms.TransformType.FIXED, TransformUtils.create(0F, -4F, -12F, 90F, 180F, 0F, 0.035F));
		map.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdPerson);
		map.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, TransformUtils.flipLeft(thirdPerson));
		map.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, TransformUtils.create(25F, -15F, -10F, 50F, 170F, 170F, 0.08F));
		map.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, TransformUtils.create(25F, -15F, -10F, 50F, 170F, 170F, 0.08F));
		TRANSFORMS = new CCModelState(map);
	}

	@Override
	public void render(TileCoffin te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		IBlockState state = te.getWorld().getBlockState(te.getPos());
		if (state.getValue(BlockMulti.INVISIBLE)) {
			return;
		}
		float rotation = te.getDirection().getRotationAngle();
		boolean upright = te.getUpright();

		RenderTools.setFullColorLightmap();
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5F, y + 2.16F, z + 0.5F);
		GlStateManager.rotate(-rotation, 0, 1, 0); //passing in negative value because of the flipping of the model below

		GlStateManager.pushMatrix();
		if (upright) {
			if ((rotation % 90) == 0) {
				GlStateManager.translate(0, -1.25, 1.85);
			} else {
				GlStateManager.translate(0, -1.25, 2.1);
			}
		} else {
			GlStateManager.translate(0, 0, -0.22f);
		}
		GlStateManager.rotate(upright ? 265 : 180, 1, 0, 0);
		GlStateManager.scale(0.09f, 0.09f, 0.09f);
		bindTexture(TEXTURES.get(te.getVariant()));
		float lidAngle = te.getPrevLidAngle() + (te.getLidAngle() - te.getPrevLidAngle()) * partialTicks;
		COFFIN_MODEL.renderAll((float) (-lidAngle / 180F * Math.PI));
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
	}

	@Override
	public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
		int variant = ItemBlockCoffin.getVariant(stack);
		if (variant < 1) {
			return;
		}
		GlStateManager.pushMatrix();

		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURES.get(variant));

		COFFIN_MODEL.renderAll();

		//Fixes issues with inventory rendering.
		//The Portal renderer modifies blend and disables it.
		//Vanillas inventory relies on the fact that items don't modify gl so it never bothers to set it again.
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.popMatrix();
	}

	@Override
	public IModelState getTransforms() {
		return TRANSFORMS;
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
