package net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.entity_rules;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.structure.api.IStructureBuilder;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuildingException.EntityPlacementException;

public class TemplateRuleEntityNpc extends TemplateRuleEntityLogic {

    public TemplateRuleEntityNpc() {
    }

    public TemplateRuleEntityNpc(World world, Entity entity, int turns, int x, int y, int z) {
        super(world, entity, turns, x, y, z);
    }

    @Override
    protected Entity createEntity(World world, int turns, int x, int y, int z, IStructureBuilder builder) throws EntityPlacementException {
        Entity e = super.createEntity(world, turns, x, y, z, builder);
        if (e instanceof NpcBase) {
            NpcBase c = (NpcBase) e;
            c.setHomeAreaAtCurrentPosition();
        }
        return e;
    }
}
