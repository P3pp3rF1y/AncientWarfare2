package net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.entity_rules;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;

import java.util.Optional;

public class TemplateRuleEntityNpc extends TemplateRuleEntityLogic {

	public TemplateRuleEntityNpc() {
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
}
