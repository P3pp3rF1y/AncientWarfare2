package net.shadowmage.ancientwarfare.vehicle.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelShot extends ModelBase {

	ModelRenderer Shape1;
	ModelRenderer Shape5;
	ModelRenderer Shape4;
	ModelRenderer Shape3;
	ModelRenderer Shape7;
	ModelRenderer Shape2;
	ModelRenderer Shape6;
	ModelRenderer Shape11;
	ModelRenderer Shape8;
	ModelRenderer Shape10;
	ModelRenderer Shape12;
	ModelRenderer Shape9;

	public ModelShot() {
		Shape1 = new ModelRenderer(this, "Shape1");
		Shape1.setTextureOffset(0, 28);
		Shape1.setTextureSize(128, 128);
		Shape1.setRotationPoint(-6.0f, 6.0f, -6.0f);
		setPieceRotation(Shape1, 0.0f, 0.0f, 0.0f);
		Shape1.addBox(0.0f, 0.0f, 0.0f, 12, 1, 12);
		Shape5 = new ModelRenderer(this, "Shape5");
		Shape5.setTextureOffset(27, 71);
		Shape5.setTextureSize(128, 128);
		Shape5.setRotationPoint(12.0f, -12.0f, 0.0f);
		setPieceRotation(Shape5, 0.0f, 0.0f, 0.0f);
		Shape5.addBox(0.0f, 0.0f, 0.0f, 1, 12, 12);
		Shape1.addChild(Shape5);
		Shape4 = new ModelRenderer(this, "Shape4");
		Shape4.setTextureOffset(27, 45);
		Shape4.setTextureSize(128, 128);
		Shape4.setRotationPoint(0.0f, -12.0f, -1.0f);
		setPieceRotation(Shape4, 0.0f, 0.0f, 0.0f);
		Shape4.addBox(0.0f, 0.0f, 0.0f, 12, 12, 1);
		Shape1.addChild(Shape4);
		Shape3 = new ModelRenderer(this, "Shape3");
		Shape3.setTextureOffset(49, 28);
		Shape3.setTextureSize(128, 128);
		Shape3.setRotationPoint(0.0f, -13.0f, 0.0f);
		setPieceRotation(Shape3, 0.0f, 0.0f, 0.0f);
		Shape3.addBox(0.0f, 0.0f, 0.0f, 12, 1, 12);
		Shape1.addChild(Shape3);
		Shape7 = new ModelRenderer(this, "Shape7");
		Shape7.setTextureOffset(0, 71);
		Shape7.setTextureSize(128, 128);
		Shape7.setRotationPoint(-1.0f, -12.0f, 0.0f);
		setPieceRotation(Shape7, 0.0f, 0.0f, 0.0f);
		Shape7.addBox(0.0f, 0.0f, 0.0f, 1, 12, 12);
		Shape1.addChild(Shape7);
		Shape2 = new ModelRenderer(this, "Shape2");
		Shape2.setTextureOffset(41, 59);
		Shape2.setTextureSize(128, 128);
		Shape2.setRotationPoint(1.0f, -14.0f, 1.0f);
		setPieceRotation(Shape2, 0.0f, 0.0f, 0.0f);
		Shape2.addBox(0.0f, 0.0f, 0.0f, 10, 1, 10);
		Shape1.addChild(Shape2);
		Shape6 = new ModelRenderer(this, "Shape6");
		Shape6.setTextureOffset(0, 59);
		Shape6.setTextureSize(128, 128);
		Shape6.setRotationPoint(1.0f, 1.0f, 1.0f);
		setPieceRotation(Shape6, 0.0f, 0.0f, 0.0f);
		Shape6.addBox(0.0f, 0.0f, 0.0f, 10, 1, 10);
		Shape1.addChild(Shape6);
		Shape11 = new ModelRenderer(this, "Shape11");
		Shape11.setTextureOffset(23, 96);
		Shape11.setTextureSize(128, 128);
		Shape11.setRotationPoint(-2.0f, -11.0f, 1.0f);
		setPieceRotation(Shape11, 0.0f, 0.0f, 0.0f);
		Shape11.addBox(0.0f, 0.0f, 0.0f, 1, 10, 10);
		Shape1.addChild(Shape11);
		Shape8 = new ModelRenderer(this, "Shape8");
		Shape8.setTextureOffset(0, 45);
		Shape8.setTextureSize(128, 128);
		Shape8.setRotationPoint(0.0f, -12.0f, 12.0f);
		setPieceRotation(Shape8, 0.0f, 0.0f, 0.0f);
		Shape8.addBox(0.0f, 0.0f, 0.0f, 12, 12, 1);
		Shape1.addChild(Shape8);
		Shape10 = new ModelRenderer(this, "Shape10");
		Shape10.setTextureOffset(0, 16);
		Shape10.setTextureSize(128, 128);
		Shape10.setRotationPoint(1.0f, -11.0f, 13.0f);
		setPieceRotation(Shape10, 0.0f, 0.0f, 0.0f);
		Shape10.addBox(0.0f, 0.0f, 0.0f, 10, 10, 1);
		Shape1.addChild(Shape10);
		Shape12 = new ModelRenderer(this, "Shape12");
		Shape12.setTextureOffset(0, 96);
		Shape12.setTextureSize(128, 128);
		Shape12.setRotationPoint(13.0f, -11.0f, 1.0f);
		setPieceRotation(Shape12, 0.0f, 0.0f, 0.0f);
		Shape12.addBox(0.0f, 0.0f, 0.0f, 1, 10, 10);
		Shape1.addChild(Shape12);
		Shape9 = new ModelRenderer(this, "Shape9");
		Shape9.setTextureOffset(23, 16);
		Shape9.setTextureSize(128, 128);
		Shape9.setRotationPoint(1.0f, -11.0f, -2.0f);
		setPieceRotation(Shape9, 0.0f, 0.0f, 0.0f);
		Shape9.addBox(0.0f, 0.0f, 0.0f, 10, 10, 1);
		Shape1.addChild(Shape9);
	}

	@Override
	public void render(Entity entity, float f1, float f2, float f3, float f4, float f5, float f6) {
		super.render(entity, f1, f2, f3, f4, f5, f6);
		setRotationAngles(f1, f2, f3, f4, f5, f6, entity);
		Shape1.render(f6);
	}

	public void setPieceRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
