package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class ContainerNpcCreativeControls extends ContainerNpcBase<NpcBase> {

    public String ownerName;//allow for editing owner name for player-owned, no effect on faction-owned
    public boolean wander;//temp flag in all npcs
    public int maxHealth;
    public int attackDamage;//faction based only
    public int armorValue;//faction based only
    public String customTexRef;//might as well allow for player-owned as well...

    private boolean hasChanged;//if set to true, will set all flags to entity on container close

    public ContainerNpcCreativeControls(EntityPlayer player, int x, int y, int z) {
        super(player, x);
        ownerName = entity.getOwnerName();
        customTexRef = entity.getCustomTex();
        wander = entity.getIsAIEnabled();
        maxHealth = entity.getMaxHealthOverride();
        attackDamage = entity.getAttackDamageOverride();
        armorValue = entity.getArmorValueOverride();
    }

    public void sendChangesToServer() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("ownerName", ownerName);
        tag.setString("customTex", customTexRef);
        tag.setBoolean("wander", wander);
        tag.setInteger("maxHealth", maxHealth);
        tag.setInteger("attackDamage", attackDamage);
        tag.setInteger("armorValue", armorValue);
        sendDataToServer(tag);
    }

    @Override
    public void sendInitData() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("ownerName", ownerName);
        tag.setString("customTex", customTexRef);
        tag.setBoolean("wander", wander);
        tag.setInteger("maxHealth", maxHealth);
        tag.setInteger("attackDamage", attackDamage);
        tag.setInteger("armorValue", armorValue);
        sendDataToClient(tag);
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("ownerName")) {
            ownerName = tag.getString("ownerName");
        }
        if (tag.hasKey("wander")) {
            wander = tag.getBoolean("wander");
        }
        if (tag.hasKey("attackDamage")) {
            attackDamage = tag.getInteger("attackDamage");
        }
        if (tag.hasKey("armorValue")) {
            armorValue = tag.getInteger("armorValue");
        }
        if (tag.hasKey("maxHealth")) {
            maxHealth = tag.getInteger("maxHealth");
        }
        if (tag.hasKey("customTex")) {
            customTexRef = tag.getString("customTex");
        }
        hasChanged = true;
        refreshGui();
    }

    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        if (hasChanged && !player.world.isRemote) {
            hasChanged = false;
            entity.setOwnerName(ownerName);
            entity.setCustomTexRef(customTexRef);
            entity.setAttackDamageOverride(attackDamage);
            entity.setArmorValueOverride(armorValue);
            entity.setIsAIEnabled(wander);
            entity.setMaxHealthOverride(maxHealth);
        }
        super.onContainerClosed(par1EntityPlayer);
    }

}
