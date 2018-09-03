package net.shadowmage.ancientwarfare.structure.api;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException.TemplateRuleParsingException;

import java.util.List;

public abstract class TemplateRuleBlock extends TemplateRule {

	/*
	 * Called by reflection
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param block
	 * @param meta
	 * @param turns
	 */
	public TemplateRuleBlock(World world, BlockPos pos, Block block, int meta, int turns) {

	}

	/*
	 * Called by reflection
	 */
	public TemplateRuleBlock(int ruleNumber, List<String> lines) throws TemplateParsingException.TemplateRuleParsingException {
		parseRule(ruleNumber, lines);
	}

	/*
	 * should this rule be re-used in the template for the passed in block/meta parameters?
	 * common things to check are simple block ID / meta combinations.
	 * keep in mind you must rotate the passed in meta if you wish to compare it with the meta stored in your rule (you did normalize to north-oriented on construction, right?)
	 * more complex blocks may check the tile-entity for specific data
	 *
	 * @param meta  -- pure meta as from world.getblockMetaData
	 * @param turns -- 90' clockwise turns needed for proper orientation from normalized template orientation
	 * @return true if this rule can handle the input block
	 */
	public abstract boolean shouldReuseRule(World world, Block block, int meta, int turns, BlockPos pos);

	@Override
	public final void parseRule(int ruleNumber, List<String> lines) throws TemplateRuleParsingException {
		super.parseRule(ruleNumber, lines);
	}

	@Override
	protected String getRuleType() {
		return "rule";
	}
}
