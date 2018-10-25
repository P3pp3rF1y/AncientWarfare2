package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.entityrules;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;

import java.util.Optional;

public class TemplateRuleEntityNpc extends TemplateRuleEntityLogic {

	public static final String PLUGIN_NAME = "AWNpc";

	public TemplateRuleEntityNpc() {
		super();
	}

	public TemplateRuleEntityNpc(World world, Entity entity, int turns, int x, int y, int z) {
		super(world, entity, turns, x, y, z);
	}

	@Override
	protected Optional<Entity> createEntity(World world, int turns, BlockPos pos, IStructureBuilder builder) {
		Optional<Entity> entity = super.createEntity(world, turns, pos, builder);
		if (!entity.isPresent()) {
			return Optional.empty();
		}
		Entity e = entity.get();
		if (e instanceof NpcBase) {
			NpcBase c = (NpcBase) e;
			c.setHomeAreaAtCurrentPosition();
		}
		return Optional.of(e);
	}

	@Override
	protected String getPluginName() {
		return PLUGIN_NAME;
	}
}
