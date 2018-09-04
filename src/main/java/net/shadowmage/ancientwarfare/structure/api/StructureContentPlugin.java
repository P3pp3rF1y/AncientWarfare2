package net.shadowmage.ancientwarfare.structure.api;

import net.shadowmage.ancientwarfare.structure.template.StructurePluginManager;

public interface StructureContentPlugin {

	/*
	 * implementing classes should use this callback to register any
	 * block handlers with the passed in manager
	 */
	void addHandledBlocks(StructurePluginManager manager);

	/*
	 * implementing classes should use this callback to register any
	 * entity handlers with the passed in manager
	 */
	void addHandledEntities(StructurePluginManager manager);

}
