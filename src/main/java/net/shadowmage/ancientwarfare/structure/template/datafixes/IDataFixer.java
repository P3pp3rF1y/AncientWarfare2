package net.shadowmage.ancientwarfare.structure.template.datafixes;

import net.shadowmage.ancientwarfare.structure.template.StructureTemplate.Version;

public interface IDataFixer {
	FixResult<String> fix(String data);

	Version getVersion();

	boolean isForRule(String ruleName);
}
