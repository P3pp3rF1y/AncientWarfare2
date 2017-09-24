package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;

public class FactionTrade extends Trade {

    private int refillFrequency;
    private int ticksTilRefill;
    private int maxAvailable;
    private int currentAvailable;
//TODO add minLevel stat -- used to determine if trade should be available

    public FactionTrade() {
        refillFrequency = 20 * 60 * 5;//five minutes per item refilled
        ticksTilRefill = refillFrequency;
        maxAvailable = 1;
        currentAvailable = 1;
    }

    public boolean hasItems() {
        for (int i = 0; i < size(); i++) {
            if (getInputStack(i) != null || getOutputStack(i) != null) {
                return true;
            }
        }
        return false;
    }

    public int getRefillFrequency() {
        return refillFrequency;
    }

    public int getMaxAvailable() {
        return maxAvailable;
    }

    public int getCurrentAvailable() {
        return currentAvailable;
    }

    public void setRefillFrequency(int refill) {
        refillFrequency = refill;
        ticksTilRefill = refillFrequency;
    }

    public void setMaxAvailable(int max) {
        maxAvailable = max;
        currentAvailable = max;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setInteger("refillFrequency", refillFrequency);
        tag.setInteger("ticksTilRefill", ticksTilRefill);
        tag.setInteger("maxAvailable", maxAvailable);
        tag.setInteger("currentAvailable", currentAvailable);
        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        refillFrequency = tag.getInteger("refillFrequency");
        ticksTilRefill = tag.getInteger("ticksTilRefill");
        maxAvailable = tag.getInteger("maxAvailable");
        currentAvailable = tag.getInteger("currentAvailable");
        super.readFromNBT(tag);
    }

    public void updateTrade(int ticks) {
        ticksTilRefill += ticks;
        if (refillFrequency > 0)//update per freq period
        {
            while (ticksTilRefill >= refillFrequency) {
                ticksTilRefill -= refillFrequency;
                if (currentAvailable < maxAvailable) {
                    currentAvailable++;
                }
            }
        } else if (refillFrequency == 0)//full refill automatically if frequency==0
        {
            currentAvailable = maxAvailable;
        }//dont refill if frequency<0
    }

    @Override
    public void performTrade(EntityPlayer player, IInventory storage) {
        if (currentAvailable > 0) {
            super.performTrade(player, null);
        }
    }

    @Override
    protected void doTrade(EntityPlayer player, IInventory storage) {
        if (refillFrequency != 0) {
            currentAvailable--;
        }//0 denotes instant restock, no reason to decrease qty if it will just be instantly restocked when GUI is opened next
        super.doTrade(player, storage);
    }
}
