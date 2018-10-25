package net.shadowmage.ancientwarfare.structure.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.BufferedWriter;
import java.io.IOException;

/*
 * base template-rule class.  Plugins should define their own rule classes.
 * all data to place the block/entity/target of the rule must be contained in the rule.
 * ONLY one rule per block-position in the template.  So -- no entity/block combination in same space unless
 * handled specially via a plugin rule
 *
 * @author Shadowmage
 */
public abstract class TemplateRule {

	public static final String JSON_PREFIX = "JSON:";

	public int ruleNumber = -1;

	public abstract void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder);

	public abstract void writeRuleData(NBTTagCompound tag);

	public abstract void addResources(NonNullList<ItemStack> resources);

	public abstract ItemStack getRemainingStack();

	public abstract boolean shouldPlaceOnBuildPass(World world, int turns, BlockPos pos, int buildPass);

	public abstract void parseRule(NBTTagCompound tag);

	public void writeRule(BufferedWriter out) throws IOException {
		out.write(getRuleType() + ":");
		out.newLine();
		out.write("plugin=" + getPluginName());
		out.newLine();
		out.write("number=" + ruleNumber);
		out.newLine();
		out.write("data:");
		out.newLine();
		NBTTagCompound tag = new NBTTagCompound();
		writeRuleData(tag);
		writeTag(out, tag);
		out.write(":enddata");
		out.newLine();
		out.write(":end" + getRuleType());
		out.newLine();
		out.newLine();
	}

	protected abstract String getPluginName();

	protected abstract String getRuleType();

	private void writeTag(BufferedWriter out, NBTTagCompound tag) throws IOException {
		String line = JSON_PREFIX + tag.toString();
		out.write(line);
		out.newLine();
	}

	@Override
	public String toString() {
		return "Template rule: " + ruleNumber + " type: " + getClass().getSimpleName();
	}

	protected BlockPos getBlockPosFromNBT(NBTTagCompound tag) {
		return new BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"));
	}

	protected NBTTagCompound writeBlockPosToNBT(NBTTagCompound tag, BlockPos pos) {
		tag.setInteger("x", pos.getX());
		tag.setInteger("y", pos.getY());
		tag.setInteger("z", pos.getZ());
		return tag;
	}

	public boolean placeInSurvival() {
		return false;
	}
}
