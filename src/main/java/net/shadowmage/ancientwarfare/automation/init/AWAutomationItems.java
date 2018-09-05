package net.shadowmage.ancientwarfare.automation.init;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.shadowmage.ancientwarfare.automation.AncientWarfareAutomation;
import net.shadowmage.ancientwarfare.automation.item.ItemWorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.item.ItemMulti;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.InjectionTools;

@GameRegistry.ObjectHolder(AncientWarfareAutomation.MOD_ID)
@Mod.EventBusSubscriber(modid = AncientWarfareAutomation.MOD_ID)
public class AWAutomationItems {
	private AWAutomationItems() {}

	public static final ItemMulti WORKSITE_UPGRADE = InjectionTools.nullValue();

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();

		ItemMulti worksiteUpgrade = new ItemWorksiteUpgrade().listenToProxy(AncientWarfareAutomation.proxy);
		worksiteUpgrade.addSubItem(WorksiteUpgrade.SIZE_MEDIUM.ordinal(), "automation/worksite_upgrade#variant=bounds_medium");
		worksiteUpgrade.addSubItem(WorksiteUpgrade.SIZE_LARGE.ordinal(), "automation/worksite_upgrade#variant=bounds_large");
		worksiteUpgrade.addSubItem(WorksiteUpgrade.QUARRY_MEDIUM.ordinal(), "automation/worksite_upgrade#variant=quarry_medium");
		worksiteUpgrade.addSubItem(WorksiteUpgrade.QUARRY_LARGE.ordinal(), "automation/worksite_upgrade#variant=quarry_large");
		worksiteUpgrade.addSubItem(WorksiteUpgrade.ENCHANTED_TOOLS_1.ordinal(), "automation/worksite_upgrade#variant=enchanted_tools_1");
		worksiteUpgrade.addSubItem(WorksiteUpgrade.ENCHANTED_TOOLS_2.ordinal(), "automation/worksite_upgrade#variant=enchanted_tools_2");
		worksiteUpgrade.addSubItem(WorksiteUpgrade.TOOL_QUALITY_1.ordinal(), "automation/worksite_upgrade#variant=quality_tools_1");
		worksiteUpgrade.addSubItem(WorksiteUpgrade.TOOL_QUALITY_2.ordinal(), "automation/worksite_upgrade#variant=quality_tools_2");
		worksiteUpgrade.addSubItem(WorksiteUpgrade.TOOL_QUALITY_3.ordinal(), "automation/worksite_upgrade#variant=quality_tools_3");
		worksiteUpgrade.addSubItem(WorksiteUpgrade.BASIC_CHUNK_LOADER.ordinal(), "automation/worksite_upgrade#variant=chunkloader_basic");
		worksiteUpgrade.addSubItem(WorksiteUpgrade.QUARRY_CHUNK_LOADER.ordinal(), "automation/worksite_upgrade#variant=chunkloader_quarry");
		registry.register(worksiteUpgrade);
	}
}
