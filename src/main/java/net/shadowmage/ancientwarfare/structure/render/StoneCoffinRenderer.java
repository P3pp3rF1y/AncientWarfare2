package net.shadowmage.ancientwarfare.structure.render;

import codechicken.lib.render.CCModelState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.structure.block.BlockCoffin;
import net.shadowmage.ancientwarfare.structure.block.BlockMulti;
import net.shadowmage.ancientwarfare.structure.block.BlockStoneCoffin;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockStoneCoffin;
import net.shadowmage.ancientwarfare.structure.model.ModelStoneCoffin;
import net.shadowmage.ancientwarfare.structure.tile.TileStoneCoffin;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class StoneCoffinRenderer extends RenderLootInfo<TileStoneCoffin> implements IItemRenderer {
	private static final ModelStoneCoffin STONE_COFFIN_MODEL = new ModelStoneCoffin();

	private static final Map<BlockCoffin.IVariant, ResourceLocation> TEXTURES = new HashMap<>();

	static {
		for (BlockStoneCoffin.Variant variant : BlockStoneCoffin.Variant.values()) {
			TEXTURES.put(variant, new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/model/structure/stone_coffin_" + variant.getName() + ".png"));
		}
	}

	private static final IModelState TRANSFORMS;

	static {
		Map<ItemCameraTransforms.TransformType, TRSRTransformation> map;
		TRSRTransformation thirdPerson;

		map = new EnumMap<>(ItemCameraTransforms.TransformType.class);
		thirdPerson = TransformUtils.create(0F, 3F, 5F, 75F, 180F, 180F, 0.015F);
		map.put(ItemCameraTransforms.TransformType.GUI, TransformUtils.create(1F, 4F, 0F, 60F, 225F, 200F, 0.019F));
		map.put(ItemCameraTransforms.TransformType.GROUND, TransformUtils.create(0F, 8F, 0F, 0F, 0F, 180F, 0.017F));
		map.put(ItemCameraTransforms.TransformType.FIXED, TransformUtils.create(0F, -4F, -12F, 90F, 180F, 0F, 0.035F));
		map.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdPerson);
		map.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, TransformUtils.flipLeft(thirdPerson));
		map.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, TransformUtils.create(25F, -15F, -10F, 50F, 170F, 170F, 0.08F));
		map.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, TransformUtils.create(25F, -15F, -10F, 50F, 170F, 170F, 0.08F));
		TRANSFORMS = new CCModelState(map);
	}

	@Override
	public void render(TileStoneCoffin te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		super.render(te, x, y, z, partialTicks, destroyStage, alpha);
		IBlockState state = te.getWorld().getBlockState(te.getPos());
		if (state.getBlock() != AWStructureBlocks.STONE_COFFIN || Boolean.TRUE.equals(state.getValue(BlockMulti.INVISIBLE))) {
			return;
		}
		float rotation = te.getDirection().getRotationAngle();

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 1.78F, z);
		GlStateManager.rotate(-rotation, 0, 1, 0); //passing in negative value because of the flipping of the model below
		GlStateManager.pushMatrix();

		switch ((int) rotation) {
			case 0: // north
				GlStateManager.translate(0, 0, -1F);
				break;
			case 90: // east
				GlStateManager.translate(0, 0, -2F);
				break;
			case 180: //south
				GlStateManager.translate(-1F, 0, -2F);
				break;
			case 270: // west
			default:
				GlStateManager.translate(-1F, 0, -1F);
				break;
		}

		GlStateManager.rotate(180, 1, 0, 0);
		GlStateManager.scale(0.074f, 0.074f, 0.074f);
		bindTexture(TEXTURES.get(te.getVariant()));
		float lidAngle = te.getPrevLidAngle() + (te.getLidAngle() - te.getPrevLidAngle()) * partialTicks;
		STONE_COFFIN_MODEL.renderAll((float) (-lidAngle / 180F * Math.PI));
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
	}

	@Override
	public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
		BlockStoneCoffin.Variant variant = ItemBlockStoneCoffin.getVariant(stack);

		GlStateManager.pushMatrix();

		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURES.get(variant));

		STONE_COFFIN_MODEL.renderAll();

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
