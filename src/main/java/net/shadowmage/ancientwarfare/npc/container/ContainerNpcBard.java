package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.util.SongPlayData;
import net.shadowmage.ancientwarfare.npc.entity.NpcBard;

public class ContainerNpcBard extends ContainerNpcBase<NpcBard> {

    public final SongPlayData data;

    public ContainerNpcBard(EntityPlayer player, int x, int y, int z) {
        super(player, x);
        data = entity.getSongs();
    }

    @Override
    public void sendInitData() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("tuneData", data.writeToNBT(new NBTTagCompound()));
        sendDataToClient(tag);
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("tuneData")) {
            data.readFromNBT(tag.getCompoundTag("tuneData"));
        }
        refreshGui();
    }

    public void sendTuneDataToServer() {
        if (player.world.isRemote)//handles sending new/updated/changed data back to server on GUI close.  the last GUI to close will be the one whose data 'sticks'
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setTag("tuneData", data.writeToNBT(new NBTTagCompound()));
            sendDataToServer(tag);
        }
    }

}
