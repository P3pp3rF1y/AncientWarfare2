package net.shadowmage.ancientwarfare.vehicle.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.util.Trig;

@SideOnly(Side.CLIENT)
public class ModelCatapultStandFixed extends ModelBase {
	ModelRenderer turretMidBeam;
	ModelRenderer upright1;
	ModelRenderer upright2;
	ModelRenderer turretTopHorizontal;
	ModelRenderer turretLeftAngled;
	ModelRenderer turretRightAngled;
	ModelRenderer pulleyAxle;
	ModelRenderer pulleyArm1;
	ModelRenderer pulleyArm2;
	ModelRenderer turretFrontBeam;
	ModelRenderer turretRearBeam;
	ModelRenderer armMain;
	ModelRenderer bucket1;
	ModelRenderer bucket3;
	ModelRenderer bucket6;
	ModelRenderer bucket4;
	ModelRenderer bucket2;
	ModelRenderer turretLeftBeam1;
	ModelRenderer turretLeftBeam2;
	ModelRenderer turretRightBeam1;
	ModelRenderer turretRightBeam2;
	ModelRenderer turretLeftAngle2;
	ModelRenderer turretRightAngle2;

	public ModelCatapultStandFixed() {
		turretMidBeam = new ModelRenderer(this, "turretMidBeam");
		turretMidBeam.setTextureOffset(0, 128);
		turretMidBeam.setTextureSize(256, 256);
		turretMidBeam.setRotationPoint(0.0f, -1.0f, 0.0f);
		setPieceRotation(turretMidBeam, 0.0f, 0.0f, 0.0f);
		turretMidBeam.addBox(-12.0f, -1.0f, -1.5f, 24, 2, 3);
		upright1 = new ModelRenderer(this, "upright1");
		upright1.setTextureOffset(9, 150);
		upright1.setTextureSize(256, 256);
		upright1.setRotationPoint(-15.0f, -19.0f, -1.5f);
		setPieceRotation(upright1, 0.0f, 0.0f, 0.0f);
		upright1.addBox(0.0f, 0.0f, 0.0f, 3, 20, 3);
		turretMidBeam.addChild(upright1);
		upright2 = new ModelRenderer(this, "upright2");
		upright2.setTextureOffset(9, 150);
		upright2.setTextureSize(256, 256);
		upright2.setRotationPoint(12.0f, -19.0f, -1.5f);
		setPieceRotation(upright2, 0.0f, 0.0f, 0.0f);
		upright2.addBox(0.0f, 0.0f, 0.0f, 3, 20, 3);
		turretMidBeam.addChild(upright2);
		turretTopHorizontal = new ModelRenderer(this, "turretTopHorizontal");
		turretTopHorizontal.setTextureOffset(0, 140);
		turretTopHorizontal.setTextureSize(256, 256);
		turretTopHorizontal.setRotationPoint(-12.0f, -19.0f, -1.5f);
		setPieceRotation(turretTopHorizontal, 0.0f, 0.0f, 0.0f);
		turretTopHorizontal.addBox(0.0f, 0.0f, 0.0f, 24, 3, 3);
		turretMidBeam.addChild(turretTopHorizontal);
		turretLeftAngled = new ModelRenderer(this, "turretLeftAngled");
		turretLeftAngled.setTextureOffset(0, 150);
		turretLeftAngled.setTextureSize(256, 256);
		turretLeftAngled.setRotationPoint(12.5f, -19.0f, -1.0f);
		setPieceRotation(turretLeftAngled, -0.7853982f, 0.0f, 0.0f);
		turretLeftAngled.addBox(0.0f, 0.0f, 0.0f, 2, 26, 2);
		turretMidBeam.addChild(turretLeftAngled);
		turretRightAngled = new ModelRenderer(this, "turretRightAngled");
		turretRightAngled.setTextureOffset(0, 150);
		turretRightAngled.setTextureSize(256, 256);
		turretRightAngled.setRotationPoint(-14.5f, -19.0f, -1.0f);
		setPieceRotation(turretRightAngled, -0.7853961f, 0.0f, 0.0f);
		turretRightAngled.addBox(0.0f, 0.0f, 0.0f, 2, 26, 2);
		turretMidBeam.addChild(turretRightAngled);
		pulleyAxle = new ModelRenderer(this, "pulleyAxle");
		pulleyAxle.setTextureOffset(0, 147);
		pulleyAxle.setTextureSize(256, 256);
		pulleyAxle.setRotationPoint(2.0f, -3.0f, 0.0f);
		setPieceRotation(pulleyAxle, -4.9455734E-6f, 0.0f, 0.0f);
		pulleyAxle.addBox(-18.0f, -0.5f, -0.5f, 30, 1, 1);
		pulleyArm1 = new ModelRenderer(this, "pulleyArm1");
		pulleyArm1.setTextureOffset(68, 140);
		pulleyArm1.setTextureSize(256, 256);
		pulleyArm1.setRotationPoint(-18.0f, 0.0f, 0.0f);
		setPieceRotation(pulleyArm1, 0.0f, 0.0f, 0.0f);
		pulleyArm1.addBox(-0.5f, -0.5f, -4.5f, 1, 1, 9);
		pulleyArm2 = new ModelRenderer(this, "pulleyArm2");
		pulleyArm2.setTextureOffset(63, 140);
		pulleyArm2.setTextureSize(256, 256);
		pulleyArm2.setRotationPoint(0.0f, 0.0f, 0.0f);
		setPieceRotation(pulleyArm2, 0.0f, 0.0f, 0.0f);
		pulleyArm2.addBox(-0.5f, -4.5f, -0.5f, 1, 9, 1);
		pulleyArm1.addChild(pulleyArm2);
		pulleyAxle.addChild(pulleyArm1);
		turretMidBeam.addChild(pulleyAxle);
		turretFrontBeam = new ModelRenderer(this, "turretFrontBeam");
		turretFrontBeam.setTextureOffset(0, 134);
		turretFrontBeam.setTextureSize(256, 256);
		turretFrontBeam.setRotationPoint(0.0f, 0.0f, -18.0f);
		setPieceRotation(turretFrontBeam, 0.0f, 0.0f, 0.0f);
		turretFrontBeam.addBox(-15.0f, -1.0f, -1.5f, 30, 2, 3);
		turretMidBeam.addChild(turretFrontBeam);
		turretRearBeam = new ModelRenderer(this, "turretRearBeam");
		turretRearBeam.setTextureOffset(0, 134);
		turretRearBeam.setTextureSize(256, 256);
		turretRearBeam.setRotationPoint(0.0f, 0.0f, 18.0f);
		setPieceRotation(turretRearBeam, 0.0f, 0.0f, 0.0f);
		turretRearBeam.addBox(-15.0f, -1.0f, -1.5f, 30, 2, 3);
		turretMidBeam.addChild(turretRearBeam);
		armMain = new ModelRenderer(this, "armMain");
		armMain.setTextureOffset(0, 179);
		armMain.setTextureSize(256, 256);
		armMain.setRotationPoint(0.0f, -3.0f, 0.0f);
		setPieceRotation(armMain, -0.017453417f, 0.0f, 0.0f);
		armMain.addBox(-1.0f, -1.0f, -1.5f, 2, 2, 29);
		bucket1 = new ModelRenderer(this, "bucket1");
		bucket1.setTextureOffset(63, 151);
		bucket1.setTextureSize(256, 256);
		bucket1.setRotationPoint(-4.0f, -1.5f, 27.0f);
		setPieceRotation(bucket1, 0.0f, 0.0f, 0.0f);
		bucket1.addBox(0.0f, 0.0f, 0.0f, 8, 4, 1);
		armMain.addChild(bucket1);
		bucket3 = new ModelRenderer(this, "bucket3");
		bucket3.setTextureOffset(83, 152);
		bucket3.setTextureSize(256, 256);
		bucket3.setRotationPoint(4.0f, -1.5f, 28.0f);
		setPieceRotation(bucket3, 0.0f, 0.0f, 0.0f);
		bucket3.addBox(0.0f, 0.0f, 0.0f, 1, 4, 8);
		armMain.addChild(bucket3);
		bucket6 = new ModelRenderer(this, "bucket6");
		bucket6.setTextureOffset(63, 157);
		bucket6.setTextureSize(256, 256);
		bucket6.setRotationPoint(-4.0f, -1.5f, 36.0f);
		setPieceRotation(bucket6, 0.0f, 0.0f, 0.0f);
		bucket6.addBox(0.0f, 0.0f, 0.0f, 8, 4, 1);
		armMain.addChild(bucket6);
		bucket4 = new ModelRenderer(this, "bucket4");
		bucket4.setTextureOffset(50, 170);
		bucket4.setTextureSize(256, 256);
		bucket4.setRotationPoint(-4.0f, 2.5f, 28.0f);
		setPieceRotation(bucket4, 0.0f, 0.0f, 0.0f);
		bucket4.addBox(0.0f, 0.0f, 0.0f, 8, 1, 8);
		armMain.addChild(bucket4);
		bucket2 = new ModelRenderer(this, "bucket2");
		bucket2.setTextureOffset(83, 165);
		bucket2.setTextureSize(256, 256);
		bucket2.setRotationPoint(-5.0f, -1.5f, 28.0f);
		setPieceRotation(bucket2, 0.0f, 0.0f, 0.0f);
		bucket2.addBox(0.0f, 0.0f, 0.0f, 1, 4, 8);
		armMain.addChild(bucket2);
		turretMidBeam.addChild(armMain);
		turretLeftBeam1 = new ModelRenderer(this, "turretLeftBeam1");
		turretLeftBeam1.setTextureOffset(22, 150);
		turretLeftBeam1.setTextureSize(256, 256);
		turretLeftBeam1.setRotationPoint(12.0f, 0.0f, -16.5f);
		setPieceRotation(turretLeftBeam1, 0.0f, 0.0f, 0.0f);
		turretLeftBeam1.addBox(0.0f, -1.0f, 0.0f, 3, 2, 15);
		turretMidBeam.addChild(turretLeftBeam1);
		turretLeftBeam2 = new ModelRenderer(this, "turretLeftBeam2");
		turretLeftBeam2.setTextureOffset(22, 150);
		turretLeftBeam2.setTextureSize(256, 256);
		turretLeftBeam2.setRotationPoint(12.0f, 0.0f, 1.5f);
		setPieceRotation(turretLeftBeam2, 0.0f, 0.0f, 0.0f);
		turretLeftBeam2.addBox(0.0f, -1.0f, 0.0f, 3, 2, 15);
		turretMidBeam.addChild(turretLeftBeam2);
		turretRightBeam1 = new ModelRenderer(this, "turretRightBeam1");
		turretRightBeam1.setTextureOffset(22, 150);
		turretRightBeam1.setTextureSize(256, 256);
		turretRightBeam1.setRotationPoint(-15.0f, 0.0f, -16.5f);
		setPieceRotation(turretRightBeam1, 0.0f, 0.0f, 0.0f);
		turretRightBeam1.addBox(0.0f, -1.0f, 0.0f, 3, 2, 15);
		turretMidBeam.addChild(turretRightBeam1);
		turretRightBeam2 = new ModelRenderer(this, "turretRightBeam2");
		turretRightBeam2.setTextureOffset(22, 150);
		turretRightBeam2.setTextureSize(256, 256);
		turretRightBeam2.setRotationPoint(-15.0f, 0.0f, 1.5f);
		setPieceRotation(turretRightBeam2, 0.0f, 0.0f, 0.0f);
		turretRightBeam2.addBox(0.0f, -1.0f, 0.0f, 3, 2, 15);
		turretMidBeam.addChild(turretRightBeam2);
		turretLeftAngle2 = new ModelRenderer(this, "turretLeftAngle2");
		turretLeftAngle2.setTextureOffset(0, 150);
		turretLeftAngle2.setTextureSize(256, 256);
		turretLeftAngle2.setRotationPoint(12.5f, -19.0f, 1.0f);
		setPieceRotation(turretLeftAngle2, 0.7853982f, 0.0f, 0.0f);
		turretLeftAngle2.addBox(0.0f, 0.0f, -2.0f, 2, 26, 2);
		turretMidBeam.addChild(turretLeftAngle2);
		turretRightAngle2 = new ModelRenderer(this, "turretRightAngle2");
		turretRightAngle2.setTextureOffset(0, 150);
		turretRightAngle2.setTextureSize(256, 256);
		turretRightAngle2.setRotationPoint(-14.5f, -19.0f, 1.0f);
		setPieceRotation(turretRightAngle2, 0.7853982f, 1.4044014E-7f, 0.0f);
		turretRightAngle2.addBox(0.0f, 0.0f, -2.0f, 2, 26, 2);
		turretMidBeam.addChild(turretRightAngle2);

	}

	@Override
	public void render(Entity entity, float f1, float f2, float f3, float f4, float f5, float f6) {
		super.render(entity, f1, f2, f3, f4, f5, f6);
		setRotationAngles(f1, f2, f3, f4, f5, f6, entity);
		turretMidBeam.render(f6);
	}

	public void setPieceRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setArmRotation(float pitch) {
		this.armMain.rotateAngleX = Trig.toRadians(pitch);
	}

	public void setCrankRotations(float angle) {
		this.pulleyAxle.rotateAngleX = Trig.toRadians(angle);
	}
}
