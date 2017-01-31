package net.shadowmage.ancientwarfare.core.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;

import java.util.UUID;

public class TileOwned extends TileEntity implements IOwnable {
    private String ownerName = "";
    private UUID owner;
    private final String tagKey;
    public TileOwned() {
        tagKey = "ownerName";
    }

    public TileOwned(String tag){
        tagKey = tag;
    }

    @Override
    public final void setOwner(EntityPlayer player) {
        ownerName = player.getCommandSenderName();
        owner = player.getUniqueID();
    }
    
    @Override
    public final void setOwner(String ownerName, UUID ownerUuid) {
        this.ownerName = ownerName;
        this.owner = ownerUuid;
    }

    @Override
    public final String getOwnerName() {
        return ownerName;
    }
    
    @Override
    public final UUID getOwnerUuid() {
        return owner;
    }

    @Override
    public final boolean isOwner(EntityPlayer player) {
        if(player == null)
            return false;
        if(owner!=null)
            return player.getUniqueID().equals(owner);
        return player.getCommandSenderName().equals(ownerName);
    }

    private void checkOwnerName(){
        if(hasWorldObj()){
            if(owner!=null) {
                EntityPlayer player = worldObj.func_152378_a(owner);
                if (player != null) {
                    setOwner(player);
                }
            }else if(ownerName!=null){
                EntityPlayer player = worldObj.getPlayerEntityByName(ownerName);
                if(player!=null){
                    setOwner(player);
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        ownerName = tag.getString(tagKey);
        if(tag.hasKey("ownerId"))
            owner = UUID.fromString(tag.getString("ownerId"));
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        checkOwnerName();
        tag.setString(tagKey, ownerName);
        if(owner!=null)
            tag.setString("ownerId", owner.toString());
    }
}
