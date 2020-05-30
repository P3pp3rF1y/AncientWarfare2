package net.shadowmage.ancientwarfare.structure.render;

import codechicken.lib.render.CCModelState;
import codechicken.lib.render.item.IItemRenderer;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBanner;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.structure.tile.TileFlag;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static codechicken.lib.util.TransformUtils.create;
import static codechicken.lib.util.TransformUtils.flipLeft;

public class ProtectionFlagRenderer extends TileEntitySpecialRenderer<TileFlag> implements IItemRenderer {
	private final ModelBanner bannerModel = new ModelBanner();
	private final ModelSkeletonHead humanoidHead = new ModelHumanoidHead();

	private static final IModelState TRANSFORMS;

	static {
		Map<ItemCameraTransforms.TransformType, TRSRTransformation> map = new HashMap<>();
		TRSRTransformation thirdPerson = create(0F, 2.5F, 0F, 75F, -45F, 0F, 0.6F);
		map.put(ItemCameraTransforms.TransformType.GUI, create(0F, -3F, 0F, 30F, 45F, 0F, 0.525F));
		map.put(ItemCameraTransforms.TransformType.GROUND, create(0F, 3F, 0F, 0F, 0F, 0F, 0.25F));
		map.put(ItemCameraTransforms.TransformType.FIXED, create(0F, 0F, 0F, 0F, 0F, 0F, 0.5F));
		map.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdPerson);
		map.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, flipLeft(thirdPerson));
		map.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, create(0F, 0F, 0F, 0F, -45F, 0F, 0.4F));
		map.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, create(0F, 0F, 0F, 0F, 225F, 0F, 0.4F));
		TRANSFORMS = new CCModelState(map);
	}

	@Override
	public void render(TileFlag te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		boolean flag = te.getWorld() != null;
		int angle = flag ? te.getBlockMetadata() : 0;
		long worldTime = flag ? te.getWorld().getTotalWorldTime() : 0L;
		String faction = te.getName();
		BlockPos pos = te.getPos();

		render((float) x, (float) y, (float) z, partialTicks, alpha, angle, worldTime, faction, pos);
		if (te.isPlayerOwned()) {
			renderPlayerHead(te.getPlayerProfile(), (float) x, (float) y, (float) z, partialTicks, angle);
		}
	}

	private void renderPlayerHead(GameProfile profile, float x, float y, float z, float animateTicks, int rotation) {
		ResourceLocation resourcelocation;

		Minecraft minecraft = Minecraft.getMinecraft();
		Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profile);

		if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
			resourcelocation = minecraft.getSkinManager().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
		} else {
			UUID uuid = EntityPlayer.getUUID(profile);
			resourcelocation = DefaultPlayerSkin.getDefaultSkin(uuid);
		}

		bindTexture(resourcelocation);
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();

		GlStateManager.translate(x + 0.5F, y + 2F, z + 0.5F);

		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
		GlStateManager.enableAlpha();

		GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
		humanoidHead.render(null, animateTicks, 0.0F, 0.0F, 360 * (rotation / 16.0F) - 180, 0.0F, 0.0625F);
		GlStateManager.popMatrix();

	}

	private void render(float x, float y, float z, float partialTicks, float alpha, int rotation, float worldTime, String faction, BlockPos pos) {
		GlStateManager.pushMatrix();

		GlStateManager.translate(x + 0.5F, y + 0.5F, z + 0.5F);
		float f1 = (float) (rotation * 360) / 16.0F;
		GlStateManager.rotate(-f1, 0.0F, 1.0F, 0.0F);
		bannerModel.bannerStand.showModel = true;

		float f3 = (float) (pos.getX() * 7 + pos.getY() * 9 + pos.getZ() * 13) + worldTime + partialTicks;
		bannerModel.bannerSlate.rotateAngleX = (-0.0125F + 0.01F * MathHelper.cos(f3 * (float) Math.PI * 0.02F)) * (float) Math.PI;
		GlStateManager.enableRescaleNormal();
		ResourceLocation resourcelocation = getBannerResourceLocation(faction);

		Minecraft.getMinecraft().renderEngine.bindTexture(resourcelocation);
		GlStateManager.pushMatrix();
		GlStateManager.scale(0.6666667F, -0.6666667F, -0.6666667F);
		bannerModel.renderBanner();
		GlStateManager.popMatrix();

		GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
		GlStateManager.popMatrix();
	}

	private ResourceLocation getBannerResourceLocation(String faction) {
		ResourceLocation textureEntry = new ResourceLocation(AncientWarfareCore.MOD_ID, "textures/entity/structure/banner/" + faction + ".png");
		return textureEntry;
	}

	@Override
	public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
		if (!stack.hasTagCompound()) {
			return;
		}

		GlStateManager.pushMatrix();

		NBTTagCompound tag = stack.getTagCompound();
		//noinspection ConstantConditions
		render(0, 0, 0, 0, 1, 0, 0, tag.getString("name"), BlockPos.ORIGIN);

		//Fixes issues with inventory rendering.
		//The Portal renderer modifies blend and disables it.
		//Vanillas inventory relies on the fact that items don't modify gl so it never bothers to set it again.
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
		return false;
	}
}
