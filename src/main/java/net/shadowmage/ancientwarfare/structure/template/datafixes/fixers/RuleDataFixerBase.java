package net.shadowmage.ancientwarfare.structure.template.datafixes.fixers;

import net.minecraft.util.Tuple;
import net.shadowmage.ancientwarfare.structure.template.datafixes.FixResult;
import net.shadowmage.ancientwarfare.structure.template.datafixes.IRuleFixer;

import java.util.ArrayList;
import java.util.List;

public abstract class RuleDataFixerBase implements IRuleFixer {
	@Override
	public FixResult<Tuple<String, List<String>>> fix(String ruleName, List<String> data) {
		FixResult.Builder<Tuple<String, List<String>>> resultBuilder = new FixResult.Builder<>();

		List<String> modifiedData = new ArrayList<>();
		for (String line : data) {
			modifiedData.add(resultBuilder.updateAndGetData(fixData(ruleName, line)));
		}
		return resultBuilder.build(new Tuple<>(ruleName, modifiedData));
	}

	protected abstract FixResult<String> fixData(String ruleName, String data);
}
