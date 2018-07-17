package net.shadowmage.ancientwarfare.core.api;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

//TODO move this class to NPC module on split
@ObjectHolder(AncientWarfareCore.MOD_ID)
@Mod.EventBusSubscriber(modid = AncientWarfareCore.MOD_ID)
public class AWSounds {

	@SubscribeEvent
	public static void register(RegistryEvent.Register<SoundEvent> event) {
		IForgeRegistry<SoundEvent> registry = event.getRegistry();

		registry.register(createSoundEvent("teleport_in"));
		registry.register(createSoundEvent("teleport_out"));
		registry.register(createSoundEvent("bard.tune1"));
		registry.register(createSoundEvent("bard.tune2"));
		registry.register(createSoundEvent("bard.tune3"));
		registry.register(createSoundEvent("bard.tune4"));
		registry.register(createSoundEvent("bard.tune5"));
		registry.register(createSoundEvent("bard.tune6"));
	}

	private static SoundEvent createSoundEvent(String soundName) {
		ResourceLocation registryName = new ResourceLocation(AncientWarfareCore.MOD_ID, soundName);
		return new SoundEvent(registryName).setRegistryName(registryName);
	}
}
