package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.shadowmage.ancientwarfare.core.interfaces.ISinger;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.SongPlayData;

import java.util.List;

public class TileSoundBlock extends TileUpdatable implements ISinger, ITickable{

    private boolean playing = false;//if currently playing a tune.
    private boolean redstoneInteraction = false;
    private int currentDelay;//the current cooldown delay.  if not playing, this delay will be incremented before attempting to start next song
    private int tuneIndex = -1;//the index of the song being played / to play, incremented/updated on songStart()
    private int playerCheckDelay;//used to not check for players -every- tick. checks every 10 ticks
    private int playerRange = 20;
    private int playTime;//tracking current play time.  when this exceeds length, cooldown delay is triggered
    private SongPlayData tuneData;
    private Block blockCache;

    public TileSoundBlock() {
        tuneData = new SongPlayData();
    }

    @Override
    public void update() {
        if (world.isRemote) {
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
                        AxisAlignedBB aabb = new AxisAlignedBB(pos, pos.add(1, 1, 1)).expand(playerRange, playerRange, playerRange);

                        List<EntityPlayer> list = world.getEntitiesWithinAABB(EntityPlayer.class, aabb);
                        if (list != null && !list.isEmpty()) {
                            startSong();
                        }
                    }
                } else if (isRedstoneInteraction()) {
                    if (world.getStrongPower(pos) > 0) {
                        startSong();
                    }
                } else {
                    startSong();
                }
            }
        }
    }

    private void startSong() {
        if (tuneData.getIsRandom()) {
            tuneIndex = 0;
            if (tuneData.size() > 0) {
                tuneIndex = world.rand.nextInt(tuneData.size());
            }
        } else {
            tuneIndex++;
        }
        if (tuneIndex >= tuneData.size()) {
            tuneIndex = 0;
        }
        if (tuneData.size() <= 0) {
            return;
        }
        playing = true;
        playTime = tuneData.get(tuneIndex).play(world, pos);
    }

    private void endSong() {
        playing = false;
        playTime = 0;
        currentDelay = tuneData.getMinDelay();
        int diff = tuneData.getMaxDelay() - currentDelay;
        if (diff > 0) {
            currentDelay += world.rand.nextInt(diff);
        }
    }

    @Override
    protected void writeUpdateNBT(NBTTagCompound tag) {
        super.writeUpdateNBT(tag);
        cacheToNBT(tag);
    }

    @Override
    protected void handleUpdateNBT(NBTTagCompound tag) {
        super.handleUpdateNBT(tag);
        cacheFromNBT(tag);
    }

    private void cacheFromNBT(NBTTagCompound tag){
        String id = tag.getString("block");
        if(!id.isEmpty()){
            blockCache = Block.getBlockFromName(id);
        }
    }

    private void cacheToNBT(NBTTagCompound tag){
        if(blockCache!=null){
            tag.setString("block", blockCache.getRegistryName().toString());
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        tuneData.readFromNBT(tag.getCompoundTag("tuneData"));
        redstoneInteraction = tag.getBoolean("redstone");
        tuneIndex = tag.getInteger("tuneIndex");
        playerRange = tag.getInteger("range");
        cacheFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("tuneData", tuneData.writeToNBT(new NBTTagCompound()));
        tag.setBoolean("redstone", redstoneInteraction);
        tag.setInteger("tuneIndex", tuneIndex);
        tag.setInteger("range", playerRange);
        cacheToNBT(tag);

        return tag;
    }

    public SongPlayData getSongs() {
        return tuneData;
    }

    public boolean isRedstoneInteraction() {
        return redstoneInteraction;
    }

    public void setRedstoneInteraction(boolean redstoneInteraction) {
        this.redstoneInteraction = redstoneInteraction;
    }

    public void setPlayerRange(int value){
        playerRange = value;
    }

    public int getPlayerRange(){
        return playerRange;
    }

    public Block getBlockCache(){
        return blockCache;
    }

    public void setBlockCache(ItemStack itemStack){
        blockCache = Block.getBlockFromItem(itemStack.getItem());
        BlockTools.notifyBlockUpdate(this);
        world.notifyNeighborsRespectDebug(pos, this.blockType, true);
        markDirty();
    }
}
