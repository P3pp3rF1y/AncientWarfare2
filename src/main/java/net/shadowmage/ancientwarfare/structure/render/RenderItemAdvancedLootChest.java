package net.shadowmage.ancientwarfare.structure.render;

import codechicken.lib.render.CCModelState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.EnumMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class RenderItemAdvancedLootChest implements IItemRenderer {
	private static final TileEntityChest CHEST_TE = new TileEntityChest();

	private static final IModelState TRANSFORMS;

	static {
		Map<TransformType, TRSRTransformation> map;
		TRSRTransformation thirdPerson;

		map = new EnumMap<>(TransformType.class);
		thirdPerson = TransformUtils.create(0F, 2.5F, 0F, 75F, 45F, 0F, 0.375F);
		map.put(TransformType.GUI, TransformUtils.create(0F, 0F, 0F, 30F, 45F, 0F, 0.625F));
		map.put(TransformType.GROUND, TransformUtils.create(0F, 3F, 0F, 0F, 0F, 0F, 0.25F));
		map.put(TransformType.FIXED, TransformUtils.create(0F, 0F, 0F, 0F, 0F, 0F, 0.5F));
		map.put(TransformType.THIRD_PERSON_RIGHT_HAND, thirdPerson);
		map.put(TransformType.THIRD_PERSON_LEFT_HAND, TransformUtils.flipLeft(thirdPerson));
		map.put(TransformType.FIRST_PERSON_RIGHT_HAND, TransformUtils.create(0F, 0F, 0F, 0F, 45F, 0F, 0.4F));
		map.put(TransformType.FIRST_PERSON_LEFT_HAND, TransformUtils.create(0F, 0F, 0F, 0F, 225F, 0F, 0.4F));
		TRANSFORMS = new CCModelState(map);
	}

	@Override
	public void renderItem(ItemStack stack, TransformType transformType) {
		GlStateManager.pushMatrix();

		TileEntityRendererDispatcher.instance.render(CHEST_TE, 0D, 0D, 0D, 0F, 1F);

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
