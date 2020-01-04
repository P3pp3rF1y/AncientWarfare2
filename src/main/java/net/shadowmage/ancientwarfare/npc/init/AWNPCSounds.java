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

	public static final SoundEvent ENTITY_BARBARIAN_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_BARBARIAN_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_BEAST_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_BEAST_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_BEAST_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_BRIGAND_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_BRIGAND_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_BRIGAND_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_COVEN_DRYAD_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_COVEN_DRYAD_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_COVEN_FAMILIAR_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_COVEN_FAMILIAR_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_COVEN_FAMILIAR_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_COVEN_PUPPET_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_COVEN_PUPPET_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_COVEN_PUPPET_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_COVEN_SATYR_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_COVEN_SATYR_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_COVEN_SCARECROW_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_COVEN_SCARECROW_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_COVEN_SCARECROW_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_COVEN_WITCH_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_COVEN_WITCH_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_COVEN_WITCH_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_DWARF_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_DWARF_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_DWARF_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_ELF_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_ELF_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_ELF_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_GIANT_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_GIANT_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_GIANT_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_HUMAN_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_HUMAN_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_HUMAN_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_ISHTARI_ANUBITE_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_ISHTARI_ANUBITE_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_ISHTARI_ANUBITE_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_ISHTARI_MUMMY_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_ISHTARI_MUMMY_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_ISHTARI_MUMMY_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_ISHTARI_PHAROAH_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_ISHTARI_PHAROAH_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_ISHTARI_PHAROAH_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_KLOWN_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_KLOWN_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_KLOWN_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_KOBOLD_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_KOBOLD_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_KOBOLD_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_KONG_APE_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_KONG_APE_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_KONG_APE_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_LIZARDMAN_AMBIENT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_LIZARDMAN_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_LIZARDMAN_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_LIZARDMAN_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_MALICE_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_MALICE_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_MALICE_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_NORSKA_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_NORSKA_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_NORSKA_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_ORC_AMBIENT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_ORC_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_ORC_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_ORC_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_ORC_URUK_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_ORC_URUK_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_ORC_URUK_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_PIRATE_AMBIENT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_PIRATE_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_PIRATE_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_PIRATE_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_SKELETON_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_SKELETON_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_VAMPIRE_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_VAMPIRE_BOSS_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_VAMPIRE_BOSS_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_VAMPIRE_BOSS_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_VAMPIRE_BRIDE_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_VAMPIRE_BRIDE_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_VAMPIRE_BRIDE_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_VAMPIRE_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_HUMAN_FEMALE_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_HUMAN_FEMALE_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_HUMAN_FEMALE_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_MONSTER_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_MONSTER_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_HOBBIT_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_HOBBIT_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_GREMLIN_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_GREMLIN_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_GNOME_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_GNOME_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_ENT_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_ENT_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_GARGOYLE_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_GARGOYLE_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_GARGOYLE_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_ENT_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_GNOME_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_GREMLIN_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_HOBBIT_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_MONSTER_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_VAMPIRE_HURT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_ZOMBIE_AMBIENT = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_ZOMBIE_ATTACK = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_ZOMBIE_DEATH = InjectionTools.nullValue();
	public static final SoundEvent ENTITY_ZOMBIE_HURT = InjectionTools.nullValue();

	private AWNPCSounds() {}

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
		registry.register(createSoundEvent("entity_barbarian_attack"));
		registry.register(createSoundEvent("entity_barbarian_hurt"));
		registry.register(createSoundEvent("entity_beast_attack"));
		registry.register(createSoundEvent("entity_beast_death"));
		registry.register(createSoundEvent("entity_beast_hurt"));
		registry.register(createSoundEvent("entity_brigand_attack"));
		registry.register(createSoundEvent("entity_brigand_death"));
		registry.register(createSoundEvent("entity_brigand_hurt"));
		registry.register(createSoundEvent("entity_coven_dryad_death"));
		registry.register(createSoundEvent("entity_coven_dryad_hurt"));
		registry.register(createSoundEvent("entity_coven_familiar_attack"));
		registry.register(createSoundEvent("entity_coven_familiar_death"));
		registry.register(createSoundEvent("entity_coven_familiar_hurt"));
		registry.register(createSoundEvent("entity_coven_puppet_attack"));
		registry.register(createSoundEvent("entity_coven_puppet_death"));
		registry.register(createSoundEvent("entity_coven_puppet_hurt"));
		registry.register(createSoundEvent("entity_coven_satyr_death"));
		registry.register(createSoundEvent("entity_coven_satyr_hurt"));
		registry.register(createSoundEvent("entity_coven_scarecrow_death"));
		registry.register(createSoundEvent("entity_coven_scarecrow_attack"));
		registry.register(createSoundEvent("entity_coven_scarecrow_hurt"));
		registry.register(createSoundEvent("entity_coven_witch_death"));
		registry.register(createSoundEvent("entity_coven_witch_attack"));
		registry.register(createSoundEvent("entity_coven_witch_hurt"));
		registry.register(createSoundEvent("entity_dwarf_attack"));
		registry.register(createSoundEvent("entity_dwarf_death"));
		registry.register(createSoundEvent("entity_dwarf_hurt"));
		registry.register(createSoundEvent("entity_elf_attack"));
		registry.register(createSoundEvent("entity_elf_death"));
		registry.register(createSoundEvent("entity_elf_hurt"));
		registry.register(createSoundEvent("entity_giant_attack"));
		registry.register(createSoundEvent("entity_giant_death"));
		registry.register(createSoundEvent("entity_giant_hurt"));
		registry.register(createSoundEvent("entity_human_attack"));
		registry.register(createSoundEvent("entity_human_death"));
		registry.register(createSoundEvent("entity_human_hurt"));
		registry.register(createSoundEvent("entity_ishtari_anubite_attack"));
		registry.register(createSoundEvent("entity_ishtari_anubite_death"));
		registry.register(createSoundEvent("entity_ishtari_anubite_hurt"));
		registry.register(createSoundEvent("entity_ishtari_mummy_attack"));
		registry.register(createSoundEvent("entity_ishtari_mummy_death"));
		registry.register(createSoundEvent("entity_ishtari_mummy_hurt"));
		registry.register(createSoundEvent("entity_ishtari_pharoah_attack"));
		registry.register(createSoundEvent("entity_ishtari_pharoah_death"));
		registry.register(createSoundEvent("entity_ishtari_pharoah_hurt"));
		registry.register(createSoundEvent("entity_klown_attack"));
		registry.register(createSoundEvent("entity_klown_death"));
		registry.register(createSoundEvent("entity_klown_hurt"));
		registry.register(createSoundEvent("entity_kobold_attack"));
		registry.register(createSoundEvent("entity_kobold_death"));
		registry.register(createSoundEvent("entity_kobold_hurt"));
		registry.register(createSoundEvent("entity_kong_ape_attack"));
		registry.register(createSoundEvent("entity_kong_ape_death"));
		registry.register(createSoundEvent("entity_kong_ape_hurt"));
		registry.register(createSoundEvent("entity_lizardman_ambient"));
		registry.register(createSoundEvent("entity_lizardman_attack"));
		registry.register(createSoundEvent("entity_lizardman_death"));
		registry.register(createSoundEvent("entity_lizardman_hurt"));
		registry.register(createSoundEvent("entity_malice_attack"));
		registry.register(createSoundEvent("entity_malice_death"));
		registry.register(createSoundEvent("entity_malice_hurt"));
		registry.register(createSoundEvent("entity_norska_attack"));
		registry.register(createSoundEvent("entity_norska_death"));
		registry.register(createSoundEvent("entity_norska_hurt"));
		registry.register(createSoundEvent("entity_orc_ambient"));
		registry.register(createSoundEvent("entity_orc_attack"));
		registry.register(createSoundEvent("entity_orc_death"));
		registry.register(createSoundEvent("entity_orc_hurt"));
		registry.register(createSoundEvent("entity_orc_uruk_attack"));
		registry.register(createSoundEvent("entity_orc_uruk_death"));
		registry.register(createSoundEvent("entity_orc_uruk_hurt"));
		registry.register(createSoundEvent("entity_pirate_ambient"));
		registry.register(createSoundEvent("entity_pirate_attack"));
		registry.register(createSoundEvent("entity_pirate_death"));
		registry.register(createSoundEvent("entity_pirate_hurt"));
		registry.register(createSoundEvent("entity_skeleton_death"));
		registry.register(createSoundEvent("entity_skeleton_hurt"));
		registry.register(createSoundEvent("entity_vampire_attack"));
		registry.register(createSoundEvent("entity_vampire_boss_attack"));
		registry.register(createSoundEvent("entity_vampire_boss_death"));
		registry.register(createSoundEvent("entity_vampire_boss_hurt"));
		registry.register(createSoundEvent("entity_vampire_bride_attack"));
		registry.register(createSoundEvent("entity_vampire_bride_death"));
		registry.register(createSoundEvent("entity_vampire_bride_hurt"));
		registry.register(createSoundEvent("entity_vampire_death"));
		registry.register(createSoundEvent("entity_human_female_death"));
		registry.register(createSoundEvent("entity_human_female_attack"));
		registry.register(createSoundEvent("entity_monster_death"));
		registry.register(createSoundEvent("entity_monster_attack"));
		registry.register(createSoundEvent("entity_hobbit_death"));
		registry.register(createSoundEvent("entity_hobbit_attack"));
		registry.register(createSoundEvent("entity_gremlin_death"));
		registry.register(createSoundEvent("entity_gremlin_attack"));
		registry.register(createSoundEvent("entity_gnome_death"));
		registry.register(createSoundEvent("entity_gnome_attack"));
		registry.register(createSoundEvent("entity_ent_death"));
		registry.register(createSoundEvent("entity_ent_attack"));
		registry.register(createSoundEvent("entity_gargoyle_death"));
		registry.register(createSoundEvent("entity_gargoyle_attack"));
		registry.register(createSoundEvent("entity_gargoyle_hurt"));
		registry.register(createSoundEvent("entity_ent_hurt"));
		registry.register(createSoundEvent("entity_gnome_hurt"));
		registry.register(createSoundEvent("entity_gremlin_hurt"));
		registry.register(createSoundEvent("entity_hobbit_hurt"));
		registry.register(createSoundEvent("entity_monster_hurt"));
		registry.register(createSoundEvent("entity_human_female_hurt"));
		registry.register(createSoundEvent("entity_vampire_hurt"));
		registry.register(createSoundEvent("entity_zombie_ambient"));
		registry.register(createSoundEvent("entity_zombie_attack"));
		registry.register(createSoundEvent("entity_zombie_death"));
		registry.register(createSoundEvent("entity_zombie_hurt"));
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
		npcSoundEvents.put("entity_barbarian_death", ENTITY_HUMAN_DEATH);
	}

	public static SoundEvent getSoundEventFromString(String name) {
		name = name.toLowerCase();
		return npcSoundEvents.get(name);
	}
}