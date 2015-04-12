package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.shadowmage.ancientwarfare.core.util.SongPlayData;
import net.shadowmage.ancientwarfare.core.util.SongPlayData.SongEntry;

import java.util.List;

public class TileSoundBlock extends TileEntity {

    private boolean playing = false;//if currently playing a tune.
    private boolean redstoneInteraction = false;
    private int currentDelay;//the current cooldown delay.  if not playing, this delay will be incremented before attempting to start next song
    private int tuneIndex = -1;//the index of the song being played / to play, incremented/updated on songStart()
    private int playerCheckDelay;//used to not check for players -every- tick. checks every 10 ticks
    private int playerRange = 20;
    private int playTime;//tracking current play time.  when this exceeds length, cooldown delay is triggered
    private SongPlayData tuneData;

    public TileSoundBlock() {
        tuneData = new SongPlayData();
    }

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) {
            return;
        }
        if (playing) {
            if (playTime-- <= 0) {
                endSong();
            }
        } else {
            if (currentDelay-- <= 0) {
                if (tuneData.getPlayOnPlayerEntry()) {
                    if (playerCheckDelay-- <= 0) {
                        playerCheckDelay = 20;
                        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1).expand(playerRange, playerRange, playerRange);
                        @SuppressWarnings("unchecked")
                        List<EntityPlayer> list = worldObj.getEntitiesWithinAABB(EntityPlayer.class, aabb);
                        if (list != null && !list.isEmpty()) {
                            startSong();
                        }
                    }
                } else if (isRedstoneInteraction()) {
                    if (worldObj.getBlockPowerInput(xCoord, yCoord, zCoord) > 0) {
                        startSong();
                    }
                } else {
                    startSong();
                }
            }
        }
    }

    private void startSong() {
        playing = true;
        playTime = 0;
        if (tuneData.getIsRandom()) {
            tuneIndex = 0;
            if (tuneData.size() > 0) {
                tuneIndex = worldObj.rand.nextInt(tuneData.size());
            }
        } else {
            if (tuneIndex++ >= tuneData.size()) {
                tuneIndex = 0;
            }
        }
        if (tuneData.size() <= 0) {
            return;
        }
        playTime = tuneData.get(tuneIndex).play(worldObj, xCoord, yCoord, zCoord);
    }

    private void endSong() {
        playing = false;
        playTime = 0;
        currentDelay = tuneData.getMinDelay();
        int diff = tuneData.getMaxDelay() - currentDelay;
        if (diff > 0) {
            currentDelay += worldObj.rand.nextInt(diff);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        tuneData.readFromNBT(tag.getCompoundTag("tuneData"));
        redstoneInteraction = tag.getBoolean("redstone");
        tuneIndex = tag.getInteger("tuneIndex");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("tuneData", tuneData.writeToNBT(new NBTTagCompound()));
        tag.setBoolean("redstone", redstoneInteraction);
        tag.setInteger("tuneIndex", tuneIndex);
    }

    public SongPlayData getTuneData() {
        return tuneData;
    }

    public boolean isRedstoneInteraction() {
        return redstoneInteraction;
    }

    public void setRedstoneInteraction(boolean redstoneInteraction) {
        this.redstoneInteraction = redstoneInteraction;
    }

}
