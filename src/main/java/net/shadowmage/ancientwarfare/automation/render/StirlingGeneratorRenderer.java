package net.shadowmage.ancientwarfare.automation.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileStirlingGenerator;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import java.util.Collection;

public class StirlingGeneratorRenderer extends BaseTorqueRenderer<TileStirlingGenerator> {

	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.MOD_ID + ":automation/stirling_generator", "normal");
	public static final StirlingGeneratorRenderer INSTANCE = new StirlingGeneratorRenderer();

	private final Collection<CCModel> flywheel;
	private final Collection<CCModel> pistonCrank;
	private final Collection<CCModel> pistonCrank2;

	private final Collection<CCModel> flywheelArm;
	private final Collection<CCModel> pistonArm;
	private final Collection<CCModel> pistonArm2;

	private float armAngle, armAngle2;

	private StirlingGeneratorRenderer() {
		super("automation/stirling_generator.obj");

		flywheelArm = removeGroups(s -> s.startsWith("part1.flywheel_mount.flywheel2.flywheel_arm"));
		pistonArm = removeGroups(s -> s.startsWith("part1.crank_mount.piston_crank.piston_arm."));
		pistonArm2 = removeGroups(s -> s.startsWith("part1.crank_mount.piston_crank.piston_arm2."));

		flywheel = removeGroups(s -> s.startsWith("part1.flywheel_mount.flywheel2."));
		pistonCrank = removeGroups(s -> s.startsWith("part1.crank_mount.piston_crank."));
		pistonCrank2 = removeGroups(s -> s.startsWith("part1.piston_crank2."));

	}

	@Override
	protected void transformMovingParts(Collection<CCModel> transformedGroups, EnumFacing frontFacing, float[] rotations, IExtendedBlockState state) {
		float rotation = rotations[frontFacing.getIndex()];
		calculateArmAngle1(-rotation);
		calculateArmAngle2(-rotation - 90);
		Transformation flywheelRotation = new Rotation(rotation, 0, 0, 1).at(new Vector3(1d / 2d, 1d / 2d, 0));
		transformedGroups.addAll(rotateModels(flywheel, frontFacing, flywheelRotation));
		Transformation pistonCrankRotation = new Rotation(-rotation, 0, 0, 1).at(new Vector3(12d / 16d, 12d / 16d, 0d));
		transformedGroups.addAll(rotateModels(pistonCrank, frontFacing, pistonCrankRotation));
		transformedGroups.addAll(rotateModels(pistonCrank2, frontFacing, new Rotation(rotation, 0, 0, 1).at(new Vector3(12d / 16d, 11d / 16d, 0d))));
		transformedGroups.addAll(rotateModels(flywheelArm, frontFacing, new Rotation(-rotation, 0, 0, 1).at(new Vector3(9d / 16d, 8d / 16d, 7.5d / 16d)).with(flywheelRotation)));
		transformedGroups.addAll(rotateModels(pistonArm, frontFacing, new Rotation(rotation + armAngle, 0, 0, 1).at(new Vector3(11d / 16d, 12d / 16d, 0d)).with(pistonCrankRotation)));
		transformedGroups.addAll(rotateModels(pistonArm2, frontFacing, new Rotation(rotation + armAngle2, 0, 0, 1).at(new Vector3(11d / 16d, 12d / 16d, 0d)).with(pistonCrankRotation)));
	}

	private void calculateArmAngle1(float crankAngle) {
		float crankDistance = 1.f;//side a
		float crankLength = 9.f;//side b
		calculatePistonPosition1(crankAngle, crankDistance, crankLength);
	}

	private void calculatePistonPosition1(float crankAngleRadians, float radius, float length) {
		float cA = MathHelper.cos(crankAngleRadians);
		float sA = MathHelper.sin(crankAngleRadians);
		float pistonPos = radius * cA + MathHelper.sqrt(length * length - radius * radius * sA * sA);

		float bx = sA * radius;
		float by = cA * radius;
		float cx = 0;

		armAngle = (float) Math.atan2(cx - bx, pistonPos - by);
	}

	private void calculateArmAngle2(float crankAngle) {
		float crankDistance = 1.f;//side a
		float crankLength = 7.f;//side b
		calculatePistonPosition2(crankAngle, crankDistance, crankLength);
	}

	private void calculatePistonPosition2(float crankAngleRadians, float radius, float length) {
		float cA = MathHelper.cos(crankAngleRadians);
		float sA = MathHelper.sin(crankAngleRadians);
		float pistonPos2 = radius * cA + MathHelper.sqrt(length * length - radius * radius * sA * sA);

		float bx = sA * radius;
		float by = cA * radius;
		float cx = -2f;

		armAngle2 = (float) Math.atan2(cx - bx, pistonPos2 - by);
	}
}