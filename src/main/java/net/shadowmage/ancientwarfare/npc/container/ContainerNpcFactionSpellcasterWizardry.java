package net.shadowmage.ancientwarfare.npc.container;

import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.NBTExtras;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionSpellcasterWizardry;
import net.shadowmage.ancientwarfare.npc.registry.NpcDefaultsRegistry;
import net.shadowmage.ancientwarfare.npc.skin.NpcSkinSettings;

import java.util.List;

public class ContainerNpcFactionSpellcasterWizardry extends ContainerNpcBase<NpcFactionSpellcasterWizardry> implements ISkinSettingsContainer {

	private final List<Spell> allSpells = Spell.getAllSpells();

	private List<Spell> assignedSpells;
	public int maxHealth;
	public NpcSkinSettings skinSettings;
	private boolean hasChanged; //if set to true, will set all flags to entity on container close

	public ContainerNpcFactionSpellcasterWizardry(EntityPlayer player, int x, int y, int z) {
		super(player, x);
		skinSettings = entity.getSkinSettings();
		maxHealth = entity.getMaxHealthOverride();
		assignedSpells = entity.getSpells();
	}

	public void sendChangesToServer() {
		sendDataToServer(serializeContainerData());
	}

	private NBTTagCompound serializeContainerData() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("skinSettings", skinSettings.serializeNBT());
		tag.setInteger("maxHealth", maxHealth);
		tag.setTag("assignedSpells", NBTExtras.listToNBT(assignedSpells, spell -> new NBTTagInt(spell.metadata())));
		return tag;
	}

	@Override
	public void sendInitData() {
		sendDataToClient(serializeContainerData());
	}

	@Override
	public void handlePacketData(NBTTagCompound nbt) {
		assignedSpells = ((List<Spell>) NBTExtras.NBTToList(nbt.getTagList("assignedSpells", Constants.NBT.TAG_INT),
				(NBTTagInt tag) -> Spell.byMetadata(tag.getInt())));
		maxHealth = nbt.getInteger("maxHealth");
		skinSettings = NpcSkinSettings.deserializeNBT(nbt.getCompoundTag("skinSettings"));
		entity.setSkinSettings(skinSettings);
		hasChanged = true;
		refreshGui();
	}

	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer) {
		if (hasChanged && !player.world.isRemote) {
			hasChanged = false;
			entity.setSkinSettings(skinSettings.minimizeData());
			entity.setSpells(assignedSpells);
			entity.setMaxHealthOverride(maxHealth);
			//			entity.setNpcTypePreset(npcTypePreset);
		}
		super.onContainerClosed(par1EntityPlayer);
	}

	public List<Spell> getAssignedSpells() {
		return assignedSpells;
	}

	public void addSpell(Spell spell) {
		assignedSpells.add(spell);
	}

	public void removeSpell(Spell spell) {
		assignedSpells.remove(spell);
	}

	public List<Spell> getAllSpells() {
		return allSpells;
	}

	public void setNameAndPresetDefaults(String presetSubtypeName) {
		String faction = entity.getFaction();
		String nameTag = "entity.ancientwarfarenpc." + faction + "." + presetSubtypeName + "." + "name";
		entity.setCustomNameTag(I18n.translateToLocal(nameTag)); // set name
		// add each spell to entity
		String[] spells = (NpcDefaultsRegistry.getFactionNpcDefault(entity.getFaction(), presetSubtypeName).getSpells()).split(",");
		for (String spell : spells) {
				Spell spellObj = Spell.get(spell);
				addSpell(spellObj);
		}

		// set health
		int basehealth = (int) NpcDefaultsRegistry.getFactionNpcDefault(entity.getFaction(), presetSubtypeName).getBaseHealth();
		maxHealth = (int) (basehealth);

		// set skin
		skinSettings.setSkinType(NpcSkinSettings.SkinType.NPC_TYPE);
		skinSettings.setRandom(true);
		skinSettings.setNpcTypeName(faction + "." + presetSubtypeName);
	}

	@Override
	public void handleNpcSkinUpdate() {
		sendDataToServer("skinSettings", skinSettings.serializeNBT());
	}

	@Override
	public NpcSkinSettings getSkinSettings() {
		return skinSettings;
	}

	@Override
	public void setSkinSettings(NpcSkinSettings skinSettings) {
		this.skinSettings = skinSettings;
	}
}


