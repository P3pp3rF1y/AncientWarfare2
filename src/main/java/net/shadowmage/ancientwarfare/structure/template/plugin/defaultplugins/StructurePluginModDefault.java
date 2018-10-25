package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins;

import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.shadowmage.ancientwarfare.structure.api.StructureContentPlugin;
import net.shadowmage.ancientwarfare.structure.template.StructurePluginManager;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.entityrules.TemplateRuleEntityHanging;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.entityrules.TemplateRuleEntityLogic;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.entityrules.TemplateRuleVanillaEntity;

import java.util.Optional;

public class StructurePluginModDefault implements StructureContentPlugin {

	private final String mod;

	public StructurePluginModDefault(String id) {
		this.mod = id;
	}

	@Override
	public void addHandledBlocks(StructurePluginManager manager) {
	}

	@Override
	public void addHandledEntities(StructurePluginManager manager) {
		for (ResourceLocation key : ForgeRegistries.ENTITIES.getKeys()) {
			if (key.toString().startsWith(mod)) {
				Optional.ofNullable(EntityList.getClass(key)).ifPresent(clazz -> {
					if (EntityHanging.class.isAssignableFrom(clazz)) {
						manager.registerEntityHandler(TemplateRuleEntityHanging.PLUGIN_NAME, clazz, TemplateRuleEntityHanging::new, TemplateRuleEntityHanging::new);
					} else if (EntityAnimal.class.isAssignableFrom(clazz)) {
						manager.registerEntityHandler(TemplateRuleVanillaEntity.PLUGIN_NAME, clazz, TemplateRuleVanillaEntity::new, TemplateRuleVanillaEntity::new);
					} else if (EntityLiving.class.isAssignableFrom(clazz) || IInventory.class.isAssignableFrom(clazz)) {
						manager.registerEntityHandler(TemplateRuleEntityLogic.PLUGIN_NAME, clazz, TemplateRuleEntityLogic::new, TemplateRuleEntityLogic::new);
					}
				});
			}
		}
	}

}
