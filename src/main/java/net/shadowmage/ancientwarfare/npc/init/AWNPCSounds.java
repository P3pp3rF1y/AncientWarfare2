package net.shadowmage.ancientwarfare.npc.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;
import net.shadowmage.ancientwarfare.core.util.InjectionTools;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;

import java.util.HashMap;
import java.util.Map;

@ObjectHolder(AncientWarfareNPC.MOD_ID)
@Mod.EventBusSubscriber(modid = AncientWarfareNPC.MOD_ID)
public class AWNPCSounds {

	public static Map<String, SoundEvent> npcSoundEvents = new HashMap<>();

	public static final SoundEvent BARBARIAN_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent BARBARIAN_HURT = InjectionTools.nullValue();
	public static final SoundEvent BEAST_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent BEAST_DEATH = InjectionTools.nullValue();
	public static final SoundEvent BEAST_HURT = InjectionTools.nullValue();
	public static final SoundEvent BRIGAND_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent BRIGAND_DEATH = InjectionTools.nullValue();
	public static final SoundEvent BRIGAND_HURT = InjectionTools.nullValue();
	public static final SoundEvent COVEN_DRYAD_DEATH = InjectionTools.nullValue();
	public static final SoundEvent COVEN_DRYAD_HURT = InjectionTools.nullValue();
	public static final SoundEvent COVEN_FAMILIAR_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent COVEN_FAMILIAR_DEATH = InjectionTools.nullValue();
	public static final SoundEvent COVEN_FAMILIAR_HURT = InjectionTools.nullValue();
	public static final SoundEvent COVEN_PUPPET_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent COVEN_PUPPET_DEATH = InjectionTools.nullValue();
	public static final SoundEvent COVEN_PUPPET_HURT = InjectionTools.nullValue();
	public static final SoundEvent COVEN_SATYR_DEATH = InjectionTools.nullValue();
	public static final SoundEvent COVEN_SATYR_HURT = InjectionTools.nullValue();
	public static final SoundEvent COVEN_SCARECROW_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent COVEN_SCARECROW_DEATH = InjectionTools.nullValue();
	public static final SoundEvent COVEN_SCARECROW_HURT = InjectionTools.nullValue();
	public static final SoundEvent COVEN_WITCH_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent COVEN_WITCH_DEATH = InjectionTools.nullValue();
	public static final SoundEvent COVEN_WITCH_HURT = InjectionTools.nullValue();
	public static final SoundEvent DWARF_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent DWARF_DEATH = InjectionTools.nullValue();
	public static final SoundEvent DWARF_HURT = InjectionTools.nullValue();
	public static final SoundEvent ELF_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ELF_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ELF_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENT_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENT_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENT_HURT = InjectionTools.nullValue();
	public static final SoundEvent GARGOYLE_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent GARGOYLE_DEATH = InjectionTools.nullValue();
	public static final SoundEvent GARGOYLE_HURT = InjectionTools.nullValue();
	public static final SoundEvent GIANT_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent GIANT_DEATH = InjectionTools.nullValue();
	public static final SoundEvent GIANT_HURT = InjectionTools.nullValue();
	public static final SoundEvent GNOME_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent GNOME_DEATH = InjectionTools.nullValue();
	public static final SoundEvent GNOME_HURT = InjectionTools.nullValue();
	public static final SoundEvent GREMLIN_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent GREMLIN_DEATH = InjectionTools.nullValue();
	public static final SoundEvent GREMLIN_HURT = InjectionTools.nullValue();
	public static final SoundEvent HOBBIT_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent HOBBIT_DEATH = InjectionTools.nullValue();
	public static final SoundEvent HOBBIT_HURT = InjectionTools.nullValue();
	public static final SoundEvent HUMAN_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent HUMAN_DEATH = InjectionTools.nullValue();
	public static final SoundEvent HUMAN_FEMALE_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent HUMAN_FEMALE_DEATH = InjectionTools.nullValue();
	public static final SoundEvent HUMAN_FEMALE_HURT = InjectionTools.nullValue();
	public static final SoundEvent HUMAN_HURT = InjectionTools.nullValue();
	public static final SoundEvent ISHTARI_ANUBITE_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ISHTARI_ANUBITE_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ISHTARI_ANUBITE_HURT = InjectionTools.nullValue();
	public static final SoundEvent ISHTARI_MUMMY_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ISHTARI_MUMMY_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ISHTARI_MUMMY_HURT = InjectionTools.nullValue();
	public static final SoundEvent ISHTARI_PHAROAH_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ISHTARI_PHAROAH_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ISHTARI_PHAROAH_HURT = InjectionTools.nullValue();
	public static final SoundEvent KLOWN_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent KLOWN_DEATH = InjectionTools.nullValue();
	public static final SoundEvent KLOWN_HURT = InjectionTools.nullValue();
	public static final SoundEvent KOBOLD_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent KOBOLD_DEATH = InjectionTools.nullValue();
	public static final SoundEvent KOBOLD_HURT = InjectionTools.nullValue();
	public static final SoundEvent KONG_APE_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent KONG_APE_DEATH = InjectionTools.nullValue();
	public static final SoundEvent KONG_APE_HURT = InjectionTools.nullValue();
	public static final SoundEvent LIZARDMAN_AMBIENT = InjectionTools.nullValue();
	public static final SoundEvent LIZARDMAN_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent LIZARDMAN_DEATH = InjectionTools.nullValue();
	public static final SoundEvent LIZARDMAN_HURT = InjectionTools.nullValue();
	public static final SoundEvent MALICE_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent MALICE_DEATH = InjectionTools.nullValue();
	public static final SoundEvent MALICE_HURT = InjectionTools.nullValue();
	public static final SoundEvent MONSTER_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent MONSTER_DEATH = InjectionTools.nullValue();
	public static final SoundEvent MONSTER_HURT = InjectionTools.nullValue();
	public static final SoundEvent NORSKA_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent NORSKA_DEATH = InjectionTools.nullValue();
	public static final SoundEvent NORSKA_HURT = InjectionTools.nullValue();
	public static final SoundEvent ORC_AMBIENT = InjectionTools.nullValue();
	public static final SoundEvent ORC_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ORC_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ORC_HURT = InjectionTools.nullValue();
	public static final SoundEvent ORC_URUK_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ORC_URUK_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ORC_URUK_HURT = InjectionTools.nullValue();
	public static final SoundEvent PIRATE_AMBIENT = InjectionTools.nullValue();
	public static final SoundEvent PIRATE_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent PIRATE_DEATH = InjectionTools.nullValue();
	public static final SoundEvent PIRATE_HURT = InjectionTools.nullValue();
	public static final SoundEvent SKELETON_DEATH = InjectionTools.nullValue();
	public static final SoundEvent SKELETON_HURT = InjectionTools.nullValue();
	public static final SoundEvent VAMPIRE_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent VAMPIRE_BOSS_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent VAMPIRE_BOSS_DEATH = InjectionTools.nullValue();
	public static final SoundEvent VAMPIRE_BOSS_HURT = InjectionTools.nullValue();
	public static final SoundEvent VAMPIRE_BRIDE_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent VAMPIRE_BRIDE_DEATH = InjectionTools.nullValue();
	public static final SoundEvent VAMPIRE_BRIDE_HURT = InjectionTools.nullValue();
	public static final SoundEvent VAMPIRE_DEATH = InjectionTools.nullValue();
	public static final SoundEvent VAMPIRE_HURT = InjectionTools.nullValue();
	public static final SoundEvent ZOMBIE_AMBIENT = InjectionTools.nullValue();
	public static final SoundEvent ZOMBIE_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ZOMBIE_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ZOMBIE_HURT = InjectionTools.nullValue();

	private AWNPCSounds() {}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<SoundEvent> event) {
		IForgeRegistry<SoundEvent> registry = event.getRegistry();

		registry.register(createSoundEvent("bard.tune1"));
		registry.register(createSoundEvent("bard.tune2"));
		registry.register(createSoundEvent("bard.tune3"));
		registry.register(createSoundEvent("bard.tune4"));
		registry.register(createSoundEvent("bard.tune5"));
		registry.register(createSoundEvent("bard.tune6"));
		registry.register(createSoundEvent("teleport_in"));
		registry.register(createSoundEvent("teleport_out"));
		registry.register(createSoundEvent("barbarian_attack"));
		registry.register(createSoundEvent("barbarian_hurt"));
		registry.register(createSoundEvent("beast_attack"));
		registry.register(createSoundEvent("beast_death"));
		registry.register(createSoundEvent("beast_hurt"));
		registry.register(createSoundEvent("brigand_attack"));
		registry.register(createSoundEvent("brigand_death"));
		registry.register(createSoundEvent("brigand_hurt"));
		registry.register(createSoundEvent("coven_dryad_death"));
		registry.register(createSoundEvent("coven_dryad_hurt"));
		registry.register(createSoundEvent("coven_familiar_attack"));
		registry.register(createSoundEvent("coven_familiar_death"));
		registry.register(createSoundEvent("coven_familiar_hurt"));
		registry.register(createSoundEvent("coven_puppet_attack"));
		registry.register(createSoundEvent("coven_puppet_death"));
		registry.register(createSoundEvent("coven_puppet_hurt"));
		registry.register(createSoundEvent("coven_satyr_death"));
		registry.register(createSoundEvent("coven_satyr_hurt"));
		registry.register(createSoundEvent("coven_scarecrow_attack"));
		registry.register(createSoundEvent("coven_scarecrow_death"));
		registry.register(createSoundEvent("coven_scarecrow_hurt"));
		registry.register(createSoundEvent("coven_witch_attack"));
		registry.register(createSoundEvent("coven_witch_death"));
		registry.register(createSoundEvent("coven_witch_hurt"));
		registry.register(createSoundEvent("dwarf_attack"));
		registry.register(createSoundEvent("dwarf_death"));
		registry.register(createSoundEvent("dwarf_hurt"));
		registry.register(createSoundEvent("elf_attack"));
		registry.register(createSoundEvent("elf_death"));
		registry.register(createSoundEvent("elf_hurt"));
		registry.register(createSoundEvent("ent_attack"));
		registry.register(createSoundEvent("ent_death"));
		registry.register(createSoundEvent("ent_hurt"));
		registry.register(createSoundEvent("gargoyle_attack"));
		registry.register(createSoundEvent("gargoyle_death"));
		registry.register(createSoundEvent("gargoyle_hurt"));
		registry.register(createSoundEvent("giant_attack"));
		registry.register(createSoundEvent("giant_death"));
		registry.register(createSoundEvent("giant_hurt"));
		registry.register(createSoundEvent("gnome_attack"));
		registry.register(createSoundEvent("gnome_death"));
		registry.register(createSoundEvent("gnome_hurt"));
		registry.register(createSoundEvent("gremlin_attack"));
		registry.register(createSoundEvent("gremlin_death"));
		registry.register(createSoundEvent("gremlin_hurt"));
		registry.register(createSoundEvent("hobbit_attack"));
		registry.register(createSoundEvent("hobbit_death"));
		registry.register(createSoundEvent("hobbit_hurt"));
		registry.register(createSoundEvent("human_attack"));
		registry.register(createSoundEvent("human_death"));
		registry.register(createSoundEvent("human_female_attack"));
		registry.register(createSoundEvent("human_female_death"));
		registry.register(createSoundEvent("human_female_hurt"));
		registry.register(createSoundEvent("human_hurt"));
		registry.register(createSoundEvent("ishtari_anubite_attack"));
		registry.register(createSoundEvent("ishtari_anubite_death"));
		registry.register(createSoundEvent("ishtari_anubite_hurt"));
		registry.register(createSoundEvent("ishtari_mummy_attack"));
		registry.register(createSoundEvent("ishtari_mummy_death"));
		registry.register(createSoundEvent("ishtari_mummy_hurt"));
		registry.register(createSoundEvent("ishtari_pharoah_attack"));
		registry.register(createSoundEvent("ishtari_pharoah_death"));
		registry.register(createSoundEvent("ishtari_pharoah_hurt"));
		registry.register(createSoundEvent("klown_attack"));
		registry.register(createSoundEvent("klown_death"));
		registry.register(createSoundEvent("klown_hurt"));
		registry.register(createSoundEvent("kobold_attack"));
		registry.register(createSoundEvent("kobold_death"));
		registry.register(createSoundEvent("kobold_hurt"));
		registry.register(createSoundEvent("kong_ape_attack"));
		registry.register(createSoundEvent("kong_ape_death"));
		registry.register(createSoundEvent("kong_ape_hurt"));
		registry.register(createSoundEvent("lizardman_ambient"));
		registry.register(createSoundEvent("lizardman_attack"));
		registry.register(createSoundEvent("lizardman_death"));
		registry.register(createSoundEvent("lizardman_hurt"));
		registry.register(createSoundEvent("malice_attack"));
		registry.register(createSoundEvent("malice_death"));
		registry.register(createSoundEvent("malice_hurt"));
		registry.register(createSoundEvent("monster_attack"));
		registry.register(createSoundEvent("monster_death"));
		registry.register(createSoundEvent("monster_hurt"));
		registry.register(createSoundEvent("norska_attack"));
		registry.register(createSoundEvent("norska_death"));
		registry.register(createSoundEvent("norska_hurt"));
		registry.register(createSoundEvent("orc_ambient"));
		registry.register(createSoundEvent("orc_attack"));
		registry.register(createSoundEvent("orc_death"));
		registry.register(createSoundEvent("orc_hurt"));
		registry.register(createSoundEvent("orc_uruk_attack"));
		registry.register(createSoundEvent("orc_uruk_death"));
		registry.register(createSoundEvent("orc_uruk_hurt"));
		registry.register(createSoundEvent("pirate_ambient"));
		registry.register(createSoundEvent("pirate_attack"));
		registry.register(createSoundEvent("pirate_death"));
		registry.register(createSoundEvent("pirate_hurt"));
		registry.register(createSoundEvent("skeleton_death"));
		registry.register(createSoundEvent("skeleton_hurt"));
		registry.register(createSoundEvent("vampire_attack"));
		registry.register(createSoundEvent("vampire_boss_attack"));
		registry.register(createSoundEvent("vampire_boss_death"));
		registry.register(createSoundEvent("vampire_boss_hurt"));
		registry.register(createSoundEvent("vampire_bride_attack"));
		registry.register(createSoundEvent("vampire_bride_death"));
		registry.register(createSoundEvent("vampire_bride_hurt"));
		registry.register(createSoundEvent("vampire_death"));
		registry.register(createSoundEvent("vampire_hurt"));
		registry.register(createSoundEvent("zombie_ambient"));
		registry.register(createSoundEvent("zombie_attack"));
		registry.register(createSoundEvent("zombie_death"));
		registry.register(createSoundEvent("zombie_hurt"));
		addAlternativeSoundReferences();
	}

	private static SoundEvent createSoundEvent(String soundName) {
		ResourceLocation registryName = new ResourceLocation(AncientWarfareNPC.MOD_ID, soundName);
		SoundEvent soundEvent = new SoundEvent(registryName).setRegistryName(registryName);
		npcSoundEvents.put(soundName, soundEvent);
		return soundEvent;
	}

	public static boolean isValidSound(String sound) {
		return npcSoundEvents.containsKey(sound);
	}

	public static void addAlternativeSoundReferences() {
		npcSoundEvents.put("barbarian_death", HUMAN_DEATH);
	}

	public static SoundEvent getSoundEventFromString(String name) {
		name = name.toLowerCase();
		return npcSoundEvents.get(name);
	}
}