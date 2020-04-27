package net.shadowmage.ancientwarfare.structure.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Coffin - Sir Squidly
 * Created using Tabula 7.0.1
 */
@SideOnly(Side.CLIENT)
public class ModelStoneCoffin extends ModelBase {

///// old
//	private ModelRenderer sideBottom;
//	private ModelRenderer sideL1;
//	private ModelRenderer sideL2;
//	private ModelRenderer sideTop;
//	private ModelRenderer sideR1;
//	private ModelRenderer sideR2;
//	private ModelRenderer lid;
//	private ModelRenderer bottom;
/////


	public ModelRenderer baseface;
	public ModelRenderer cover;
	public ModelRenderer rightface;
	public ModelRenderer southface;
	public ModelRenderer northface;
	public ModelRenderer leftface;
	public ModelRenderer coverinside;



	public ModelStoneCoffin() {
		this.textureWidth = 128;
		this.textureHeight = 128;
		this.coverinside = new ModelRenderer(this, 2, 1);
		this.coverinside.mirror = true;
		this.coverinside.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.coverinside.addBox(-9.5F, -36.5F, -3.5F, 19, 43, 3, 0.0F);
		this.baseface = new ModelRenderer(this, -48, 0);
		this.baseface.setRotationPoint(0.0F, 24.0F, 0.0F);
		this.baseface.addBox(-12.0F, -4.0F, -24.0F, 24, 4, 48, 0.0F);
		this.cover = new ModelRenderer(this, 48, 0);
		this.cover.setRotationPoint(0.0F, -12.0F, -15.0F);
		this.cover.addBox(-10.0F, -37.0F, -4.0F, 20, 44, 4, 0.0F);
		this.setRotateAngle(cover, -1.5707963267948966F, 0.0F, 0.0F);
		this.southface = new ModelRenderer(this, 0, 52);
		this.southface.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.southface.addBox(-12.0F, -12.0F, -24.0F, 24, 12, 4, 0.0F);
		this.northface = new ModelRenderer(this, 0, 52);
		this.northface.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.northface.addBox(-12.0F, -12.0F, -24.0F, 24, 12, 4, 0.0F);
		this.setRotateAngle(northface, 0.0F, -3.141592653589793F, 0.0F);
		this.leftface = new ModelRenderer(this, 0, 68);
		this.leftface.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.leftface.addBox(-24.0F, -12.0F, -12.0F, 48, 12, 4, 0.0F);
		this.setRotateAngle(leftface, 0.0F, 1.5707963267948966F, 0.0F);
		this.rightface = new ModelRenderer(this, 0, 68);
		this.rightface.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.rightface.addBox(-24.0F, -12.0F, -12.0F, 48, 12, 4, 0.0F);
		this.setRotateAngle(rightface, 0.0F, -1.5707963267948966F, 0.0F);
		this.cover.addChild(this.coverinside);
		this.baseface.addChild(this.cover);
		this.baseface.addChild(this.southface);
		this.baseface.addChild(this.northface);
		this.baseface.addChild(this.leftface);
		this.baseface.addChild(this.rightface);
	}

	public void renderAll() {
		float scale = 1f;
		baseface.render(scale);
//		cover.render(scale);
//		rightface.render(scale);
//		southface.render(scale);
//		northface.render(scale);
//		leftface.render(scale);
//		coverinside.render(scale);
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
