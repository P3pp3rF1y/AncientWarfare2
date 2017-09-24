package net.shadowmage.ancientwarfare.core.api;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

@ObjectHolder(AncientWarfareCore.modID)
public class AWSounds {

	@ObjectHolder("teleport_out")
	public static SoundEvent TOWN_HALL_TELEPORT_OUT;
	@ObjectHolder("teleport_in")
	public static SoundEvent TOWN_HALL_TELEPORT_IN;

	@SubscribeEvent
	public static void register(RegistryEvent.Register<SoundEvent> event) {
		IForgeRegistry<SoundEvent> registry = event.getRegistry();

		registry.register(createSoundEvent("teleport_in"));
		registry.register(createSoundEvent("teleport_out"));
	}

	private static SoundEvent createSoundEvent(String soundName) {
		ResourceLocation registryName = new ResourceLocation(AncientWarfareCore.modID, soundName);
		return new SoundEvent(registryName).setRegistryName(registryName);
	}
}
