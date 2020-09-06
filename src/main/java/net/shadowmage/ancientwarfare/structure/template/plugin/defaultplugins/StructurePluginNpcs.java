package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins;

import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.init.AWNPCBlocks;
import net.shadowmage.ancientwarfare.structure.api.StructureContentPlugin;
import net.shadowmage.ancientwarfare.structure.template.StructurePluginManager;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleBlockTile;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.entityrules.TemplateRuleEntityNpc;

public class StructurePluginNpcs implements StructureContentPlugin {
	@Override
	public void addHandledBlocks(StructurePluginManager manager) {
		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, AWNPCBlocks.TOWN_HALL, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
	}

	@Override
	public void addHandledEntities(StructurePluginManager manager) {
		manager.<NpcBase>registerEntityHandler(TemplateRuleEntityNpc.PLUGIN_NAME, NpcBase.class::isAssignableFrom, TemplateRuleEntityNpc::new, TemplateRuleEntityNpc::new);
	}
}
