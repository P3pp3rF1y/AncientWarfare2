package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.init.AWNPCBlocks;
import net.shadowmage.ancientwarfare.structure.api.StructureContentPlugin;
import net.shadowmage.ancientwarfare.structure.template.StructurePluginManager;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleBlockTile;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.entityrules.TemplateRuleEntityNpc;

public class StructurePluginNpcs implements StructureContentPlugin {

	public StructurePluginNpcs() {

	}

	@Override
	public void addHandledBlocks(StructurePluginManager manager) {
		manager.registerBlockHandler(TemplateRuleBlockTile.PLUGIN_NAME, AWNPCBlocks.TOWN_HALL, TemplateRuleBlockTile::new, TemplateRuleBlockTile::new);
	}

	@Override
	public void addHandledEntities(StructurePluginManager manager) {
		for (ResourceLocation registryName : ForgeRegistries.ENTITIES.getKeys()) {
			Class<? extends Entity> clazz = EntityList.getClass(registryName);
			if (NpcBase.class.isAssignableFrom(clazz)) {
				manager.registerEntityHandler(TemplateRuleEntityNpc.PLUGIN_NAME, clazz, TemplateRuleEntityNpc::new, TemplateRuleEntityNpc::new);
			}
		}
	}

}
