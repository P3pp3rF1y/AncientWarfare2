package net.shadowmage.ancientwarfare.structure.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

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

	public abstract List<ItemStack> getResources();

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

	public abstract String getPluginName();

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

	public boolean placeInSurvival() {
		return false;
	}
}
