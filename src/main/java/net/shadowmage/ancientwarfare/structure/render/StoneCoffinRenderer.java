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
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import net.shadowmage.ancientwarfare.structure.block.BlockStoneCoffin;
import net.shadowmage.ancientwarfare.structure.model.ModelStoneCoffin;
import net.shadowmage.ancientwarfare.structure.tile.TileStoneCoffin;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class StoneCoffinRenderer extends TileEntitySpecialRenderer<TileStoneCoffin> implements IItemRenderer {
	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.MOD_ID + ":structure/stone_coffin", "normal");
	private static final ModelStoneCoffin COFFIN_MODEL = new ModelStoneCoffin();

	private static final Map<Integer, ResourceLocation> TEXTURES = new HashMap<>();

	public static void setTexture(int variantId, ResourceLocation textureLocation) {
		TEXTURES.put(variantId, textureLocation);
	}

	private static final IModelState TRANSFORMS;

	static {
		Map<ItemCameraTransforms.TransformType, TRSRTransformation> map;
		TRSRTransformation thirdPerson;

		map = new EnumMap<>(ItemCameraTransforms.TransformType.class);
		thirdPerson = TransformUtils.create(0F, 2.5F, 0F, 75F, 45F, 0F, 0.375F);
		map.put(ItemCameraTransforms.TransformType.GUI, TransformUtils.create(0F, 0F, 0F, 30F, 45F, 0F, 0.625F));
		map.put(ItemCameraTransforms.TransformType.GROUND, TransformUtils.create(0F, 3F, 0F, 0F, 0F, 0F, 0.25F));
		map.put(ItemCameraTransforms.TransformType.FIXED, TransformUtils.create(0F, 0F, 0F, 0F, 0F, 0F, 0.5F));
		map.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdPerson);
		map.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, TransformUtils.flipLeft(thirdPerson));
		map.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, TransformUtils.create(0F, 0F, 0F, 0F, 45F, 0F, 0.4F));
		map.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, TransformUtils.create(0F, 0F, 0F, 0F, 225F, 0F, 0.4F));
		TRANSFORMS = new CCModelState(map);
	}

	@Override
	public void render(TileStoneCoffin te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {

		IBlockState state = te.getWorld().getBlockState(te.getPos());
		float rotation = state.getValue(BlockStoneCoffin.DIRECTION).getRotationAngle();
//		boolean upright = state.getValue(BlockStoneCoffin.UPRIGHT);

		RenderTools.setFullColorLightmap();
		GlStateManager.pushMatrix();
		GlStateManager.translate(x - 0F, y + 1.835F, z + 0.25F);
		GlStateManager.rotate(-rotation, 0, 1, 0); //passing in negative value because of the flipping of the model below

		GlStateManager.pushMatrix();

		GlStateManager.translate(0, 0, -0.22f);
		GlStateManager.rotate(180, 1, 0, 0);
		GlStateManager.scale(0.076f, 0.076f, 0.076f);
		bindTexture(new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/structure/stone_coffin_1.png"));
		COFFIN_MODEL.renderAll();
		GlStateManager.popMatrix();

		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y + 0.3F, (float) z + 0.5F);
		GlStateManager.rotate(90, 1, 0, 0);
		GlStateManager.popMatrix();
	}

	@Override
	public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
		GlStateManager.pushMatrix();

		bindTexture(TEXTURES.get(1));

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
