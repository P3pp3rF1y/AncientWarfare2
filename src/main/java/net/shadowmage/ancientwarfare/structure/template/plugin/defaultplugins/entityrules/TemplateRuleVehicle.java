package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.entityrules;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

public class TemplateRuleVehicle extends TemplateRuleEntity {
	public static final String PLUGIN_NAME = "AWVehicle";

	public TemplateRuleVehicle() {
		super();
	}

	public TemplateRuleVehicle(World world, Entity entity, int turns, int x, int y, int z) {
		super(world, entity, turns, x, y, z);
	}

	@Override
	protected void updateEntityOnPlacement(int turns, BlockPos pos, Entity e) {
		e.setPositionAndRotation(pos.getX() + BlockTools.rotateFloatX(xOffset, zOffset, turns), pos.getY() + yOffset,
				pos.getZ() + BlockTools.rotateFloatZ(xOffset, zOffset, turns), (rotation - (180f + 90f * turns)) % 360f, 0);
	}

	@Override
	protected String getPluginName() {
		return PLUGIN_NAME;
	}
}
