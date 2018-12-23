package net.shadowmage.ancientwarfare.structure.template.datafixes.fixers;

import com.google.common.collect.ImmutableMap;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.datafixes.FixResult;

import java.util.Map;

@SuppressWarnings("squid:S1192")
//need to specifically spell out the names here and the constant would live just in this place that's supposed to not be edited in the future again
public class RuleNameConsolidationFixer extends RuleNameFixerBase {
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
			.put("modBlockDefault", "vanillaBlocks")
			.build();

	@Override
	public StructureTemplate.Version getVersion() {
		return VERSION;
	}

	@Override
	public boolean isForRule(String ruleName) {
		return nameMapping.containsKey(ruleName);
	}

	@Override
	protected FixResult<String> fixName(String ruleName) {
		if (nameMapping.containsKey(ruleName)) {
			return new FixResult.Modified<>(nameMapping.get(ruleName), "RuleNameConsolidationFixer");
		}

		return new FixResult.NotModified<>(ruleName);
	}
}
