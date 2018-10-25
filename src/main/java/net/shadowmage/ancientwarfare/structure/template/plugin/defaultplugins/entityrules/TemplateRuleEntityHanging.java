package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.entityrules;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;

import java.util.Optional;

public class TemplateRuleEntityHanging extends TemplateRuleVanillaEntity {
	public static final String PLUGIN_NAME = "vanillaHangingEntity";
	private NBTTagCompound tag;
	private EnumFacing direction;

	public TemplateRuleEntityHanging(World world, Entity entity, int turns, int x, int y, int z) {
		super(world, entity, turns, x, y, z);
		EntityHanging hanging = (EntityHanging) entity;
		tag = new NBTTagCompound();
		entity.writeToNBT(tag);
		this.direction = EnumFacing.HORIZONTALS[(Optional.ofNullable(hanging.facingDirection).map(Enum::ordinal).orElse(0) + turns) % 4];
		tag.removeTag("UUIDMost");
		tag.removeTag("UUIDLeast");
	}

	public TemplateRuleEntityHanging() {
		super();
	}

	@Override
	public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		Entity e = EntityList.createEntityByIDFromName(registryName, world);
		if (e == null) {
			AncientWarfareStructure.LOG.warn("Could not create entity for type: " + registryName.toString());
			return;
		}
		EnumFacing rotateDirection = EnumFacing.HORIZONTALS[(this.direction.ordinal() + turns) % 4];
		tag.setByte("Facing", (byte) rotateDirection.getHorizontalIndex());
		NBTTagList posList = new NBTTagList();
		posList.appendTag(new NBTTagDouble(pos.getX()));
		posList.appendTag(new NBTTagDouble(pos.getY()));
		posList.appendTag(new NBTTagDouble(pos.getZ()));
		tag.setTag("Pos", posList);
		tag.setInteger("TileX", pos.getX());
		tag.setInteger("TileY", pos.getY());
		tag.setInteger("TileZ", pos.getZ());
		e.readFromNBT(tag);
		world.spawnEntity(e);
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
