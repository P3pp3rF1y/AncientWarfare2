package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.entityrules;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

public class TemplateRuleVehicle extends TemplateRuleEntity {
	public static final String PLUGIN_NAME = "AWVehicle";

	private float turretRotation;

	public TemplateRuleVehicle() {
		super();
	}

	public TemplateRuleVehicle(World world, Entity entity, int turns, int x, int y, int z) {
		super(world, entity, turns, x, y, z);
		rotation = (entity.rotationYaw - 90.f * turns) % 360.f;
		turretRotation = (((VehicleBase) entity).localTurretDestRot - 90.f * turns) % 360.f;
	}

	@Override
	protected void updateEntityOnPlacement(int turns, BlockPos pos, Entity e) {
		e.setPositionAndRotation(pos.getX() + BlockTools.rotateFloatX(xOffset, zOffset, turns), pos.getY() + yOffset,
				pos.getZ() + BlockTools.rotateFloatZ(xOffset, zOffset, turns), (rotation - 90f * turns) % 360f, 0);

		((VehicleBase) e).localTurretRotation = (turretRotation - 90f * turns) % 360;
		((VehicleBase) e).localTurretDestRot = ((VehicleBase) e).localTurretRotation;
	}

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		super.writeRuleData(tag);
		tag.setFloat("turretRotation", turretRotation);
	}

	@Override
	public void parseRule(NBTTagCompound tag) {
		super.parseRule(tag);
		turretRotation = tag.getFloat("turretRotation");
	}
}
