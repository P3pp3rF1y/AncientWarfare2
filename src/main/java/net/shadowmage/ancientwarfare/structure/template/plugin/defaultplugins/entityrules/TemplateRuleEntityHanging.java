package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.entityrules;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class TemplateRuleEntityHanging extends TemplateRuleEntity {
	public static final String PLUGIN_NAME = "vanillaHangingEntity";
	private NBTTagCompound tag;
	private EnumFacing direction;

	public TemplateRuleEntityHanging(World world, Entity entity, int turns, int x, int y, int z) {
		super(world, entity, turns, x, y, z);
		EntityHanging hanging = (EntityHanging) entity;
		this.direction = EnumFacing.HORIZONTALS[(Optional.ofNullable(hanging.facingDirection).map(Enum::ordinal).orElse(0) + turns) % 4];
	}

	public TemplateRuleEntityHanging() {
		super();
	}

	@Override
	protected NBTTagCompound getEntityNBT(BlockPos pos, int turns) {
		NBTTagCompound tag = super.getEntityNBT(pos, turns);

		EnumFacing rotateDirection = EnumFacing.HORIZONTALS[(this.direction.ordinal() + turns) % 4];
		tag.setByte("Facing", (byte) rotateDirection.getHorizontalIndex());
		tag.setInteger("TileX", pos.getX());
		tag.setInteger("TileY", pos.getY());
		tag.setInteger("TileZ", pos.getZ());

		return tag;
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		super.writeRuleData(tag);
		tag.setByte("direction", (byte) direction.ordinal());
		tag.setTag("entityData", this.tag);
	}

	@Override
	public void parseRule(NBTTagCompound tag) {
		super.parseRule(tag);
		this.tag = tag.getCompoundTag("entityData");
		this.direction = EnumFacing.VALUES[tag.getByte("direction")];
	}

	@Override
	protected String getPluginName() {
		return PLUGIN_NAME;
	}
}
