package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;
import net.shadowmage.ancientwarfare.core.util.SongPlayData;
import net.shadowmage.ancientwarfare.structure.tile.TileSoundBlock;

public class ContainerSoundBlock extends ContainerTileBase<TileSoundBlock> {

    public SongPlayData data;
    public boolean redstoneInteraction;
    public int range;

    public ContainerSoundBlock(EntityPlayer player, int x, int y, int z) {
        super(player, x, y, z);
        data = tileEntity.getSongs();
        redstoneInteraction = tileEntity.isRedstoneInteraction();
        range = tileEntity.getPlayerRange();
    }

    @Override
    public void sendInitData() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("tuneData", data.writeToNBT(new NBTTagCompound()));
        tag.setBoolean("redstone", redstoneInteraction);
        tag.setInteger("range", range);
        sendDataToClient(tag);
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("tuneData")) {
            tileEntity.getSongs().readFromNBT(tag.getCompoundTag("tuneData"));
            data = tileEntity.getSongs();
        }
        redstoneInteraction = tag.getBoolean("redstone");
        tileEntity.setRedstoneInteraction(redstoneInteraction);
        range = tag.getInteger("range");
        tileEntity.setPlayerRange(range);
        if(!tileEntity.getWorldObj().isRemote){
            tileEntity.markDirty();
        }
        refreshGui();
    }

    public void sendTuneDataToServer(EntityPlayer player) {
        if (player.worldObj.isRemote)//handles sending new/updated/changed data back to server on GUI close.  the last GUI to close will be the one whose data 'sticks'
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setTag("tuneData", data.writeToNBT(new NBTTagCompound()));
            tag.setBoolean("redstone", redstoneInteraction);
            tag.setInteger("range", range);
            sendDataToServer(tag);
        }
    }
}
