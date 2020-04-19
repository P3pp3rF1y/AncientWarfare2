package net.shadowmage.ancientwarfare.npc.container;

import electroblob.wizardry.spell.Spell;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.util.NBTHelper;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionSpellcasterWizardry;
import net.shadowmage.ancientwarfare.npc.registry.NpcDefaultsRegistry;
import net.shadowmage.ancientwarfare.npc.skin.NpcSkinSettings;

import java.util.List;

public class ContainerNpcFactionSpellcasterWizardry extends ContainerNpcBase<NpcFactionSpellcasterWizardry> implements ISkinSettingsContainer {
	private static final String SKIN_SETTINGS_TAG = "skinSettings";
	private final List<Spell> allSpells = Spell.getAllSpells();

	private List<Spell> assignedSpells;
	private int maxHealth;
	private NpcSkinSettings skinSettings;
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
		tag.setTag(SKIN_SETTINGS_TAG, skinSettings.serializeNBT());
		tag.setInteger("maxHealth", maxHealth);
		tag.setTag("assignedSpells", NBTHelper.getTagList(assignedSpells, spell -> new NBTTagInt(spell.metadata())));
		return tag;
	}

	@Override
	public void sendInitData() {
		sendDataToClient(serializeContainerData());
	}

	@Override
	public void handlePacketData(NBTTagCompound nbt) {
		assignedSpells = NBTHelper.getList(nbt.getTagList("assignedSpells", Constants.NBT.TAG_INT),
				tag -> Spell.byMetadata(((NBTTagInt) tag).getInt()));
		maxHealth = nbt.getInteger("maxHealth");
		skinSettings = NpcSkinSettings.deserializeNBT(nbt.getCompoundTag(SKIN_SETTINGS_TAG));
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
		maxHealth = (int) NpcDefaultsRegistry.getFactionNpcDefault(entity.getFaction(), presetSubtypeName).getBaseHealth();

		// set skin
		skinSettings.setSkinType(NpcSkinSettings.SkinType.NPC_TYPE);
		skinSettings.setRandom(true);
		skinSettings.setNpcTypeName(faction + "." + presetSubtypeName);
	}

	@Override
	public void handleNpcSkinUpdate() {
		sendDataToServer(SKIN_SETTINGS_TAG, skinSettings.serializeNBT());
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


