package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.tile.TileSoundBlock;

public class TemplateRuleSoundBlock extends TemplateRuleBlockTile<TileSoundBlock> {
	public static final String PLUGIN_NAME = "soundBlock";

	public TemplateRuleSoundBlock() {
		super();
	}

	public TemplateRuleSoundBlock(World world, BlockPos pos, IBlockState state, int turns) {
		super(world, pos, state, turns);
		tag.removeTag("playerSpecificValues");
	}
}
