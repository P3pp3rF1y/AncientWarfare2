package net.shadowmage.ancientwarfare.structure.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public abstract class TemplateRuleEntityBase extends TemplateRule {

	private BlockPos pos;

	/*
	 * Called by reflection
	 */
	public TemplateRuleEntityBase() {}

	@Override
	public void writeRuleData(NBTTagCompound tag) {
		tag.setLong("position", pos.toLong());
	}

	@Override
	public void parseRule(NBTTagCompound tag) {
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

	@Override
	public ItemStack getRemainingStack() {
		return ItemStack.EMPTY;
	}
}
