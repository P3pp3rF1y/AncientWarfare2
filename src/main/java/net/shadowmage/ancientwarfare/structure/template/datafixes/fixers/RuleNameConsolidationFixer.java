package net.shadowmage.ancientwarfare.structure.template.datafixes.fixers;

import com.google.common.collect.ImmutableMap;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.datafixes.FixResult;
import net.shadowmage.ancientwarfare.structure.template.datafixes.IRuleNameFixer;

import java.util.Map;

@SuppressWarnings("squid:S1192")
//need to specifically spell out the names here and the constant would live just in this place that's supposed to not be edited in the future again
public class RuleNameConsolidationFixer implements IRuleNameFixer {
	private static final StructureTemplate.Version VERSION = new StructureTemplate.Version(2, 4);

	private static final Map<String, String> nameMapping = new ImmutableMap.Builder<String, String>()
			.put("awAdvancedSpawner", "blockTile")
			.put("awCoreLogic", "blockTile")
			.put("awStructureLogic", "blockTile")
			.put("awTorqueTile", "awWorksite")
			.put("awAutomationLogic", "blockTile")
			.put("modContainerDefault", "blockTile")
			.put("modHangingDefault", "vanillaHangingEntity")
			.put("modAnimalDefault", "vanillaEntities")
			.put("modEquippedDefault", "vanillaLogicEntity")
			.put("awTownHall", "blockTile")
			.put("vanillaDoors", "doors")
			.put("vanillaInventory", "inventory")
			.put("vanillaLogic", "blockTile")
			.put("awWorksite", "rotatable")
			.build();

	@Override
	public FixResult<String> fix(String data) {
		if (nameMapping.containsKey(data)) {
			return new FixResult.Modified<>(nameMapping.get(data), "RuleNameConsolidationFixer");
		}

		return new FixResult.NotModified<>(data);
	}

	@Override
	public StructureTemplate.Version getVersion() {
		return VERSION;
	}

	@Override
	public boolean isForRule(String ruleName) {
		return nameMapping.containsKey(ruleName);
	}
}
