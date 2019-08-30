package net.shadowmage.ancientwarfare.structure.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;
import net.shadowmage.ancientwarfare.core.util.InjectionTools;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;

@ObjectHolder(AncientWarfareStructure.MOD_ID)
@Mod.EventBusSubscriber(modid = AncientWarfareStructure.MOD_ID)
public class AWStructureSounds {
	public static final SoundEvent PROTECTION_FLAG_CLAIM = InjectionTools.nullValue();
	public static final SoundEvent COFFIN_OPENS = InjectionTools.nullValue();
	public static final SoundEvent URN_BREAK = InjectionTools.nullValue();
	public static final SoundEvent COINSTACK_BREAK = InjectionTools.nullValue();
	public static final SoundEvent COINSTACK_INTERACT = InjectionTools.nullValue();

	private AWStructureSounds() {}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<SoundEvent> event) {
		IForgeRegistry<SoundEvent> registry = event.getRegistry();

		registry.register(createSoundEvent("protection_flag_claim"));
		registry.register(createSoundEvent("coffin_opens"));
		registry.register(createSoundEvent("urn_break"));
		registry.register(createSoundEvent("coinstack_break"));
		registry.register(createSoundEvent("coinstack_interact"));
	}

	private static SoundEvent createSoundEvent(String soundName) {
		ResourceLocation registryName = new ResourceLocation(AncientWarfareStructure.MOD_ID, soundName);
		return new SoundEvent(registryName).setRegistryName(registryName);
	}
}
