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
public class ModelCoffin extends ModelBase {
	private ModelRenderer sideBottom;
	private ModelRenderer sideL1;
	private ModelRenderer sideL2;
	private ModelRenderer sideTop;
	private ModelRenderer sideR1;
	private ModelRenderer sideR2;
	private ModelRenderer lid;
	private ModelRenderer bottom;

	public ModelCoffin() {
		textureWidth = 144;
		textureHeight = 80;
		sideR2 = new ModelRenderer(this, 0, 35);
		sideR2.setRotationPoint(-5.0F, 15.0F, -7.89F);
		sideR2.addBox(0.0F, 0.0F, 0.0F, 1, 9, 26, 0.0F);
		setRotateAngle(sideR2, -0.11519173063162574F);
		bottom = new ModelRenderer(this, 24, 47);
		bottom.setRotationPoint(-8.0F, 22.9F, -8.0F);
		bottom.addBox(0.0F, 0.0F, 0.0F, 16, 1, 32, 0.0F);
		ModelRenderer lidsideR1 = new ModelRenderer(this, 118, 45);
		lidsideR1.setRotationPoint(0.0F, -0.1F, 25.6F);
		lidsideR1.addBox(0.0F, 0.0F, 0.0F, 1, 1, 7, 0.0F);
		setRotateAngle(lidsideR1, 0.4363323129985824F);
		ModelRenderer lidsideR2 = new ModelRenderer(this, 90, 27);
		lidsideR2.setRotationPoint(3.0F, -0.1F, 0.0F);
		lidsideR2.addBox(0.0F, 0.0F, 0.0F, 1, 1, 26, 0.0F);
		setRotateAngle(lidsideR2, -0.11519173063162574F);
		sideL2 = new ModelRenderer(this, 0, 0);
		sideL2.setRotationPoint(4.0F, 15.0F, -7.89F);
		sideL2.addBox(0.0F, 0.0F, 0.0F, 1, 9, 26, 0.0F);
		setRotateAngle(sideL2, 0.11519173063162574F);
		lid = new ModelRenderer(this, 24, 0);
		lid.setRotationPoint(-8.0F, 14.100000000000001F, -8.0F);
		lid.addBox(0.0F, 0.0F, 0.0F, 16, 0, 32, 0.0F);
		ModelRenderer lidsideL2 = new ModelRenderer(this, 90, 0);
		lidsideL2.setRotationPoint(12.0F, -0.09F, 0.0F);
		lidsideL2.addBox(0.0F, 0.0F, 0.0F, 1, 1, 26, 0.0F);
		setRotateAngle(lidsideL2, 0.11519173063162574F);
		sideBottom = new ModelRenderer(this, 0, 10);
		sideBottom.setRotationPoint(-5.0F, 15.0F, -8.0F);
		sideBottom.addBox(0.0F, 0.0F, 0.0F, 10, 9, 1, 0.0F);
		sideTop = new ModelRenderer(this, 0, 0);
		sideTop.setRotationPoint(-5.0F, 15.0F, 23.0F);
		sideTop.addBox(0.0F, 0.0F, 0.0F, 10, 9, 1, 0.0F);
		ModelRenderer lidsideBottom = new ModelRenderer(this, 90, 2);
		lidsideBottom.setRotationPoint(3.0F, -0.11F, 0.0F);
		lidsideBottom.addBox(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
		ModelRenderer lidsideL1 = new ModelRenderer(this, 118, 18);
		lidsideL1.setRotationPoint(15.1F, -0.1F, 25.2F);
		lidsideL1.addBox(0.0F, 0.0F, 0.0F, 1, 1, 7, 0.0F);
		setRotateAngle(lidsideL1, -0.4363323129985824F);
		sideR1 = new ModelRenderer(this, 28, 45);
		sideR1.setRotationPoint(-7.96F, 15.0F, 17.75F);
		sideR1.addBox(0.0F, 0.0F, 0.0F, 1, 9, 7, 0.0F);
		setRotateAngle(sideR1, 0.4363323129985824F);
		ModelRenderer lidsideTop = new ModelRenderer(this, 90, 0);
		lidsideTop.setRotationPoint(3.0F, -0.11F, 31.0F);
		lidsideTop.addBox(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
		sideL1 = new ModelRenderer(this, 28, 10);
		sideL1.setRotationPoint(7.05F, 15.0F, 17.3F);
		sideL1.addBox(0.0F, 0.0F, 0.0F, 1, 9, 7, 0.0F);
		setRotateAngle(sideL1, -0.4363323129985824F);
		lid.addChild(lidsideR1);
		lid.addChild(lidsideR2);
		lid.addChild(lidsideL2);
		lid.addChild(lidsideBottom);
		lid.addChild(lidsideL1);
		lid.addChild(lidsideTop);
	}

	public void renderAll() {
		renderAll(0F);
	}

	public void renderAll(float lidAngle) {
		float scale = 1f;
		sideR2.render(scale);
		bottom.render(scale);
		sideL2.render(scale);
		setRotateAngle(lid, 0F, 0F, lidAngle);
		lid.render(scale);
		sideBottom.render(scale);
		sideTop.render(scale);
		sideR1.render(scale);
		sideL1.render(scale);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	private void setRotateAngle(ModelRenderer modelRenderer, float y) {
		setRotateAngle(modelRenderer, 0F, y, 0F);
	}

	private void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
