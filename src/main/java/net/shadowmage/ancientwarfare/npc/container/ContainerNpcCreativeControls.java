package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.skin.NpcSkinSettings;

public class ContainerNpcCreativeControls extends ContainerNpcBase<NpcBase> implements ISkinSettingsContainer {

	public String ownerName;//allow for editing owner name for player-owned, no effect on faction-owned
	public boolean wander;//temp flag in all npcs
	public boolean hasCustomEquipment;//faction based only
	public int maxHealth;
	public int attackDamage;//faction based only
	public int armorValue;//faction based only
	public NpcSkinSettings skinSettings;

	private boolean hasChanged;//if set to true, will set all flags to entity on container close

	public ContainerNpcCreativeControls(EntityPlayer player, int x, int y, int z) {
		super(player, x);
		ownerName = entity.getOwner().getName();
		skinSettings = entity.getSkinSettings();
		wander = entity.getIsAIEnabled();
		maxHealth = entity.getMaxHealthOverride();
		attackDamage = entity.getAttackDamageOverride();
		armorValue = entity.getArmorValueOverride();
		hasCustomEquipment = entity.getCustomEquipmentOverride();
	}

	public void sendChangesToServer() {
		sendDataToServer(serializeContainerData());
	}

	private NBTTagCompound serializeContainerData() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("ownerName", ownerName);
		tag.setTag("skinSettings", skinSettings.serializeNBT());
		tag.setBoolean("wander", wander);
		tag.setInteger("maxHealth", maxHealth);
		tag.setInteger("attackDamage", attackDamage);
		tag.setInteger("armorValue", armorValue);
		tag.setBoolean("hasCustomEquipment", hasCustomEquipment);
		return tag;
	}

	@Override
	public void sendInitData() {
		sendDataToClient(serializeContainerData());
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		ownerName = tag.getString("ownerName");
		wander = tag.getBoolean("wander");
		hasCustomEquipment = tag.getBoolean("hasCustomEquipment");
		attackDamage = tag.getInteger("attackDamage");
		armorValue = tag.getInteger("armorValue");
		maxHealth = tag.getInteger("maxHealth");
		skinSettings = NpcSkinSettings.deserializeNBT(tag.getCompoundTag("skinSettings"));
		entity.setSkinSettings(skinSettings);
		hasChanged = true;
		refreshGui();
	}

	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer) {
		if (hasChanged && !player.world.isRemote) {
			hasChanged = false;
			entity.setOwnerName(ownerName);
			entity.setSkinSettings(skinSettings.minimizeData());
			entity.setAttackDamageOverride(attackDamage);
			entity.setArmorValueOverride(armorValue);
			entity.setIsAIEnabled(wander);
			entity.setMaxHealthOverride(maxHealth);
			entity.setCustomEquipmentOverride(hasCustomEquipment);
		}
		super.onContainerClosed(par1EntityPlayer);
	}

	@Override
	public void handleNpcSkinUpdate() {
		sendDataToServer("skinSettings", skinSettings.serializeNBT());
	}

	@Override
	public NpcSkinSettings getSkinSettings() {
		return skinSettings;
	}

	public boolean isFactionNpc() {
		return entity instanceof NpcFaction;
	}

	@Override
	public void setSkinSettings(NpcSkinSettings skinSettings) {
		this.skinSettings = skinSettings;
	}
}
