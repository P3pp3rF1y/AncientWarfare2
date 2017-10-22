package net.shadowmage.ancientwarfare.automation.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import java.util.Map;

public class HandCrankedGeneratorRenderer extends BaseTorqueRenderer {

	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.modID + ":automation/hand_cranked_generator", "normal");
	public static final HandCrankedGeneratorRenderer INSTANCE = new HandCrankedGeneratorRenderer();

	private final Map<String, CCModel> outputGear;
	private final Map<String, CCModel> inputGear;

	private HandCrankedGeneratorRenderer() {
		super("automation/hand_cranked_generator.obj");

		outputGear = removeGroups(s -> s.startsWith("base.outputGear."));
		inputGear = removeGroups(s -> s.startsWith("base.inputGear."));
	}

	@Override
	protected void transformMovingParts(Map<String, CCModel> transformedGroups, EnumFacing frontFacing, float[] rotations, IExtendedBlockState state) {
		float inR = -rotations[EnumFacing.UP.getIndex()];
		float outR = -rotations[frontFacing.getIndex()];
		transformedGroups.putAll(rotateModels(outputGear, frontFacing, new Rotation(outR, 0, 0, 1).at(new Vector3(8d/16d, 8d/16d, 8d/16d))));
		transformedGroups.putAll(rotateModels(inputGear, frontFacing, new Rotation(inR, 0, 1, 0).at(new Vector3(8d/16d, 10.5d/16d, 11.5d/16d))));
	}
}