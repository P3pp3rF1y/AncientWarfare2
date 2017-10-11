package net.shadowmage.ancientwarfare.npc.render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class ModelNpc extends ModelBiped {

    private final ModelRenderer playerSkinLeftArm;
    private final ModelRenderer playerSkinLeftArmwear;
    private final ModelRenderer playerSkinRightArmwear;
    private final ModelRenderer playerSkinLeftLeg;
    private final ModelRenderer playerSkinLeftLegwear;
    private final ModelRenderer playerSkinRightLegwear;
    private final ModelRenderer playerSkinBodyWear;
    private final ModelRenderer playerSkinHead;
    private final ModelRenderer playerSkinHeadwear;
    private final ModelRenderer playerSkinBody;
    private final ModelRenderer playerSkinRightArm;
    private final ModelRenderer playerSkinRightLeg;

    public ModelNpc() {
        super(0.0F, 0.0F, 64, 32);
        textureHeight = 64;
        playerSkinHead = new ModelRenderer(this, 0, 0);
        playerSkinHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
        playerSkinHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        playerSkinHeadwear = new ModelRenderer(this, 32, 0);
        playerSkinHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F);
        playerSkinHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
        playerSkinBody = new ModelRenderer(this, 16, 16);
        playerSkinBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
        playerSkinBody.setRotationPoint(0.0F, 0.0F, 0.0F);
        playerSkinRightArm = new ModelRenderer(this, 40, 16);
        playerSkinRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
        playerSkinRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        playerSkinRightLeg = new ModelRenderer(this, 0, 16);
        playerSkinRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        playerSkinRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        playerSkinLeftArm = new ModelRenderer(this, 32, 48);
        playerSkinLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
        playerSkinLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        playerSkinLeftArmwear = new ModelRenderer(this, 48, 48);
        playerSkinLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.25F);
        playerSkinLeftArmwear.setRotationPoint(5.0F, 2.0F, 0.0F);
        playerSkinRightArmwear = new ModelRenderer(this, 40, 32);
        playerSkinRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, 0.25F);
        playerSkinRightArmwear.setRotationPoint(-5.0F, 2.0F, 10.0F);
        playerSkinLeftLeg = new ModelRenderer(this, 16, 48);
        playerSkinLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.0F);
        playerSkinLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
        playerSkinLeftLegwear = new ModelRenderer(this, 0, 48);
        playerSkinLeftLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F);
        playerSkinLeftLegwear.setRotationPoint(1.9F, 12.0F, 0.0F);
        playerSkinRightLegwear = new ModelRenderer(this, 0, 32);
        playerSkinRightLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, 0.25F);
        playerSkinRightLegwear.setRotationPoint(-1.9F, 12.0F, 0.0F);
        playerSkinBodyWear = new ModelRenderer(this, 16, 32);
        playerSkinBodyWear.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.25F);
        playerSkinBodyWear.setRotationPoint(0.0F, 0.0F, 0.0F);

        setupPartVisibility(false);
    }

    private void setupPartVisibility(boolean usesPlayerSkin) {
        playerSkinLeftArm.isHidden = !usesPlayerSkin;
        playerSkinLeftArmwear.isHidden = !usesPlayerSkin;
        playerSkinRightArm.isHidden = !usesPlayerSkin;
        playerSkinRightArmwear.isHidden = !usesPlayerSkin;
        playerSkinLeftLeg.isHidden = !usesPlayerSkin;
        playerSkinLeftLegwear.isHidden = !usesPlayerSkin;
        playerSkinRightLeg.isHidden = !usesPlayerSkin;
        playerSkinRightLegwear.isHidden = !usesPlayerSkin;
        playerSkinBody.isHidden = !usesPlayerSkin;
        playerSkinBodyWear.isHidden = !usesPlayerSkin;
        playerSkinHead.isHidden = !usesPlayerSkin;
        playerSkinHeadwear.isHidden = !usesPlayerSkin;

        bipedLeftArm.isHidden = usesPlayerSkin;
        bipedLeftLeg.isHidden = usesPlayerSkin;
        bipedRightLeg.isHidden = usesPlayerSkin;
        bipedRightArm.isHidden = usesPlayerSkin;
        bipedBody.isHidden = usesPlayerSkin;
        bipedHead.isHidden = usesPlayerSkin;
        bipedHeadwear.isHidden = usesPlayerSkin;
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime) {
        this.rightArmPose = ModelBiped.ArmPose.EMPTY;
        this.leftArmPose = ModelBiped.ArmPose.EMPTY;
        ItemStack itemstack = entitylivingbaseIn.getHeldItem(EnumHand.MAIN_HAND);

        if (itemstack.getItem() == Items.BOW && ((NpcBase)entitylivingbaseIn).isSwingingArms())
        {
            if (entitylivingbaseIn.getPrimaryHand() == EnumHandSide.RIGHT)
            {
                this.rightArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
            }
            else
            {
                this.leftArmPose = ModelBiped.ArmPose.BOW_AND_ARROW;
            }
        }

        super.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTickTime);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        boolean usesPlayerSkin = ((NpcBase) entityIn).getUsesPlayerSkin();
        setupPartVisibility(usesPlayerSkin);
        if (usesPlayerSkin) {
            setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
            GlStateManager.pushMatrix();

            playerSkinLeftLeg.render(scale);
            playerSkinLeftLegwear.render(scale);
            playerSkinRightLeg.render(scale);
            playerSkinRightLegwear.render(scale);
            playerSkinLeftArm.render(scale);
            playerSkinLeftArmwear.render(scale);
            playerSkinRightArm.render(scale);
            playerSkinRightArmwear.render(scale);
            playerSkinBody.render(scale);
            playerSkinBodyWear.render(scale);
            playerSkinHead.render(scale);
            playerSkinHeadwear.render(scale);

            GlStateManager.popMatrix();
        } else {
            super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        copyModelAngles(this.bipedLeftLeg, this.playerSkinLeftLegwear);
        copyModelAngles(this.bipedLeftLeg, this.playerSkinLeftLeg);
        copyModelAngles(this.bipedRightLeg, this.playerSkinRightLegwear);
        copyModelAngles(this.bipedRightLeg, this.playerSkinRightLeg);
        copyModelAngles(this.bipedLeftArm, this.playerSkinLeftArm);
        copyModelAngles(this.bipedLeftArm, this.playerSkinLeftArmwear);
        copyModelAngles(this.bipedRightArm, this.playerSkinRightArmwear);
        copyModelAngles(this.bipedRightArm, this.playerSkinRightArm);
        copyModelAngles(this.bipedBody, this.playerSkinBodyWear);
        copyModelAngles(this.bipedBody, this.playerSkinBody);
        copyModelAngles(this.bipedHead, this.playerSkinHead);
        copyModelAngles(this.bipedHead, this.playerSkinHeadwear);
    }
}
