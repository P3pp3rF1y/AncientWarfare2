package net.shadowmage.ancientwarfare.core.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.util.EntityTools;

import java.util.UUID;

public class TileOwned extends TileUpdatable implements IOwnable {
    private String ownerName = "";
    private UUID ownerId;
    private final String tagKey;
    public TileOwned() {
        tagKey = "ownerName";
    }

    public TileOwned(String tag){
        tagKey = tag;
    }

    @Override
    public final void setOwner(EntityPlayer player) {
        ownerName = player.getName();
        ownerId = player.getUniqueID();
    }
    
    @Override
    public final void setOwner(String ownerName, UUID ownerUuid) {
        this.ownerName = ownerName;
        this.ownerId = ownerUuid;
    }

    @Override
    public final String getOwnerName() {
        return ownerName;
    }
    
    @Override
    public final UUID getOwnerUuid() {
        return ownerId;
    }

    @Override
    public final boolean isOwner(EntityPlayer player) {
        return EntityTools.isOwnerOrSameTeam(player, ownerId, ownerName);
    }

    @Override
    protected void writeUpdateNBT(NBTTagCompound tag) {
        tag.setString(tagKey, ownerName);
        if(ownerId !=null)
            tag.setString("ownerId", ownerId.toString());
    }

    @Override
    protected void handleUpdateNBT(NBTTagCompound tag) {
        ownerName = tag.getString(tagKey);
        if(tag.hasKey("ownerId"))
            ownerId = UUID.fromString(tag.getString("ownerId"));
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        ownerName = tag.getString(tagKey);
        if(tag.hasKey("ownerId"))
            ownerId = UUID.fromString(tag.getString("ownerId"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setString(tagKey, ownerName);
        if(ownerId !=null)
            tag.setString("ownerId", ownerId.toString());
        return tag;
    }
}
