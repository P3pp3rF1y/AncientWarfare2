package net.shadowmage.ancientwarfare.automation.render;

import codechicken.lib.render.CCModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import javax.annotation.Nullable;
import java.util.Collection;

public class TorqueJunctionRenderer extends TorqueTransportSidedRenderer {
	public static final ModelResourceLocation LIGHT_MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.MOD_ID + ":automation/torque_junction", "light");
	public static final ModelResourceLocation MEDIUM_MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.MOD_ID + ":automation/torque_junction", "medium");
	public static final ModelResourceLocation HEAVY_MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.MOD_ID + ":automation/torque_junction", "heavy");

	public static final TorqueJunctionRenderer INSTANCE = new TorqueJunctionRenderer();

	private TorqueJunctionRenderer() {
		super();
	}

	@Override
	protected void transformMovingParts(Collection<CCModel> transformedGroups, EnumFacing frontFacing, float[] rotations, @Nullable IExtendedBlockState state) {
		if (state == null) {
			transformedGroups.addAll(gearHeads[EnumFacing.NORTH.ordinal()]);
			transformedGroups.addAll(gearHeads[EnumFacing.EAST.ordinal()]);
			transformedGroups.addAll(gearHeads[EnumFacing.WEST.ordinal()]);
		} else {
			super.transformMovingParts(transformedGroups, frontFacing, rotations, state);
		}
	}
}
