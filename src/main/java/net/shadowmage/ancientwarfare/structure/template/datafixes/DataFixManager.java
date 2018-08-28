package net.shadowmage.ancientwarfare.structure.template.datafixes;

import net.shadowmage.ancientwarfare.structure.template.StructureTemplate.Version;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DataFixManager {

	private DataFixManager() {}

	private static List<IDataFixer> ruleFixes = new ArrayList<>();

	public static void registerRuleFixer(IDataFixer fixer) {
		ruleFixes.add(fixer);
	}

	public static FixResult<List<String>> fixRuleData(Version templateVersion, String ruleName, List<String> data) {
		FixResult.Builder<List<String>> resultBuilder = new FixResult.Builder<>();

		List<String> modifiedData = new ArrayList<>();
		for (String line : data) {
			for (IDataFixer fixer : ruleFixes.stream().filter(f -> f.isForRule(ruleName)).sorted(VERSION_ASCENDING).collect(Collectors.toList())) {
				if (fixer.getVersion().isGreaterThan(templateVersion)) {
					line = resultBuilder.updateAndGetData(fixer.fix(line));
				}
			}
			modifiedData.add(line);
		}

		return resultBuilder.build(modifiedData);
	}

	private static final Comparator<IDataFixer> VERSION_ASCENDING = (o1, o2) -> {
		if (o1.getVersion().isGreaterThan(o2.getVersion())) {
			return 1;
		} else if (o2.getVersion().isGreaterThan(o1.getVersion())) {
			return -1;
		}
		return 0;
	};
}
