package net.shadowmage.ancientwarfare.structure.api;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public abstract class TemplateRuleEntity extends TemplateRule {

	private BlockPos pos;

	/*
	 * Called by reflection
	 * @param world
	 * @param entity
	 * @param turns
	 * @param x
	 * @param y
	 * @param z
	 */
	public TemplateRuleEntity(World world, Entity entity, int turns, int x, int y, int z) {

	}

	/*
	 * Called by reflection
	 */
	public TemplateRuleEntity(int ruleNumber, List<String> lines) throws TemplateParsingException.TemplateRuleParsingException {
		parseRule(ruleNumber, lines);
	}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		tag.setLong("position", pos.toLong());
	}

	@Override
	public void parseRuleData(NBTTagCompound tag) {
		pos = BlockPos.fromLong(tag.getLong("position"));
	}

	@Override
	protected String getRuleType() {
		return "entity";
	}

	public final void setPosition(BlockPos pos) {
		this.pos = pos;
	}

	public final BlockPos getPosition() {
		return pos;
	}

}
