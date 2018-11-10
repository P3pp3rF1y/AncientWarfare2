package net.shadowmage.ancientwarfare.structure.template.datafixes.fixers;

import com.google.common.collect.ImmutableMap;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate.Version;
import net.shadowmage.ancientwarfare.structure.template.datafixes.FixResult;
import net.shadowmage.ancientwarfare.structure.template.datafixes.IRuleNameFixer;

import java.util.Map;

public class EntityRuleNameFixer implements IRuleNameFixer {
	private static final Version VERSION = new Version(2, 7);

	private static final Map<String, String> nameMapping = new ImmutableMap.Builder<String, String>()
			.put("vanillaLogicEntity", "entity")
			.put("vanillaEntities", "entity")
			.build();

	@Override
	public FixResult<String> fix(String data) {
		if (nameMapping.containsKey(data)) {
			return new FixResult.Modified<>(nameMapping.get(data), "EntityRuleNameFixer");
		}

		return new FixResult.NotModified<>(data);
	}

	@Override
	public Version getVersion() {
		return VERSION;
	}

	@Override
	public boolean isForRule(String ruleName) {
		return nameMapping.containsKey(ruleName);
	}
}
