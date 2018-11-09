package net.shadowmage.ancientwarfare.structure.api;

import net.minecraftforge.fml.common.eventhandler.Event;

/*
 * Called when StructurePluginManager tries to load plugins, for each active mod
 * To be cancelled by mod wishing to have different block/entity support in structures, other than the default
 * StructurePluginModDefault
 */
public class StructurePluginRegistrationEvent extends Event {

	public final IStructurePluginRegister register;

	public StructurePluginRegistrationEvent(IStructurePluginRegister register) {
		this.register = register;
	}
}
