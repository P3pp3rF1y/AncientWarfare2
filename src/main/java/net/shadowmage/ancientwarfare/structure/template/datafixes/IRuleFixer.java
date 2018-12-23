package net.shadowmage.ancientwarfare.structure.template.datafixes;

import net.minecraft.util.Tuple;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;

import java.util.List;

public interface IRuleFixer {
	StructureTemplate.Version getVersion();

	boolean isForRule(String ruleName);

	FixResult<Tuple<String, List<String>>> fix(String ruleName, List<String> data);
}
