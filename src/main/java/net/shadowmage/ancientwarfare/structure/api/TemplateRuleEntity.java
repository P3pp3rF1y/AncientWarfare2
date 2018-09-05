package net.shadowmage.ancientwarfare.structure.api;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException.TemplateRuleParsingException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

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
	public final void writeRule(BufferedWriter out) throws IOException {
		out.write("position=" + NBTTools.getCSVStringForArray(new int[] {pos.getX(), pos.getY(), pos.getZ()}));
		out.newLine();
		super.writeRule(out);
	}

	@Override
	protected String getRuleType() {
		return "entity";
	}

	@Override
	public final void parseRule(int ruleNumber, List<String> lines) throws TemplateRuleParsingException {
		this.ruleNumber = ruleNumber;
		for (String line : lines) {
			if (line.toLowerCase(Locale.ENGLISH).startsWith("position=")) {
				int[] posArray = NBTTools.safeParseIntArray("=", line);
				pos = new BlockPos(posArray[0], posArray[1], posArray[2]);
				break;
			}
		}
		NBTTagCompound tag = readTag(lines);
		parseRuleData(tag);
	}

	public final void setPosition(BlockPos pos) {
		this.pos = pos;
	}

	public final BlockPos getPosition() {
		return pos;
	}

}
