package net.shadowmage.ancientwarfare.structure.api;

public interface IStructurePluginRegister {

	@SuppressWarnings("unused")
		//is implemented in StructurePluginManager and supposed to be called by mods
	void registerPlugin(StructureContentPlugin plugin);
}
