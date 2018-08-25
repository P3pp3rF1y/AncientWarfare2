package net.shadowmage.ancientwarfare.structure.template.datafixes;

import net.shadowmage.ancientwarfare.structure.template.StructureTemplate.Version;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataFixManager {
	private DataFixManager() {}

	private static Map<String, List<IDataFixer>> ruleFixes = new HashMap<>();

	public static void registerRuleFixer(String ruleName, IDataFixer fixer) {
		ruleFixes.computeIfAbsent(ruleName, n -> new ArrayList<>()).add(fixer);
	}

	public static List<String> fixRuleData(Version templateVersion, String ruleName, List<String> data) {
		if (!ruleFixes.containsKey(ruleName)) {
			return data;
		}

		List<String> modifiedData = new ArrayList<>();

		for (String line : data) {
			for (IDataFixer fixer : ruleFixes.get(ruleName).stream().sorted((o1, o2) -> {
				if (o1.getVersion().isGreaterThan(o2.getVersion())) {
					return 1;
				} else if (o2.getVersion().isGreaterThan(o1.getVersion())) {
					return -1;
				}
				return 0;
			}).collect(Collectors.toList())) {
				if (fixer.getVersion().isGreaterThan(templateVersion)) {
					modifiedData.add(fixer.fix(line));
				}
			}
		}
		return modifiedData;
	}
}
