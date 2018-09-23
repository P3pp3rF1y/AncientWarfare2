package net.shadowmage.ancientwarfare.structure.template.datafixes;

import net.shadowmage.ancientwarfare.structure.template.StructureTemplate.Version;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
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
			modifiedData.add(fixData(ruleFixes, templateVersion, ruleName, resultBuilder, line, (f, d) -> ((IDataFixer) f).fix(ruleName, d)));
		}

		return resultBuilder.build(modifiedData);
	}

	private static <T> String fixData(List<? extends IFixer> fixes, Version templateVersion, String ruleName, FixResult.Builder<T> resultBuilder, String data,
			BiFunction<IFixer, String, FixResult<String>> doFix) {
		String ret = data;
		for (IFixer fixer : fixes.stream().filter(f -> f.isForRule(ruleName)).sorted(VERSION_ASCENDING).collect(Collectors.toList())) {
			if (fixer.getVersion().isGreaterThan(templateVersion)) {
				ret = resultBuilder.updateAndGetData(doFix.apply(fixer, ret));
			}
		}
		return ret;
	}

	public static FixResult<String> fixRuleName(Version templateVersion, String ruleName) {
		FixResult.Builder<String> ret = new FixResult.Builder<>();
		return ret.build(fixData(ruleNameFixes, templateVersion, ruleName, ret, ruleName, (f, n) -> ((IRuleNameFixer) f).fix(n)));
	}

	private static final Comparator<IFixer> VERSION_ASCENDING = (o1, o2) -> {
		if (o1.getVersion().isGreaterThan(o2.getVersion())) {
			return 1;
		} else if (o2.getVersion().isGreaterThan(o1.getVersion())) {
			return -1;
		}
		return 0;
	};
}
