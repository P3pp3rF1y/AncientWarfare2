package net.shadowmage.ancientwarfare.structure.template.datafixes;

import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;

public interface IFixer {
	StructureTemplate.Version getVersion();

	boolean isForRule(String ruleName);
}
