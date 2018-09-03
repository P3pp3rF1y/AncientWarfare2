package net.shadowmage.ancientwarfare.structure.template.datafixes;

import net.shadowmage.ancientwarfare.structure.template.StructureTemplate.Version;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DataFixManager {

	private DataFixManager() {}

	private static List<IRuleNameFixer> ruleNameFixes = new ArrayList<>();
	private static List<IDataFixer> ruleFixes = new ArrayList<>();

	public static void registerRuleFixer(IDataFixer fixer) {
		ruleFixes.add(fixer);
	}

	public static void registerRuleNameFixer(IRuleNameFixer fixer) {
		ruleNameFixes.add(fixer);
	}

	public static FixResult<List<String>> fixRuleData(Version templateVersion, String ruleName, List<String> data) {
		FixResult.Builder<List<String>> resultBuilder = new FixResult.Builder<>();

		List<String> modifiedData = new ArrayList<>();
		for (String line : data) {
			modifiedData.add(fixData(ruleFixes, templateVersion, ruleName, resultBuilder, line));
		}

		return resultBuilder.build(modifiedData);
	}

	private static <T> String fixData(List<? extends IDataFixer> fixes, Version templateVersion, String ruleName, FixResult.Builder<T> resultBuilder, String data) {
		String ret = data;
		for (IDataFixer fixer : fixes.stream().filter(f -> f.isForRule(ruleName)).sorted(VERSION_ASCENDING).collect(Collectors.toList())) {
			if (fixer.getVersion().isGreaterThan(templateVersion)) {
				ret = resultBuilder.updateAndGetData(fixer.fix(data));
			}
		}
		return ret;
	}

	public static FixResult<String> fixRuleName(Version templateVersion, String ruleName) {
		FixResult.Builder<String> ret = new FixResult.Builder<>();
		return ret.build(fixData(ruleNameFixes, templateVersion, ruleName, ret, ruleName));
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
