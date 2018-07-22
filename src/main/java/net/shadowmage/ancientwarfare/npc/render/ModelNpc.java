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
	public ModelNpc() {
		super(0.0F, false);
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime) {
		this.rightArmPose = ModelBiped.ArmPose.EMPTY;
		this.leftArmPose = ModelBiped.ArmPose.EMPTY;
		ItemStack itemstack = entitylivingbaseIn.getHeldItem(EnumHand.MAIN_HAND);

		if (itemstack.getItem() == Items.BOW && ((NpcBase) entitylivingbaseIn).isSwingingArms()) {
			if (entitylivingbaseIn.getPrimaryHand() == EnumHandSide.RIGHT) {
				this.rightArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
			} else {
				this.leftArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
			}
		}

		super.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTickTime);
	}
}
