package net.shadowmage.ancientwarfare.structure.template.datafixes.fixers;

import com.google.common.collect.ImmutableMap;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate.Version;
import net.shadowmage.ancientwarfare.structure.template.datafixes.FixResult;

import java.util.Map;

public class EntityRuleNameFixer extends RuleNameFixerBase {
	private static final Version VERSION = new Version(2, 7);

	private static final Map<String, String> nameMapping = new ImmutableMap.Builder<String, String>()
			.put("vanillaLogicEntity", "entity")
			.put("vanillaEntities", "entity")
			.build();

	@Override
	public Version getVersion() {
		return VERSION;
	}

	@Override
	public boolean isForRule(String ruleName) {
		return nameMapping.containsKey(ruleName);
	}

	@Override
	protected FixResult<String> fixName(String ruleName) {
		if (nameMapping.containsKey(ruleName)) {
			return new FixResult.Modified<>(nameMapping.get(ruleName), "EntityRuleNameFixer");
		}

		return new FixResult.NotModified<>(ruleName);
	}
}
