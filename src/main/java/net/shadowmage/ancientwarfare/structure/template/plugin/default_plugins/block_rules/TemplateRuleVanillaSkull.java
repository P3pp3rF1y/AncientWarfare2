package net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;

public class TemplateRuleVanillaSkull extends TemplateRuleBlockLogic {

    int rotation;

    public TemplateRuleVanillaSkull(World world, BlockPos pos, Block block, int meta, int turns) {
        super(world, pos, block, meta, turns);
        int t = tag.getInteger("Rot");
        AWLog.logDebug("base rot: " + t);
        t += 4 * turns;
        t += 8;//rotate to opposite, no clue why this is needed...
        t %= 16;
        AWLog.logDebug("rotated Rot: " + t);
    }

    public TemplateRuleVanillaSkull() {
    }

    @Override
    public void handlePlacement(World world, int turns, BlockPos pos, IStructureBuilder builder) {
        tag.setInteger("Rot", (rotation + 4 * turns) % 16);
        AWLog.logDebug("pre-place rot: " + tag.getInteger("Rot"));
        super.handlePlacement(world, turns, pos, builder);
    }

}
