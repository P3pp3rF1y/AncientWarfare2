package net.shadowmage.ancientwarfare.npc.render;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;

public class ModelNpc extends ModelPlayer {
	public ModelNpc(boolean useSmallArms) {
		super(0.0F, useSmallArms);
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime) {
		ArmPose mainArmPose = ArmPose.EMPTY;
		ArmPose offArmPose = ArmPose.EMPTY;
		ItemStack mainHandItemStack = entitylivingbaseIn.getHeldItem(EnumHand.MAIN_HAND);
		ItemStack offHandItemStack = entitylivingbaseIn.getHeldItem(EnumHand.OFF_HAND);

		if (entitylivingbaseIn.getItemInUseCount() > 0) {
			if (entitylivingbaseIn.getActiveHand() == EnumHand.MAIN_HAND && !mainHandItemStack.isEmpty() && mainHandItemStack.getItemUseAction() == EnumAction.BOW) {
				mainArmPose = ArmPose.BOW_AND_ARROW;
			}

			if (entitylivingbaseIn.getActiveHand() == EnumHand.OFF_HAND && !offHandItemStack.isEmpty() && offHandItemStack.getItemUseAction() == EnumAction.BLOCK) {
				offArmPose = ArmPose.BLOCK;
			}
		}

		if (entitylivingbaseIn.getPrimaryHand() == EnumHandSide.RIGHT) {
			rightArmPose = mainArmPose;
			leftArmPose = offArmPose;
		} else {
			leftArmPose = mainArmPose;
			rightArmPose = offArmPose;
		}

		super.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTickTime);
	}
}
