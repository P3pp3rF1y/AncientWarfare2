package net.shadowmage.ancientwarfare.structure.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * ModelAW2CoffinStone - Silentine
 * Created using Tabula 7.1.0
 */
@SideOnly(Side.CLIENT)
public class ModelStoneCoffin extends ModelBase {


	public ModelRenderer baseface;
	public ModelRenderer lid;
	public ModelRenderer rightface;
	public ModelRenderer southface;
	public ModelRenderer northface;
	public ModelRenderer leftface;
	public ModelRenderer lidInside;



	public ModelStoneCoffin() {
		textureWidth = 128;
		textureHeight = 128;
		lid = new ModelRenderer(this, 48, 0);
		lid.setRotationPoint(0.0F, 12.0F, -15.0F);
		lid.addBox(-10.0F, -37.0F, -4.0F, 20, 44, 4, 0.0F);
		southface = new ModelRenderer(this, 0, 52);
		southface.setRotationPoint(0.0F, 0.0F, 0.0F);
		southface.addBox(-12.0F, -12.0F, -24.0F, 24, 12, 4, 0.0F);
		lidInside = new ModelRenderer(this, 2, 1);
		lidInside.mirror = true;
		lidInside.setRotationPoint(0.0F, 0.0F, 0.0F);
		lidInside.addBox(-9.5F, -36.5F, -3.5F, 19, 43, 3, 0.0F);
		northface = new ModelRenderer(this, 0, 52);
		northface.setRotationPoint(0.0F, 0.0F, 0.0F);
		northface.addBox(-12.0F, -12.0F, -24.0F, 24, 12, 4, 0.0F);
		setRotateAngle(northface, 0.0F, -3.141592653589793F, 0.0F);
		leftface = new ModelRenderer(this, 0, 68);
		leftface.setRotationPoint(0.0F, 0.0F, 0.0F);
		leftface.addBox(-24.0F, -12.0F, -12.0F, 48, 12, 4, 0.0F);
		setRotateAngle(leftface, 0.0F, 1.5707963267948966F, 0.0F);
		baseface = new ModelRenderer(this, -48, 0);
		baseface.setRotationPoint(0.0F, 24.0F, 0.0F);
		baseface.addBox(-12.0F, -4.0F, -24.0F, 24, 4, 48, 0.0F);
		rightface = new ModelRenderer(this, 0, 68);
		rightface.setRotationPoint(0.0F, 0.0F, 0.0F);
		rightface.addBox(-24.0F, -12.0F, -12.0F, 48, 12, 4, 0.0F);
		setRotateAngle(rightface, 0.0F, -1.5707963267948966F, 0.0F);
		baseface.addChild(this.southface);
		lid.addChild(this.lidInside);
		baseface.addChild(this.northface);
		baseface.addChild(this.leftface);
		baseface.addChild(this.rightface);
	}

	public void renderAll() {
		renderAll(0F);
	}

	public void renderAll(float lidAngle) {
		float scale = 1f;
		baseface.render(scale);
		setRotateAngle(lid, -1.5707963267948966F, lidAngle, 0F);
		lid.render(scale);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
