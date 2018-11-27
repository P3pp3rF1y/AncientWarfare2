package net.shadowmage.ancientwarfare.structure.template.datafixes.fixers;

import net.minecraft.util.Tuple;
import net.shadowmage.ancientwarfare.structure.template.datafixes.FixResult;
import net.shadowmage.ancientwarfare.structure.template.datafixes.IRuleFixer;

import java.util.List;

public abstract class RuleNameFixerBase implements IRuleFixer {
	@Override
	public FixResult<Tuple<String, List<String>>> fix(String ruleName, List<String> data) {
		FixResult.Builder<Tuple<String, List<String>>> resultBuilder = new FixResult.Builder<>();
		String modifiedName = resultBuilder.updateAndGetData(fixName(ruleName));
		return resultBuilder.build(new Tuple<>(modifiedName, data));
	}

	protected abstract FixResult<String> fixName(String ruleName);
}
