package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public final class POTradePoint {
    protected BlockPos position;
    protected int delay;
    protected boolean shouldUpkeep;//if the npc should refill upkeep at this stop

    public BlockPos getPosition() {
        return position;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setShouldUpkeep(boolean val) {
        this.shouldUpkeep = val;
    }

    public boolean shouldUpkeep() {
        return shouldUpkeep;
    }

    public void readFromNBT(NBTTagCompound tag) {
        position = BlockPos.fromLong(tag.getLong("pos"));
        delay = tag.getInteger("delay");
        shouldUpkeep = tag.getBoolean("upkeep");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setLong("pos", position.toLong());
        tag.setInteger("delay", delay);
        tag.setBoolean("upkeep", shouldUpkeep);
        return tag;
    }

}
