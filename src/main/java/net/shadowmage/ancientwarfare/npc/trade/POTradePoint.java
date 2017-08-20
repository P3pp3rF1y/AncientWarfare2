package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.nbt.NBTTagCompound;

public final class POTradePoint {
    protected BlockPos position = new BlockPos();
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
        position = new BlockPos(tag.getCompoundTag("pos"));
        delay = tag.getInteger("delay");
        shouldUpkeep = tag.getBoolean("upkeep");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setTag("pos", position.writeToNBT(new NBTTagCompound()));
        tag.setInteger("delay", delay);
        tag.setBoolean("upkeep", shouldUpkeep);
        return tag;
    }

}
