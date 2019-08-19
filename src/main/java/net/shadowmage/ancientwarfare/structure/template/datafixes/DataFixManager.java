package net.shadowmage.ancientwarfare.structure.template.datafixes;

import net.minecraft.util.Tuple;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate.Version;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DataFixManager {

	private DataFixManager() {}

	private static List<IRuleFixer> fixes = new ArrayList<>();

	private static Version currentVersion = new Version(1, 0);

	public static Version getCurrentVersion() {
		return currentVersion;
	}

	public static void registerRuleFixer(IRuleFixer fixer) {
		if (fixer.getVersion().isGreaterThan(currentVersion)) {
			currentVersion = fixer.getVersion();
		}
		fixes.add(fixer);
	}

	public static FixResult<Tuple<String, List<String>>> fixRuleData(Version templateVersion, String ruleName, List<String> data) {
		FixResult.Builder<Tuple<String, List<String>>> resultBuilder = new FixResult.Builder<>();

		List<String> modifiedData = data;
		String modifiedRuleName = ruleName;
		for (IRuleFixer fixer : fixes.stream().filter(f -> f.getVersion().isGreaterThan(templateVersion)).sorted(VERSION_ASCENDING).collect(Collectors.toList())) {
			if (fixer.isForRule(modifiedRuleName)) {
				Tuple<String, List<String>> fixed = resultBuilder.updateAndGetData(fixer.fix(modifiedRuleName, modifiedData));
				modifiedRuleName = fixed.getFirst();
				modifiedData = fixed.getSecond();
			}
		}
		return resultBuilder.build(new Tuple<>(modifiedRuleName, modifiedData));
	}

	private static final Comparator<IRuleFixer> VERSION_ASCENDING = (o1, o2) -> {
		if (o1.getVersion().isGreaterThan(o2.getVersion())) {
			return 1;
		} else if (o2.getVersion().isGreaterThan(o1.getVersion())) {
			return -1;
		}
		return 0;
	};
}
