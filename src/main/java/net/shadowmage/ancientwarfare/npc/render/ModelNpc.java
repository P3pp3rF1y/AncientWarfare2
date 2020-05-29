package net.shadowmage.ancientwarfare.npc.render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class ModelNpc extends ModelPlayer {
	public ModelNpc(boolean useSmallArms) {
		super(0.0F, useSmallArms);
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime) {
		rightArmPose = ModelBiped.ArmPose.EMPTY;
		leftArmPose = ModelBiped.ArmPose.EMPTY;
		ItemStack itemstack = entitylivingbaseIn.getHeldItem(EnumHand.MAIN_HAND);
		ItemStack leftHandItemstack = entitylivingbaseIn.getHeldItem(EnumHand.OFF_HAND);

		if (itemstack.getItem() == Items.BOW && ((NpcBase) entitylivingbaseIn).isSwingingArms()) {
			if (entitylivingbaseIn.getPrimaryHand() == EnumHandSide.RIGHT) {
				rightArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
			} else {
				leftArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
			}
		}
		if ((leftHandItemstack.getItem().isShield(entitylivingbaseIn.getHeldItemOffhand(), entitylivingbaseIn) && ((NpcBase) entitylivingbaseIn).isBlocking())) {
			leftArmPose = ArmPose.BLOCK;
		}
		super.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTickTime);
	}
}
