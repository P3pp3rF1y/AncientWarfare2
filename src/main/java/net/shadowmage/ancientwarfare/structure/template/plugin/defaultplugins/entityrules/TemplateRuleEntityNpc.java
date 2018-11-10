package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.entityrules;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class TemplateRuleEntityNpc extends TemplateRuleEntity {
	public static final String PLUGIN_NAME = "AWNpc";

	public TemplateRuleEntityNpc() {
		super();
	}

	public TemplateRuleEntityNpc(World world, Entity entity, int turns, int x, int y, int z) {
		super(world, entity, turns, x, y, z);
	}

	@Override
	protected void updateEntityOnPlacement(int turns, BlockPos pos, Entity e) {
		super.updateEntityOnPlacement(turns, pos, e);
		if (e instanceof NpcBase) {
			NpcBase c = (NpcBase) e;
			c.setHomeAreaAtCurrentPosition();
		}
	}

	@Override
	protected String getPluginName() {
		return PLUGIN_NAME;
	}
}
