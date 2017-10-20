package net.shadowmage.ancientwarfare.automation.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Vector3;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileStirlingGenerator;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.render.BaseBakery;
import net.shadowmage.ancientwarfare.core.render.property.CoreProperties;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StirlingGeneratorRenderer extends BaseBakery {

	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.modID + ":automation/stirling_generator", "normal");
	public static final StirlingGeneratorRenderer INSTANCE = new StirlingGeneratorRenderer();

	private final Map<String, CCModel> flywheel;
	private final Map<String, CCModel> pistonCrank;
	private final Map<String, CCModel> pistonCrank2;

	private final Map<String, CCModel> flywheelArm;
	private final Map<String, CCModel> pistonArm;
	private final Map<String, CCModel> pistonArm2;

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

	private Map<String, CCModel> removeGroups(Function<String, Boolean> filter) {
		Map<String, CCModel> ret = Maps.newHashMap();

		Iterator<Map.Entry<String, CCModel>> iterator = groups.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry<String, CCModel> entry = iterator.next();

			if (filter.apply(entry.getKey())) {
				ret.put(entry.getKey(), entry.getValue());
				iterator.remove();
			}
		}

		return ret;
	}


	@Override
	protected Map<String, CCModel> applyModelTransforms(Map<String, CCModel> modelGroups, EnumFacing face, IExtendedBlockState state) {
		Map<String, CCModel> transformedGroups = Maps.newHashMap();

		EnumFacing frontFacing = state.getValue(CoreProperties.UNLISTED_FACING);

		if (state.getValue(AutomationProperties.DYNAMIC)) {
			float rotation = state.getValue(AutomationProperties.ROTATION);
			transformMovingParts(transformedGroups, frontFacing, rotation);
		} else {
			for(Map.Entry<String, CCModel> group : modelGroups.entrySet()) {
				transformedGroups.put(group.getKey(), rotateFacing(group.getValue().copy(), frontFacing));
			}
		}

		return transformedGroups;
	}

	@Override
	protected void renderItemModels(CCRenderState ccrs) {
		super.renderItemModels(ccrs);
		Map<String, CCModel> movingParts = Maps.newHashMap();
		transformMovingParts(movingParts, EnumFacing.NORTH, 0);

		movingParts.forEach((k, m) -> m.render(ccrs, iconTransform));
	}

	private void transformMovingParts(Map<String, CCModel> transformedGroups, EnumFacing frontFacing, float rotation) {
		calculateArmAngle1(-rotation);
		calculateArmAngle2(-rotation - 90);
		Transformation flywheelRotation = new Rotation(rotation, 0, 0, 1).at(new Vector3(1d / 2d, 1d / 2d, 0));
		transformedGroups.putAll(rotateModels(flywheel, frontFacing, flywheelRotation));
		Transformation pistonCrankRotation = new Rotation(-rotation, 0, 0, 1).at(new Vector3(12d/16d, 12d/16d, 0d));
		transformedGroups.putAll(rotateModels(pistonCrank, frontFacing, pistonCrankRotation));
		transformedGroups.putAll(rotateModels(pistonCrank2, frontFacing, new Rotation(rotation, 0, 0, 1).at(new Vector3(12d/16d, 11d/16d, 0d))));
		transformedGroups.putAll(rotateModels(flywheelArm, frontFacing, new Rotation(-rotation, 0, 0, 1).at(new Vector3(9d/16d, 8d/16d, 7.5d/16d)).with(flywheelRotation)));
		transformedGroups.putAll(rotateModels(pistonArm, frontFacing, new Rotation(rotation + armAngle, 0, 0, 1).at(new Vector3(11d/16d, 12d/16d, 0d)).with(pistonCrankRotation)));
		transformedGroups.putAll(rotateModels(pistonArm2, frontFacing, new Rotation(rotation + armAngle2, 0, 0, 1).at(new Vector3(11d/16d, 12d/16d, 0d)).with(pistonCrankRotation)));
	}

	private Map<String, CCModel> rotateModels(Map<String, CCModel> groups, EnumFacing frontFacing, Transformation transform) {
		return groups.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e-> rotateFacing(e.getValue().copy().apply(transform), frontFacing)));
	}

	private CCModel rotateFacing(CCModel group, EnumFacing frontFacing) {
		return group.apply(Rotation.quarterRotations[(frontFacing.getHorizontalIndex() + 2) & 3].at(Vector3.center));
	}

	private void calculateArmAngle1(float crankAngle) {
		float ra = crankAngle;
		float crankDistance = 1.f;//side a
		float crankLength = 9.f;//side b
		calculatePistonPosition1(ra, crankDistance, crankLength);
	}

	private void calculatePistonPosition1(float crankAngleRadians, float radius, float length) {
		float cA = MathHelper.cos(crankAngleRadians);
		float sA = MathHelper.sin(crankAngleRadians);
		float pistonPos = radius * cA + MathHelper.sqrt(length * length - radius * radius * sA * sA);

		float bx = sA * radius;
		float by = cA * radius;
		float cx = 0;
		float cy = pistonPos;

		float rlrA = (float) Math.atan2(cx - bx, cy - by);
		armAngle = rlrA;
	}

	private void calculateArmAngle2(float crankAngle) {
		float ra = crankAngle;
		float crankDistance = 1.f;//side a
		float crankLength = 7.f;//side b
		calculatePistonPosition2(ra, crankDistance, crankLength);
	}

	private void calculatePistonPosition2(float crankAngleRadians, float radius, float length) {
		float cA = MathHelper.cos(crankAngleRadians);
		float sA = MathHelper.sin(crankAngleRadians);
		float pistonPos2 = radius * cA + MathHelper.sqrt(length * length - radius * radius * sA * sA);

		float bx = sA * radius;
		float by = cA * radius;
		float cx = -2f;
		float cy = pistonPos2;

		float rlrA = (float) Math.atan2(cx - bx, cy - by);
		armAngle2 = rlrA;
	}

	@Override
	public IExtendedBlockState handleState(IExtendedBlockState state, IBlockAccess access, BlockPos pos) {
		EnumFacing facing = EnumFacing.NORTH;
		TileEntity tileentity = access.getTileEntity(pos);

		if (tileentity instanceof TileStirlingGenerator) {
			TileStirlingGenerator stirlingGenerator = ((TileStirlingGenerator) tileentity);
			facing = stirlingGenerator.getPrimaryFacing();
		}

		return state.withProperty(CoreProperties.UNLISTED_FACING, facing)
				.withProperty(AutomationProperties.DYNAMIC, false)
				.withProperty(AutomationProperties.ROTATION, 0f);
	}
}