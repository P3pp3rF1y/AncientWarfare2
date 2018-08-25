package net.shadowmage.ancientwarfare.structure.template.datafixes;

import net.shadowmage.ancientwarfare.structure.template.StructureTemplate.Version;

public interface IDataFixer {
	String fix(String line);

	Version getVersion();
}
